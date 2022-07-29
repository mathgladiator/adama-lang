/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.support;

import org.adamalang.common.DefaultCopyright;
import org.adamalang.rxhtml.Feedback;
import org.adamalang.rxhtml.RxHtmlTool;
import org.adamalang.support.testgen.TestClass;
import org.adamalang.support.testgen.TestForge;
import org.jsoup.nodes.Element;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import java.util.regex.Pattern;

public class GenerateTemplateTests {
  public static void generate(int argOffset, final String[] args) throws Exception {
    String inputRootPath = "./test_templates";
    String outputJavaPath = "./src/test/java/org/adamalang/rxhtml";
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
      final var outRoot = new File(outputJavaPath);
      for (File file : root.listFiles()) {
        if (!file.getName().endsWith(".rx.html")) {
          continue;
        }
        System.out.println("\u001b[36mTemplate:\u001b[0m" + file.getName());
        StringBuilder issues = new StringBuilder();
        Feedback feedback = (element, warning) -> {
          System.out.println("  " + warning);
          issues.append("WARNING:").append(warning).append("\n");
        };
        String gold = RxHtmlTool.convertFilesToTemplateForest(Collections.singletonList(file), feedback);
        String name = file.getName().substring(0, file.getName().length() - 8).replace(Pattern.quote("."), "_");
        name = name.substring(0, 1).toUpperCase(Locale.ROOT) + name.substring(1);
        String classname = "Template" + name + "Tests";
        StringBuilder output = new StringBuilder();
        output.append(DefaultCopyright.COPYRIGHT_FILE_PREFIX);
        output.append("package org.adamalang.rxhtml;\n\n");
        output.append("public class ").append(classname).append(" extends BaseRxHtmlTest {\n");
        output.append("  @Override\n");
        output.append("  public String issues() {\n");
        TestClass.writeStringBuilder(issues.toString(), output, "issues");
        output.append("    return issues.toString();\n");
        output.append("  }\n");
        output.append("  @Override\n");
        output.append("  public String gold() {\n");
        TestClass.writeStringBuilder(gold, output, "gold");
        output.append("    return gold.toString();\n");
        output.append("  }\n");
        output.append("  @Override\n");
        output.append("  public String source() {\n");
        TestClass.writeStringBuilder(Files.readString(file.toPath()), output, "source");
        output.append("    return source.toString();\n");
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
}
