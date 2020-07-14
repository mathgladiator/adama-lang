/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.translator;

import org.adamalang.translator.env.CompilerOptions;
import org.adamalang.translator.env.EnvironmentState;
import org.adamalang.translator.env.GlobalObjectPool;
import org.adamalang.translator.tree.Document;
import org.adamalang.translator.tree.common.DocumentPosition;

import java.io.File;
import java.nio.file.Files;

public class AdamaC {
  public static void main(final String[] args) throws Exception {
    if (args.length == 0) {
      CompilerOptions.help(System.err);
      return;
    }
    final CompilerOptions options = CompilerOptions.start().args(0, args).make();
    final Document document = new Document();
    final GlobalObjectPool globals = GlobalObjectPool.createPoolWithStdLib();
    final EnvironmentState state = new EnvironmentState(globals, options);
    for (String search : options.searchPaths) {
      document.addSearchPath(new File(search));
    }
    for (String input : options.inputFiles) {
      document.importFile(input, DocumentPosition.ZERO);
    }
    document.setClassName(options.className);
    document.check(state);
    String java = document.compileJava(state);
    if (options.outputFile != null) {
      Files.writeString(new File(options.outputFile).toPath(), java);
    } else {
      System.out.println(java);
    }
  }
}
