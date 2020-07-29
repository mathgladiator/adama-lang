/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.translator;

import java.io.File;
import java.nio.file.Files;
import org.adamalang.translator.env.CompilerOptions;
import org.adamalang.translator.env.EnvironmentState;
import org.adamalang.translator.env.GlobalObjectPool;
import org.adamalang.translator.tree.Document;
import org.adamalang.translator.tree.common.DocumentPosition;

public class AdamaC {
  public static void main(final String[] args) throws Exception {
    if (args.length == 0) {
      CompilerOptions.help(System.err);
      return;
    }
    final var options = CompilerOptions.start().args(0, args).make();
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
  }
}
