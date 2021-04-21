/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * which is in the root directory of the repository. This file is part of the 'Adama'
 * project which is a programming language and document store for board games.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2021 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang;

import java.io.File;
import java.io.PrintStream;
import java.nio.file.Files;

import org.adamalang.support.GenerateErrorCSV;
import org.adamalang.support.GenerateLanguageTests;
import org.adamalang.translator.env.CompilerOptions;
import org.adamalang.translator.env.EnvironmentState;
import org.adamalang.translator.env.GlobalObjectPool;
import org.adamalang.translator.tree.Document;
import org.adamalang.translator.tree.common.DocumentPosition;

public class AdamaC {
  public static void help(final PrintStream output) {
    output.println("Translate an adama.g file into a giant Java class");
    output.println("---------------------------------------------------------------------------------");
    output.println("java -jar adamac.jar translate");
    output.println("  --package $string          | set the java package");
    output.println("  --class $string            | set the class name");
    output.println("  --input $file              | add the file to the generation");
    output.println("  --output $file             | set the output file name");
    output.println("  --code-coverage true/false | enable/disable code tracing and coverage");
    output.println("                             | turning code coverage off saves memory ");
    output.println("  --silent true/false        | enable/disable stderr logging for translator");
    output.println("  --goodwill-budget $int     | set how long the file can run until it terminates");
    output.println();
    output.println("Generate internal tests from scripts");
    output.println("---------------------------------------------------------------------------------");
    output.println("java -jar adamac.jar generate-tests");
    output.println("  --input $path             | set the input path where test files are located");
    output.println("  --output $path            | set the path where the tests will be generated");
    output.println();
  }

  public static void main(final String[] args) throws Exception {
    if (args.length == 0) {
      help(System.err);
      return;
    }
    if ("translate".equals(args[0])) {
      final var options = CompilerOptions.start().args(1, args).make();
      final var document = new Document();
      final var globals = GlobalObjectPool.createPoolWithStdLib();
      final var state = new EnvironmentState(globals, options);
      for (final String search : options.searchPaths) {
        document.addSearchPath(new File(search));
      }
      for (final String input : options.inputFiles) {
        document.importFile(input, DocumentPosition.ZERO);
      }
      document.setClassName(options.className);
      document.check(state);
      final var java = document.compileJava(state);
      if (options.outputFile != null) {
        Files.writeString(new File(options.outputFile).toPath(), java);
      } else {
        System.out.println(java);
      }
      return;
    }
    if ("generate-tests".equals(args[0])) {
      GenerateLanguageTests.generate(1, args);
      GenerateErrorCSV.main(null);
    }
  }
}
