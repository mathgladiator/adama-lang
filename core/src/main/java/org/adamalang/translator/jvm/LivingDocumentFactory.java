/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.translator.jvm;

import java.lang.reflect.Constructor;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;

import org.adamalang.runtime.ErrorCodes;
import org.adamalang.runtime.LivingDocument;
import org.adamalang.runtime.contracts.DocumentMonitor;
import org.adamalang.runtime.exceptions.ErrorCodeException;
import org.adamalang.runtime.natives.NtClient;
import org.adamalang.runtime.ops.TestReportBuilder;
import org.adamalang.runtime.stdlib.Utility;

/** responsible for compiling java code into a LivingDocumentFactory */
public class LivingDocumentFactory {
  private final Constructor<?> constructor;
  public final String reflection;

  public LivingDocumentFactory(final String className, final String javaSource, String reflection) throws ErrorCodeException {
    final var compiler = ToolProvider.getSystemJavaCompiler();
    final var diagnostics = new DiagnosticCollector<JavaFileObject>();
    final var fileManager = new ByteArrayJavaFileManager(compiler.getStandardFileManager(null, null, null));
    final var task = compiler.getTask(null, fileManager, diagnostics, null, null, ByteArrayJavaFileManager.turnIntoCompUnits(className + ".java", javaSource));
    if (task.call() == false) {
      for (final Diagnostic<?> diagnostic : diagnostics.getDiagnostics()) {
        System.err.println(diagnostic.toString());
      }
      throw new ErrorCodeException(ErrorCodes.E3_FACTORY_CANT_COMPILE_JAVA_CODE);
    }
    try {
      final var classBytes = fileManager.getClasses();
      fileManager.close();
      final var loader = new ByteArrayClassLoader(classBytes);
      final Class<?> clazz = Class.forName(className, true, loader);
      constructor = clazz.getConstructor(DocumentMonitor.class);
      this.reflection = reflection;
    } catch (final Exception ex) {
      throw new ErrorCodeException(ErrorCodes.E3_FACTORY_CANT_BIND_JAVA_CODE, ex);
    }
  }

  public LivingDocument create(final DocumentMonitor monitor) throws ErrorCodeException {
    try {
      return (LivingDocument) constructor.newInstance(monitor);
    } catch (final Exception ex) {
      throw new ErrorCodeException(ErrorCodes.E3_FACTORY_CANT_CREATE_OBJECT_DUE_TO_EXCEPTION, ex);
    }
  }

  public void populateTestReport(final TestReportBuilder report, final DocumentMonitor monitor, final String entropy) throws Exception {
    var candidate = prepareTestCandidate(monitor, entropy);
    final var tests = candidate.__getTests();
    for (final String test : tests) {
      report.annotate(test, Utility.parseJsonObject(candidate.__run_test(report, test)));
      candidate = prepareTestCandidate(monitor, entropy);
    }
  }

  private LivingDocument prepareTestCandidate(final DocumentMonitor monitor, final String entropy) throws Exception {
    final var candidate = create(monitor);
    final var consRequest = Utility.createObjectNode();
    consRequest.put("command", "construct");
    consRequest.put("timestamp", "0");
    consRequest.put("entropy", entropy);
    NtClient.NO_ONE.dump(consRequest.putObject("who"));
    consRequest.set("arg", Utility.createObjectNode());
    candidate.__transact(consRequest.toString());
    return candidate;
  }
}
