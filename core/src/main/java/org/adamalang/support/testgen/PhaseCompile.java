/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.support.testgen;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.adamalang.translator.jvm.LivingDocumentFactory;

public class PhaseCompile {
  public static LivingDocumentFactory go(final String className, final String java, final StringBuilder outputFile) throws Exception {
    final var memoryResultsCompiler = new ByteArrayOutputStream();
    final var ps = new PrintStream(memoryResultsCompiler);
    final var oldErr = System.err;
    System.setErr(ps);
    LivingDocumentFactory factory = null;
    try {
      outputFile.append("--JAVA COMPILE RESULTS-----------------------------").append("\n");
      factory = new LivingDocumentFactory(className, java, "{}");
    } finally {
      ps.flush();
      System.setErr(oldErr);
      outputFile.append(new String(memoryResultsCompiler.toByteArray()));
    }
    return factory;
  }
}
