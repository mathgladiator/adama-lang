/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.netty.api;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.adamalang.runtime.contracts.TimeSource;
import org.adamalang.runtime.exceptions.ErrorCodeException;
import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.logger.ObjectNodeLogger;
import org.adamalang.runtime.logger.SynchronousJsonDeltaDiskLogger;
import org.adamalang.runtime.logger.Transactor;
import org.adamalang.runtime.natives.NtClient;
import org.adamalang.runtime.stdlib.Utility;
import org.adamalang.translator.env.CompilerOptions;
import org.adamalang.translator.env.EnvironmentState;
import org.adamalang.translator.env.GlobalObjectPool;
import org.adamalang.translator.jvm.LivingDocumentFactory;
import org.adamalang.translator.tree.Document;
import org.adamalang.translator.tree.common.DocumentPosition;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class GameSpace {
  /** build the LivingDocumentFactory (i.e. the VM factory) from a file on disk */
  public static LivingDocumentFactory buildLivingDocumentFactory(final File root, final CompilerOptions options, final String file, final String className) throws ErrorCodeException {
    final var globals = GlobalObjectPool.createPoolWithStdLib();
    final var state = new EnvironmentState(globals, options);
    final var time1 = System.currentTimeMillis();
    final var document = new Document();
    document.addSearchPath(root);
    document.importFile(file, DocumentPosition.ZERO);
    document.setClassName(className);
    if (!document.check(state)) {
      final var issues = Utility.createArrayNode();
      document.writeErrorsAsLanguageServerDiagnosticArray(issues);
      if (options.stderrLoggingCompiler) {
        System.err.println(issues.toPrettyString());
      }
      throw new ErrorCodeException(ErrorCodeException.USERLAND_CANT_COMPILE_ADAMA_SCRIPT);
    }
    final var java = document.compileJava(state);
    JsonStreamWriter reflection = new JsonStreamWriter();
    document.writeTypeReflectionJson(reflection);
    final var time2 = System.currentTimeMillis();
    if (options.stderrLoggingCompiler) {
      System.err.println("PRODUCED JAVA:" + file + " [" + (time2 - time1) + " ms]");
    }
    final var factory = new LivingDocumentFactory(className, java, reflection.toString());
    final var time3 = System.currentTimeMillis();
    if (options.stderrLoggingCompiler) {
      System.err.println("COMPILED JAVA:" + file + " [" + (time3 - time2) + " ms]");
    }
    return factory;
  }

  public final String name;
  public final LivingDocumentFactory factory;
  public final HashMap<Long, Transactor> map;
  public final File root;
  public final TimeSource time;
  private final Random rng;

  public GameSpace(final String name, final LivingDocumentFactory factory, final TimeSource time, final File root) {
    this.name = name;
    this.factory = factory;
    this.time = time;
    this.root = root;
    map = new HashMap<>();
    // TODO: consider scanning for existing files, and then LOAD THEM UP
    // TODO: sync the key generation up
    rng = new Random();
  }

  /** return the reflected schema for the document */
  public String reflect() {
    return factory.reflection;
  }

  /** close the gamespace, will close all documents */
  public synchronized void close() throws Exception {
    for (final Map.Entry<Long, Transactor> entry : map.entrySet()) {
      entry.getValue().close();
    }
    map.clear();
  }

  /** generate an id to use for create (TODO: make unique and less dumb) */
  public synchronized long generate() {
    boolean tryAgain = true;
    long id = 0L;
    while (tryAgain) {
      id = Math.abs(rng.nextLong() + System.nanoTime());
      tryAgain = map.containsKey(id);
    }
    return id;
  }

  /** create a living document with the the given ID for the given person with the
   * given constructor argument and entropy */
  public synchronized Transactor create(final long id, final NtClient who, final ObjectNode cons, final String entropy) throws ErrorCodeException {
    final var file = new File(root, id + ".jsonlog");
    if (file.exists()) { throw new ErrorCodeException(ErrorCodeException.USERLAND_GAME_ALREADY_EXISTS); }
    final var state = ObjectNodeLogger.fresh();
    final var disk = SynchronousJsonDeltaDiskLogger.openFillAndAppend(file, state);
    final var transactor = new Transactor(factory, null, time, disk);
    transactor.construct(who, cons.toString(), entropy);
    map.put(id, transactor);
    return transactor;
  }

  /** get the living document by id if it exists */
  public synchronized Transactor get(final long id) throws ErrorCodeException {
    final var sm = map.get(id);
    if (sm != null) { return sm; }
    final var file = new File(root, id + ".jsonlog");
    if (!file.exists()) { return null; }
    final var state = ObjectNodeLogger.fresh();
    final var disk = SynchronousJsonDeltaDiskLogger.openFillAndAppend(file, state);
    final var transactor = new Transactor(factory, null, time, disk);
    transactor.create();
    transactor.insert(state.node.toString());
    map.put(id, transactor);
    return transactor;
  }
}
