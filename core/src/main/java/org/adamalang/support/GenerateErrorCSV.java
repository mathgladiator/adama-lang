/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * which is in the root directory of the repository. This file is part of the 'Adama'
 * project which is a programming language and document store for board games.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2021 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.support;

import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.json.JsonStreamWriter;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

public class GenerateErrorCSV {
    @SuppressWarnings("unchecked")
    public static void main(String[] _args) throws Exception {
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
                final var issues = (ArrayList<HashMap<String, Object>>) new JsonStreamReader(document.errorsJson()).readJavaTree();
                for (int j = 0; j < issues.size(); j++) {
                    writer.print(testFile.toString().replaceAll(Pattern.quote("\\"), "/"));
                    HashMap<String, Object> node = issues.get(j);
                    HashMap<String, HashMap<String, Object>> range = (HashMap<String, HashMap<String, Object>>) node.get("range");
                    writer.print(",");
                    writer.print(range.get("start").get("line").toString());
                    writer.print(",");
                    writer.print(range.get("start").get("character").toString());
                    writer.print(",");
                    writer.print(range.get("end").get("line").toString());
                    writer.print(",");
                    writer.print(range.get("end").get("character").toString());
                    writer.print(",");
                    JsonStreamWriter escaped = new JsonStreamWriter();
                    escaped.writeString(node.get("message").toString().replaceAll(Pattern.quote("\\"), "/"));
                    writer.print(escaped.toString());
                    writer.println();
                }
            }
        }
        writer.flush();
        Files.writeString(new File("./error-messages.csv").toPath(), new String(memory.toByteArray()));
    }
}
