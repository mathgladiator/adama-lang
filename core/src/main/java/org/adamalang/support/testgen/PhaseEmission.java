/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.support.testgen;

import org.adamalang.translator.parser.Parser;
import org.adamalang.translator.parser.StringBuilderDocumentHandler;
import org.adamalang.translator.parser.token.TokenEngine;

import java.nio.file.Files;
import java.nio.file.Path;

public class PhaseEmission {
  public static void go(final String filename, final Path path, final StringBuilder outputFile) throws Exception {
    outputFile.append("--EMISSION-----------------------------------------").append("\n");
    final var esb = new StringBuilderDocumentHandler();
    final var readIn = Files.readString(path);
    final var tokenEngine = new TokenEngine(filename, readIn.codePoints().iterator());
    final var parser = new Parser(tokenEngine);
    parser.document().accept(esb);
    report(readIn, esb.builder.toString(), outputFile);
  }

  public static void report(final String readIn, final String result, final StringBuilder outputFile) {
    if (!result.equals(readIn)) {
      outputFile.append("!!!Emission Failure!!!\n");
      outputFile.append("==========================================================\n");
      outputFile.append(result).append("\n");
      outputFile.append("=VERSUS===================================================\n");
      outputFile.append(readIn).append("\n");
      outputFile.append("==========================================================\n");
    } else {
      outputFile.append("Emission Success, Yay\n");
    }
  }
}
