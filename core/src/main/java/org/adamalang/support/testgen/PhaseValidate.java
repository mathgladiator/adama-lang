/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.support.testgen;

import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.translator.env.CompilerOptions;
import org.adamalang.translator.env.EnvironmentState;
import org.adamalang.translator.env.GlobalObjectPool;
import org.adamalang.translator.tree.Document;
import org.adamalang.translator.tree.common.DocumentPosition;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.regex.Pattern;

public class PhaseValidate {
  public static ValidationResults go(final Path inputRoot, final Path path, final String className, final boolean emission, final StringBuilder outputFile) throws Exception {
    final var options = CompilerOptions.start().enableCodeCoverage().make();
    final var globals = GlobalObjectPool.createPoolWithStdLib();
    final var state = new EnvironmentState(globals, options);
    final var document = new Document();
    document.addSearchPath(inputRoot.toFile());
    HashMap<String, String> inc = new HashMap<>();
    inc.put("std", "public int std_here = 123;");
    inc.put("bad", "public int;");
    document.setIncludes(inc);
    document.importFile(path.toString(), DocumentPosition.ZERO);
    document.setClassName(className);
    document.check(state.scope());
    JsonStreamWriter writer = new JsonStreamWriter();
    document.writeTypeReflectionJson(writer);
    String reflection = writer.toString();
    outputFile.append("Path:").append(path.getFileName().toString().replaceAll(Pattern.quote("\\"), "/")).append("\n");
    if (emission) {
      PhaseEmission.go(path.toString(), path, outputFile);
    }
    outputFile.append("--ISSUES-------------------------------------------").append("\n");
    final var java = document.hasErrors() ? "" : document.compileJava(state);
    String issues = document.errorsJson();
    outputFile.append(issues).append("\"");
    outputFile.append("--JAVA---------------------------------------------").append("\n");
    outputFile.append(java).append("\n");
    return new ValidationResults(issues.equals("[]") && !document.hasErrors(), java, reflection);
  }

  public static class ValidationResults {
    public final String java;
    public final boolean passedValidation;
    public final String reflection;

    public ValidationResults(final boolean passedValidation, final String java, String reflection) {
      this.passedValidation = passedValidation;
      this.java = java;
      this.reflection = reflection;
    }
  }
}
