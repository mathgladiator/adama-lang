/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.support;

import com.fasterxml.jackson.databind.JsonNode;
import org.adamalang.runtime.stdlib.Utility;
import org.adamalang.support.testgen.TestFile;
import org.adamalang.translator.env.CompilerOptions;
import org.adamalang.translator.env.EnvironmentState;
import org.adamalang.translator.env.GlobalObjectPool;
import org.adamalang.translator.tree.Document;
import org.adamalang.translator.tree.common.DocumentPosition;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.regex.Pattern;

public class GenerateErrorCSV {
    public static void main(String[] args) throws Exception {
        ByteArrayOutputStream memory = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(memory);
        final var options = CompilerOptions.start().make();
        final var globals = GlobalObjectPool.createPoolWithStdLib();
        final var state = new EnvironmentState(globals, options);
        var inputRootPath = "./test_code";
        final var root = new File(inputRootPath);
        writer.println("file,start_line,start_character,end_line,end_character,message");
        for (final File testFile : root.listFiles()) {
            final var test = TestFile.fromFilename(testFile.getName());
            if (!test.success) {
                final var document = new Document();
                document.addSearchPath(root);
                document.importFile(testFile.toString(), DocumentPosition.ZERO);
                document.setClassName("XClass");
                document.check(state);
                final var issues = Utility.createArrayNode();
                document.writeErrorsAsLanguageServerDiagnosticArray(issues);
                for (int j = 0; j < issues.size(); j++) {
                    writer.print(testFile.toString().replaceAll(Pattern.quote("\\"), "/"));
                    JsonNode node = issues.get(j);
                    writer.print(",");
                    writer.print(node.get("range").get("start").get("line").toString());
                    writer.print(",");
                    writer.print(node.get("range").get("start").get("character").toString());
                    writer.print(",");
                    writer.print(node.get("range").get("end").get("line").toString());
                    writer.print(",");
                    writer.print(node.get("range").get("end").get("character").toString());
                    writer.print(",");
                    writer.print(node.get("message").toString());
                    writer.println();
                }
            }
        }
        writer.flush();
        Files.writeString(new File("./error-messages.csv").toPath(), new String(memory.toByteArray()));
    }
}
