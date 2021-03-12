/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.support;

import java.io.File;
import java.util.Map;
import org.adamalang.support.testgen.TestClass;
import org.adamalang.support.testgen.TestForge;

public class GenerateLanguageTests {

  public static void main(String[] args) throws Exception {
    generate(0, args);
  }

  public static void generate(int argOffset, final String[] args) throws Exception {
    var inputRootPath = "./test_code";
    var outputJavaPath = "./src/test/java/org/adamalang/translator";
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
    final var root = new File(inputRootPath);
    final var classMap = TestForge.scan(root);
    final var outRoot = new File(outputJavaPath);
    for (final Map.Entry<String, TestClass> entry : classMap.entrySet()) {
      entry.getValue().finish(outRoot);
    }
  }
}
