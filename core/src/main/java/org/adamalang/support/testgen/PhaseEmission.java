/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.support.testgen;

import org.adamalang.translator.parser.Parser;
import org.adamalang.translator.parser.StringBuilderDocumentHandler;
import org.adamalang.translator.parser.token.TokenEngine;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;

public class PhaseEmission {

    public static void report(final String readIn, String result, StringBuilder outputFile) {
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

    public static void go(final String filename, final Path path, final StringBuilder outputFile) throws Exception {
        outputFile.append("--EMISSION-----------------------------------------").append("\n");
        final var esb = new StringBuilderDocumentHandler();
        final var readIn = Files.readString(path);
        final var tokenEngine = new TokenEngine(filename, readIn.codePoints().iterator());
        final var parser = new Parser(tokenEngine);
        parser.document().accept(esb);
        report(readIn, esb.builder.toString(), outputFile);
    }
}
