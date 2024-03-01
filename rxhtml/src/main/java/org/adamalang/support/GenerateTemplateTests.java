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
package org.adamalang.support;

import org.adamalang.common.DefaultCopyright;
import org.adamalang.rxhtml.Bundler;
import org.adamalang.rxhtml.RxHtmlBundle;
import org.adamalang.rxhtml.template.config.Feedback;
import org.adamalang.rxhtml.RxHtmlTool;
import org.adamalang.rxhtml.template.config.ShellConfig;

import java.io.File;
import java.nio.file.Files;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GenerateTemplateTests {
  private static final String WTF = "" + (char) 92;

  public static String fixTestGold(String gold) {
    return gold.replaceAll("/[0-9]*/devlibadama\\.js", Matcher.quoteReplacement("/DEV.js")).replaceAll("/libadama-worker\\.js/[a-z0-9.\"',]*", Matcher.quoteReplacement("/WORKER.js\",'VERSION'"));
  }

  public static class CompileStep {
    public final File input;
    public final File output;

    private CompileStep(File input, File output) {
      this.input = input;
      this.output = output;
    }
  }

  public static List<CompileStep> prepareScripts(String inputRootPath, String outputJavaPath) {
    if (isValid(inputRootPath) && isValid(outputJavaPath)) {
      ArrayList<CompileStep> steps = new ArrayList<>();
      final var root = new File(inputRootPath);
      final var scripts = new File(root, "scripts");
      final var types = new File(root, "types");
      types.mkdirs();
      for (File script : scripts.listFiles((dir, name) -> name.endsWith(".adama"))) {
        steps.add(new CompileStep(script, new File(types, script.getName().replaceAll(Pattern.quote(".adama"), Matcher.quoteReplacement(".json")))));
      }
      return steps;
    }
    return null;
  }

  public static void generate(String inputRootPath, String outputJavaPath) throws Exception {
    if (isValid(inputRootPath) && isValid(outputJavaPath)) {
      final var root = new File(inputRootPath);
      final var outRoot = new File(outputJavaPath);
      ArrayList<File> files = new ArrayList<>();
      for (File file : root.listFiles()) {
        if (!file.getName().endsWith(".rx.html") || file.isDirectory()) {
          continue;
        }
        files.add(file);
      }
      files.sort(Comparator.comparing(File::getName));
      for (File file : files) {
        boolean devMode = file.getName().startsWith("dev_");
        System.out.print("\u001b[36mTemplate:\u001b[0m" + file.getName() + "\n");
        StringBuilder issues = new StringBuilder();
        Feedback feedback = (element, warning) -> {
          System.out.print("  " + warning + "\n");
          issues.append("WARNING:").append(warning).append("\n");
        };
        RxHtmlBundle result = RxHtmlTool.convertStringToTemplateForest(Bundler.bundle(file, Collections.singletonList(file), false), new File(root, "types"), ShellConfig.start().withEnvironment("test").withVersion("GENMODE").withFeedback(feedback).withUseLocalAdamaJavascript(devMode).end());
        String gold = fixTestGold(result.toString());
        String name = file.getName().substring(0, file.getName().length() - 8).replace(Pattern.quote("."), "_");
        name = name.substring(0, 1).toUpperCase(Locale.ROOT) + name.substring(1);
        String classname = "Template" + name + "Tests";
        StringBuilder output = new StringBuilder();
        output.append(DefaultCopyright.COPYRIGHT_FILE_PREFIX);
        output.append("package org.adamalang.rxhtml;\n\n");
        output.append("public class ").append(classname).append(" extends BaseRxHtmlTest {\n");
        output.append("  @Override\n");
        output.append("  public boolean dev() {\n");
        if (devMode) {
          output.append("    return true;\n");
        } else {
          output.append("    return false;\n");
        }
        output.append("  }\n");
        output.append("  @Override\n");
        output.append("  public String issues() {\n");
        writeStringBuilder(issues.toString(), output, "issues");
        output.append("    return issues.toString();\n");
        output.append("  }\n");
        output.append("  @Override\n");
        output.append("  public String gold() {\n");
        writeStringBuilder(gold, output, "gold");
        output.append("    return gold.toString();\n");
        output.append("  }\n");
        output.append("  @Override\n");
        output.append("  public String source() {\n");
        writeStringBuilder(Files.readString(file.toPath()), output, "source");
        output.append("    return source.toString();\n");
        output.append("  }\n");
        output.append("  @Override\n");
        output.append("  public String schema() {\n");
        writeStringBuilder(result.viewSchema.toPrettyString(), output, "gold");
        output.append("    return gold.toString();\n");
        output.append("  }\n");
        output.append("}\n");
        Files.writeString(new File(outRoot, classname + ".java").toPath(), output.toString());
      }
    }
  }

  private static boolean isValid(String path) {
    File p = new File(path);
    return p.exists() && p.isDirectory();
  }

  public static void writeStringBuilder(String str, StringBuilder outputFile, String variable) {
    outputFile.append("    StringBuilder ").append(variable).append(" = new StringBuilder();\n");
    final var lines = str.split("\n");
    for (var k = 0; k < lines.length; k++) {
      lines[k] = lines[k].stripTrailing();
    }
    if (lines.length > 0) {
      outputFile.append(String.format("    " + variable + ".append(\"%s\");\n", escapeLine(lines[0])));
      for (var k = 1; k < lines.length; k++) {
        outputFile.append(String.format("    " + variable + ".append(\"\\n%s\");\n", escapeLine(lines[k])));
      }
    }
  }

  public static String escapeLine(final String line) {
    return line //
        .replaceAll(Pattern.quote(WTF), WTF + WTF + WTF + WTF) //
        .replaceAll("\"", WTF + WTF + "\"") //
        ;
  }
}
