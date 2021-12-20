/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * which is in the root directory of the repository. This file is part of the 'Adama'
 * project which is a programming language and document store for board games.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2021 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.data.disk;

import org.adamalang.ErrorCodes;
import org.adamalang.runtime.contracts.Key;
import org.adamalang.runtime.contracts.LivingDocumentFactoryFactory;
import org.adamalang.runtime.contracts.Callback;
import org.adamalang.runtime.exceptions.ErrorCodeException;
import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.translator.env.CompilerOptions;
import org.adamalang.translator.env.EnvironmentState;
import org.adamalang.translator.env.GlobalObjectPool;
import org.adamalang.translator.jvm.LivingDocumentFactory;
import org.adamalang.translator.tree.Document;
import org.adamalang.translator.tree.common.DocumentPosition;

import java.io.File;

public class FileSystemLivingDocumentFactoryFactory implements LivingDocumentFactoryFactory {
  private final File root;
  private final CompilerOptions options;
  private int idGen;

  public FileSystemLivingDocumentFactoryFactory(File root, CompilerOptions options) {
    this.root = root;
    this.options = options;
    this.idGen = 0;
  }

  public void fetch(Key key, Callback<LivingDocumentFactory> callback) {
    try {
      String className = "FSDoc_" + idGen;
      idGen++;
      final var globals = GlobalObjectPool.createPoolWithStdLib();
      final var state = new EnvironmentState(globals, options);
      final var time1 = System.currentTimeMillis();
      final var document = new Document();
      document.addSearchPath(root);
      document.importFile(key.space + ".a", DocumentPosition.ZERO);
      document.setClassName(className);
      if (!document.check(state)) {
        callback.failure(new ErrorCodeException(ErrorCodes.USERLAND_CANT_COMPILE_ADAMA_SCRIPT, document.errorsJson()));
        return;
      }
      final var java = document.compileJava(state);
      JsonStreamWriter reflection = new JsonStreamWriter();
      document.writeTypeReflectionJson(reflection);
      final var time2 = System.currentTimeMillis();
      if (options.stderrLoggingCompiler) {
        System.err.println("PRODUCED JAVA:" + key.space + " [" + (time2 - time1) + " ms]");
      }
      final var factory = new LivingDocumentFactory(className, java, reflection.toString());
      final var time3 = System.currentTimeMillis();
      if (options.stderrLoggingCompiler) {
        System.err.println("COMPILED JAVA:" + key.space + " [" + (time3 - time2) + " ms]");
      }
      callback.success(factory);
    } catch (ErrorCodeException ex) {
      callback.failure(ex);
    } catch (Throwable t) {
      callback.failure(new ErrorCodeException(ErrorCodes.USERLAND_CANT_COMPILE_ADAMA_SCRIPT, t));
    }
  }
}
