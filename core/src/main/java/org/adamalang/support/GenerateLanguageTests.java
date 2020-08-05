/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.support;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;
import org.adamalang.runtime.ops.SilentDocumentMonitor;
import org.adamalang.support.testgen.*;
import org.adamalang.translator.jvm.LivingDocumentFactory;


public class GenerateLanguageTests {

  public static void main(final String[] args) throws Exception {
    String inputRootPath = "./test_code";
    String outputJavaPath = "./src/test/java/org/adamalang/translator";
    for (int k = 0; k + 1 < args.length; k+= 2) {
      switch (args[k]) {
        case "--input":
          inputRootPath = args[k+1];
          break;
        case "--output":
          outputJavaPath = args[k+1];
          break;
        default:
          System.err.println("unknown option:" + args[k]);
      }
    }
    final var root = new File(inputRootPath);
    /*
    final var template = PhaseTemplate.inventEmpty();
    final var tests = new ArrayList<TestFile>();
    tests.add(new TestFile("Records", "IdMustBeInt", false));
    final String hackSearch = null;
    for (final TestFile test : tests) {
      final var file = new File(root, test.filename());
      final var path = file.toPath();
      if (!file.exists()) {
        Files.writeString(path, template);
      }
    }
    */

    TreeMap<String, TestClass> classMap = TestForge.scan(root);
    final var outRoot = new File(outputJavaPath);
    for (final Map.Entry<String, TestClass> entry : classMap.entrySet()) {
      entry.getValue().finish(outRoot);
    }
  }
}
