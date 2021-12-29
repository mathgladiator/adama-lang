/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.support;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.support.testgen.TestClass;
import org.adamalang.support.testgen.TestFile;
import org.adamalang.support.testgen.TestForge;
import org.adamalang.translator.env.CompilerOptions;
import org.adamalang.translator.env.EnvironmentState;
import org.adamalang.translator.env.GlobalObjectPool;
import org.adamalang.translator.tree.Document;
import org.adamalang.translator.tree.common.DocumentPosition;

public class GenerateLanguageTests {
  private static boolean isValid(String path) {
    File p = new File(path);
    return p.exists() && p.isDirectory();
  }

  public static void writeErrorCSV(String inputRootPath, String outputErrorFile) throws Exception {
    ByteArrayOutputStream memory = new ByteArrayOutputStream();
    PrintWriter writer = new PrintWriter(memory);
    final var options = CompilerOptions.start().make();
    final var globals = GlobalObjectPool.createPoolWithStdLib();
    final var state = new EnvironmentState(globals, options);
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
          writer.print(escaped);
          writer.println();
        }
      }
    }
    writer.flush();
    Files.writeString(new File(outputErrorFile).toPath(), new String(memory.toByteArray()));
  }

  public static void generate(int argOffset, final String[] args) throws Exception {
    String inputRootPath = "./test_code";
    String outputJavaPath = "./src/test/java/org/adamalang/translator";
    String outputErrorFile = "./error-messages.csv";
    for (var k = argOffset; k + 1 < args.length; k += 2) {
      switch (args[k]) {
        case "--input":
          inputRootPath = args[k + 1];
          break;
        case "--output":
          outputJavaPath = args[k + 1];
          break;
        default:
          System.err.println("unknown option:" + args[k]);
      }
    }
    if (isValid(inputRootPath) && isValid(outputJavaPath)) {
      final var root = new File(inputRootPath);
      final var classMap = TestForge.scan(root);
      final var outRoot = new File(outputJavaPath);
      for (final Map.Entry<String, TestClass> entry : classMap.entrySet()) {
        entry.getValue().finish(outRoot);
      }
      writeErrorCSV(inputRootPath, outputErrorFile);
    }
  }
}
