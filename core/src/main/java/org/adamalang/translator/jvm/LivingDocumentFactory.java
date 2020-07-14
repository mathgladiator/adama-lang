/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.translator.jvm;

import java.lang.reflect.Constructor;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;
import org.adamalang.runtime.LivingDocument;
import org.adamalang.runtime.contracts.DocumentMonitor;
import org.adamalang.runtime.natives.NtClient;
import org.adamalang.runtime.ops.TestReportBuilder;
import org.adamalang.runtime.stdlib.Utility;
import com.fasterxml.jackson.databind.node.ObjectNode;

/** responsible for compiling java code into a LivingDocumentFactory */
public class LivingDocumentFactory {
  private final Constructor<?> constructor;

  public LivingDocumentFactory(final String className, final String javaSource) throws Exception {
    final var compiler = ToolProvider.getSystemJavaCompiler();
    final var diagnostics = new DiagnosticCollector<JavaFileObject>();
    final var fileManager = new ByteArrayJavaFileManager(compiler.getStandardFileManager(null, null, null));
    final var task = compiler.getTask(null, fileManager, diagnostics, null, null, ByteArrayJavaFileManager.turnIntoCompUnits(className + ".java", javaSource));
    if (task.call() == false) {
      for (final Diagnostic<?> diagnostic : diagnostics.getDiagnostics()) {
        System.err.println(diagnostic.toString());
      }
      throw new Exception("Failed to compile");
    }
    final var classBytes = fileManager.getClasses();
    fileManager.close();
    final var loader = new ByteArrayClassLoader(classBytes);
    final Class<?> clazz = Class.forName(className, true, loader);
    constructor = clazz.getConstructor(ObjectNode.class, DocumentMonitor.class);
  }

  public LivingDocument create(final ObjectNode data, final DocumentMonitor monitor) throws Exception {
    return (LivingDocument) constructor.newInstance(data, monitor);
  }

  public void populateTestReport(final TestReportBuilder report, final DocumentMonitor monitor) throws Exception {
    var candidate = prepareTestCandidate(monitor);
    final var tests = candidate.__getTests();
    for (final String test : tests) {
      report.annotate(test, candidate.__run_test(report, test));
      candidate = prepareTestCandidate(monitor);
    }
  }

  private LivingDocument prepareTestCandidate(final DocumentMonitor monitor) throws Exception {
    final var root = Utility.createObjectNode();
    root.put("__entropy", "42");
    final var candidate = create(root, monitor);
    final var consRequest = Utility.createObjectNode();
    consRequest.put("command", "construct");
    consRequest.put("timestamp", "0");
    NtClient.NO_ONE.dump(consRequest.putObject("who"));
    consRequest.set("arg", Utility.createObjectNode());
    candidate.__transact(consRequest);
    return candidate;
  }
}
