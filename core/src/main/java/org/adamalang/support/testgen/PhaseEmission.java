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
package org.adamalang.support.testgen;

import org.adamalang.translator.env2.Scope;
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
    final var parser = new Parser(tokenEngine, Scope.makeRootDocument());
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
