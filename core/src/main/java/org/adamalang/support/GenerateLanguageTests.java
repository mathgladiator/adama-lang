/*
* Adama Platform and Language
* Copyright (C) 2021 - 2024 by Adama Platform Engineering, LLC
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
package org.adamalang.support;

import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.support.testgen.TestClass;
import org.adamalang.support.testgen.TestFile;
import org.adamalang.support.testgen.TestForge;
import org.adamalang.translator.env.CompilerOptions;
import org.adamalang.translator.env.EnvironmentState;
import org.adamalang.translator.env.GlobalObjectPool;
import org.adamalang.translator.env.RuntimeEnvironment;
import org.adamalang.translator.tree.Document;
import org.adamalang.translator.tree.common.DocumentPosition;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class GenerateLanguageTests {
  public static boolean generate(String inputRootPath, String outputJavaPath, String outputErrorFile) throws Exception {
    if (isValid(inputRootPath) && isValid(outputJavaPath)) {
      final var root = new File(inputRootPath);
      final var classMap = TestForge.scan(root);
      final var outRoot = new File(outputJavaPath);
      for (final Map.Entry<String, TestClass> entry : classMap.entrySet()) {
        entry.getValue().finish(outRoot);
      }
      writeErrorCSV(inputRootPath, outputErrorFile);
      return true;
    }
    return false;
  }

  private static boolean isValid(String path) {
    File p = new File(path);
    return p.exists() && p.isDirectory();
  }

  public static void writeErrorCSV(String inputRootPath, String outputErrorFile) throws Exception {
    ByteArrayOutputStream memory = new ByteArrayOutputStream();
    PrintWriter writer = new PrintWriter(memory);
    final var options = CompilerOptions.start().make();
    final var globals = GlobalObjectPool.createPoolWithStdLib(RuntimeEnvironment.Tooling);
    final var state = new EnvironmentState(globals, options);
    final var root = new File(inputRootPath);
    ArrayList<File> files = new ArrayList<>();
    for (File file : root.listFiles()) {
      files.add(file);
    }
    files.sort(Comparator.comparing(File::getName));
    writer.print("file,start_line,start_character,end_line,end_character,message\n");
    for (final File testFile : files) {
      final var test = TestFile.fromFilename(testFile.getName());
      if (!test.success) {
        final var document = new Document();
        document.addSearchPath(root);
        document.processMain(testFile.toString(), DocumentPosition.ZERO);
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
          writer.print("\n");
          writer.flush();
        }
      }
    }
    writer.flush();
    Files.writeString(new File(outputErrorFile).toPath(), memory.toString());
  }
}
