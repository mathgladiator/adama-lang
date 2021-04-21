/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.netty.api;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.function.Supplier;

import org.adamalang.data.disk.FileSystemDataService;
import org.adamalang.netty.ErrorCodes;
import org.adamalang.runtime.DurableLivingDocument;
import org.adamalang.runtime.contracts.Callback;
import org.adamalang.runtime.contracts.DataService;
import org.adamalang.runtime.contracts.TimeSource;
import org.adamalang.runtime.exceptions.ErrorCodeException;
import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.natives.NtClient;
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
    try {
      final var globals = GlobalObjectPool.createPoolWithStdLib();
      final var state = new EnvironmentState(globals, options);
      final var time1 = System.currentTimeMillis();
      final var document = new Document();
      document.addSearchPath(root);
      document.importFile(file + ".a", DocumentPosition.ZERO);
      document.setClassName(className);
      if (!document.check(state)) {
        if (options.stderrLoggingCompiler) {
          System.err.println(document.errorsJson());
        }
        throw new ErrorCodeException(ErrorCodes.USERLAND_CANT_COMPILE_ADAMA_SCRIPT);
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
    } catch (Throwable t) {
      t.printStackTrace();
      throw new ErrorCodeException(ErrorCodes.USERLAND_CANT_COMPILE_ADAMA_SCRIPT, t);
    }
  }

  private LivingDocumentFactory factory;

  public final String name;
  public final File root;
  public final TimeSource time;

  public final HashMap<Long, DurableLivingDocument> documents;
  public final DataService service;
  private final Supplier<LivingDocumentFactory> deployer;

  public GameSpace(final String name, final LivingDocumentFactory factory, final TimeSource time, final File root, Supplier<LivingDocumentFactory> deployer) {
    this.name = name;
    this.factory = factory;
    this.time = time;
    this.root = root;
    this.deployer = deployer;
    // TODO: consider scanning for existing files, and then LOAD THEM UP
    this.documents = new LinkedHashMap<>();
    this.service = new FileSystemDataService(root);
  }

  public LivingDocumentFactory getFactory() {
    return factory;
  }

  public void deploy() throws ErrorCodeException {
    LivingDocumentFactory newFactory = deployer.get();
    factory = newFactory;

    // TODO: accumulate the successes and failures?
    for (DurableLivingDocument document : documents.values()) {
      document.deploy(newFactory, new Callback<Integer>() {
        @Override
        public void success(Integer value) {

        }

        @Override
        public void failure(ErrorCodeException ex) {

        }
      });
    }
  }

  /** return the reflected schema for the document */
  public String reflect() {
    return factory.reflection;
  }

  /** close the gamespace, will close all documents */
  public synchronized void close() throws Exception {
    documents.clear();
  }

  /** generate and reserve an id to use for create */
  public synchronized void generate(Callback<Long> callback) {
    service.create(callback);
  }

  private synchronized boolean put(long id, DurableLivingDocument doc) {
    if (documents.containsKey(id)) {
      return false;
    }
    // TODO: recall how putIfAbsent works
    documents.put(id, doc);
    return true;
  }

  private synchronized DurableLivingDocument get(long id) {
    return documents.get(id);
  }

  /** create a living document with the the given id for the given person with the given argument and entropy */
  public void create(final long id, final NtClient who, final ObjectNode cons, final String entropy, Callback<DurableLivingDocument> callback) throws ErrorCodeException {
    DurableLivingDocument.fresh(id, factory, who, cons.toString(), entropy, null, time, service, Callback.transform(callback, 0, (doc) -> {
      if (put(id, doc)) {
        return doc;
      } else {
        throw new RuntimeException("document already exists");
      }
    }));
  }

  /** get the living document by id if it exists */
  public void get(final long id, Callback<DurableLivingDocument> callback) throws ErrorCodeException {
    {
      DurableLivingDocument doc = get(id);
      if (doc != null) {
        callback.success(doc);
        return;
      }
    }

    DurableLivingDocument.load(id, factory, null, time, service, Callback.transform(callback, 0, (doc) -> {
      put(id, doc);
      return doc;
    }));
  }
}
