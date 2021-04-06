/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.support.testgen;

import java.nio.file.Path;
import java.util.regex.Pattern;

import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.stdlib.Utility;
import org.adamalang.translator.env.CompilerOptions;
import org.adamalang.translator.env.EnvironmentState;
import org.adamalang.translator.env.GlobalObjectPool;
import org.adamalang.translator.tree.Document;
import org.adamalang.translator.tree.common.DocumentPosition;

public class PhaseValidate {
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

  public static ValidationResults go(final Path inputRoot, final Path path, final String className, final boolean emission, final StringBuilder outputFile) throws Exception {
    final var options = CompilerOptions.start().enableCodeCoverage().make();
    final var globals = GlobalObjectPool.createPoolWithStdLib();
    final var state = new EnvironmentState(globals, options);
    final var document = new Document();
    document.addSearchPath(inputRoot.toFile());
    document.importFile(path.toString(), DocumentPosition.ZERO);
    document.setClassName(className);
    document.check(state);
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
}
