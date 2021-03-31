/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.support.testgen;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.regex.Pattern;

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
      System.err.println("Begin");
      factory = new LivingDocumentFactory(className, java, "{}");
      System.err.println("End");
    } finally {
      ps.flush();
      System.setErr(oldErr);
      String[] lines = new String(memoryResultsCompiler.toByteArray()).split(Pattern.quote("\n"));
      for (String lineX : lines) {
        String line = lineX.trim();
        if (!line.contains("Error during class instrumentation") && line.length() > 0) {
          outputFile.append(line);
          outputFile.append("\n");
        }
      }
    }
    return factory;
  }
}
