/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.netty.api;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import org.adamalang.runtime.contracts.TimeSource;
import org.adamalang.runtime.exceptions.ErrorCodeException;
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
    final var time2 = System.currentTimeMillis();
    if (options.stderrLoggingCompiler) {
      System.err.println("PRODUCED JAVA:" + file + " [" + (time2 - time1) + " ms]");
    }
    final var factory = new LivingDocumentFactory(className, java);
    final var time3 = System.currentTimeMillis();
    if (options.stderrLoggingCompiler) {
      System.err.println("COMPILED JAVA:" + file + " [" + (time3 - time2) + " ms]");
    }
    return factory;
  }

  public final LivingDocumentFactory factory;
  public final HashMap<String, Transactor> map;
  public final File root;
  public final TimeSource time;

  public GameSpace(final LivingDocumentFactory factory, final TimeSource time, final File root) {
    this.factory = factory;
    this.time = time;
    this.root = root;
    map = new HashMap<>();
    // TODO: consider scanning for existing files, and then LOAD THEM UP
  }

  /** close the gamespace, will close all documents */
  public synchronized void close() throws Exception {
    for (final Map.Entry<String, Transactor> entry : map.entrySet()) {
      entry.getValue().close();
    }
    map.clear();
  }

  /** create a living document with the the given ID for the given person with the
   * given constructor argument and entropy */
  public synchronized Transactor create(final String id, final NtClient who, final ObjectNode cons, final String entropy) throws ErrorCodeException {
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
  public synchronized Transactor get(final String id) throws ErrorCodeException {
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
