/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.support.testgen;

import org.adamalang.runtime.remote.Deliverer;
import org.adamalang.translator.jvm.LivingDocumentFactory;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.regex.Pattern;

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
      factory = new LivingDocumentFactory(className, java, "{}", Deliverer.FAILURE);
      System.err.println("End");
    } finally {
      ps.flush();
      System.setErr(oldErr);
      String[] lines = memoryResultsCompiler.toString().split(Pattern.quote("\n"));
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
