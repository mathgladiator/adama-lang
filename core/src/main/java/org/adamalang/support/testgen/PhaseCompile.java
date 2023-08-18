/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.support.testgen;

import org.adamalang.runtime.remote.Deliverer;
import org.adamalang.translator.jvm.LivingDocumentFactory;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.TreeMap;
import java.util.regex.Pattern;

public class PhaseCompile {
  public static LivingDocumentFactory go(final String className, final String java, final StringBuilder outputFile) throws Exception {
    final var memoryResultsCompiler = new ByteArrayOutputStream();
    final var ps = new PrintStream(memoryResultsCompiler);
    System.setErr(ps);
    outputFile.append("--=[LivingDocumentFactory COMPILING]=---").append("\n");
    LivingDocumentFactory factory = new LivingDocumentFactory("test", className, java, "{}", Deliverer.FAILURE, new TreeMap<>());
    if (factory != null) {
      outputFile.append("--=[LivingDocumentFactory MADE]=---\n");
    }
    return factory;
  }
}
