/*
* Adama Platform and Language
* Copyright (C) 2021 - 2023 by Adama Platform Initiative, LLC
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as published
* by the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Affero General Public License for more details.
* 
* You should have received a copy of the GNU Affero General Public License
* along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
package org.adamalang.runtime.deploy;

import org.adamalang.ErrorCodes;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.keys.PrivateKeyBundle;
import org.adamalang.runtime.contracts.DocumentMonitor;
import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.remote.Deliverer;
import org.adamalang.runtime.remote.ServiceRegistry;
import org.adamalang.runtime.sys.CoreRequestContext;
import org.adamalang.translator.env.CompilerOptions;
import org.adamalang.translator.env.EnvironmentState;
import org.adamalang.translator.env.GlobalObjectPool;
import org.adamalang.translator.env2.Scope;
import org.adamalang.translator.jvm.ByteArrayClassLoader;
import org.adamalang.translator.jvm.ByteArrayJavaFileManager;
import org.adamalang.translator.jvm.LivingDocumentFactory;
import org.adamalang.translator.parser.Parser;
import org.adamalang.translator.parser.exceptions.AdamaLangException;
import org.adamalang.translator.parser.token.TokenEngine;
import org.adamalang.translator.tree.Document;
import org.adamalang.translator.tree.SymbolIndex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** the sync compiler */
public class SyncCompiler {
  private static final Logger LOGGER = LoggerFactory.getLogger(SyncCompiler.class);

  public static CachedByteCode compile(final String spaceName, final String className, final String javaSource, String reflection) throws ErrorCodeException {
    final var compiler = ToolProvider.getSystemJavaCompiler();
    final var diagnostics = new DiagnosticCollector<JavaFileObject>();
    final var fileManager = new ByteArrayJavaFileManager(compiler.getStandardFileManager(null, null, null));
    final var task = compiler.getTask(null, fileManager, diagnostics, null, null, ByteArrayJavaFileManager.turnIntoCompUnits(className + ".java", javaSource));
    if (task.call() == false) {
      StringBuilder report = new StringBuilder();
      for (final Diagnostic<?> diagnostic : diagnostics.getDiagnostics()) {
        report.append(diagnostic.toString() + "\n");
      }
      ErrorCodeException ex = new ErrorCodeException(ErrorCodes.FACTORY_CANT_COMPILE_JAVA_CODE, report.toString());
      LOGGER.error("failed-java-compile", ex);
      throw ex;
    }
    try {
      final var classBytes = fileManager.getClasses();
      fileManager.close();
      return new CachedByteCode(spaceName, className, reflection, classBytes);
    } catch (final Exception ex) {
      throw new ErrorCodeException(ErrorCodes.FACTORY_CANT_BIND_JAVA_CODE, ex);
    }
  }
}
