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
    CompilerOptions.Builder optionsBuilder = CompilerOptions.start().enableCodeCoverage();
    if (className.contains("Instrumented")) {
      optionsBuilder = optionsBuilder.instrument();
    }
    final var options = optionsBuilder.make();
    final var globals = GlobalObjectPool.createPoolWithStdLib();
    final var state = new EnvironmentState(globals, options);
    final var document = new Document();
    document.addSearchPath(inputRoot.toFile());
    HashMap<String, String> inc = new HashMap<>();
    inc.put("std", "public int std_here = 123;");
    inc.put("std/foo", "public int foo_here = 123;");
    inc.put("bad", "public int;");
    document.setIncludes(inc);
    document.processMain(path.toString(), DocumentPosition.ZERO);
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
