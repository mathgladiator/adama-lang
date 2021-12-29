/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * The 'LICENSE' file is in the root directory of the repository. Hint: it is MIT.
 * 
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
*/
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
