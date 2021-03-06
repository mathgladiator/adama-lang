/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * which is in the root directory of the repository. This file is part of the 'Adama'
 * project which is a programming language and document store for board games.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2021 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.support.testgen;

import java.nio.file.Files;
import java.nio.file.Path;
import org.adamalang.translator.parser.Parser;
import org.adamalang.translator.parser.StringBuilderDocumentHandler;
import org.adamalang.translator.parser.token.TokenEngine;

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
