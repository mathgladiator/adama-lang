/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.support;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.runtime.contracts.TransactionLogger;
import org.adamalang.runtime.contracts.TimeSource;
import org.adamalang.runtime.exceptions.DocumentRequestRejectedException;
import org.adamalang.runtime.logger.TransactionResult;
import org.adamalang.runtime.logger.ObjectNodeLogger;
import org.adamalang.runtime.logger.Transaction;
import org.adamalang.runtime.logger.Transactor;
import org.adamalang.runtime.ops.SilentDocumentMonitor;
import org.adamalang.translator.jvm.LivingDocumentFactory;
import org.adamalang.translator.parser.Parser;
import org.adamalang.translator.parser.StringBuilderDocumentHandler;
import org.adamalang.translator.parser.token.TokenEngine;
import org.adamalang.translator.tree.Document;
import org.adamalang.translator.env.CompilerOptions;
import org.adamalang.translator.tree.common.DocumentPosition;
import org.adamalang.translator.env.EnvironmentState;
import org.adamalang.translator.env.GlobalObjectPool;
import org.adamalang.runtime.exceptions.GoodwillExhaustedException;
import org.adamalang.runtime.ops.TestReportBuilder;
import org.adamalang.runtime.stdlib.Utility;
import org.adamalang.runtime.natives.NtClient;

/**
 * this class is a giant mess of stuff, and it could use a great deal of love.
 */
public class GenerateLanguageTests {
  public static class Test {
    public static Test fromFilename(final String filename) {
      final var parts = filename.split(Pattern.quote("_"));
      return new Test(parts[0], parts[1], "success.a".equals(parts[2]));
    }

    public final String clazz;
    public final String name;
    public final boolean success;

    public Test(final String clazz, final String name, final boolean success) {
      this.clazz = clazz;
      this.name = name;
      this.success = success;
      if (name.contains("_") || clazz.contains("_")) { throw new RuntimeException("name and class can not contain understore(_)"); }
    }

    public String filename() {
      return clazz + "_" + name + "_" + (success ? "success" : "failure") + ".a";
    }
  }
  private static class TestClass {
    private final String clazz;
    private final StringBuilder outputFile;
    private int testId;

    public TestClass(final String clazz) {
      testId = 0;
      this.clazz = clazz;
      outputFile = new StringBuilder();
      outputFile.append("package org.adamalang.translator;\n\n");
      outputFile.append("import org.junit.Test;\n\n");
      outputFile.append(String.format("public class Generated%sTests extends GeneratedBase {\n", clazz));
    }

    public void addTest(final Test test, final String code) throws IOException {
      testId++;
      final var varName = test.name + "_" + testId;
      outputFile.append("  private String cached_" + varName + " = null;\n");
      outputFile.append("  private String get_" + varName + "() {\n");
      outputFile.append("    if (cached_" + varName + " != null) {\n");
      outputFile.append("      return cached_" + varName + ";\n");
      outputFile.append("    }\n");
      final var correctedPath = "./test_code/" + test.filename();// path.toString().replaceAll(Pattern.quote(WTF), "/");
      System.out.println(correctedPath);
      final var xyz = new File(correctedPath);
      System.out.println(xyz.exists());
      outputFile.append(String.format("    cached_" + varName + " = generateTestOutput(" + test.success + ", \"%s\", \"%s\");\n", varName, correctedPath));
      outputFile.append("    return cached_" + varName + ";\n");
      outputFile.append("  }\n\n");
      if (test.success) {
        outputFile.append("  @Test\n");
        outputFile.append("  public void test" + test.name + "Emission() {\n");
        outputFile.append("    assertEmissionGood(get_" + varName + "());\n");
        outputFile.append("  }\n\n");
        outputFile.append("  @Test\n");
        outputFile.append("  public void test" + test.name + "Success() {\n");
        outputFile.append("    assertLivePass(get_" + varName + "());\n");
        outputFile.append("  }\n\n");
        outputFile.append("  @Test\n");
        outputFile.append("  public void test" + test.name + "GoodWillHappy() {\n");
        outputFile.append("    assertGoodWillHappy(get_" + varName + "());\n");
        outputFile.append("  }\n\n");
      } else {
        outputFile.append("  @Test\n");
        outputFile.append("  public void test" + test.name + "Failure() {\n");
        outputFile.append("    assertLiveFail(get_" + varName + "());\n");
        outputFile.append("  }\n\n");
      }
      outputFile.append("  @Test\n");
      outputFile.append("  public void test" + test.name + "ExceptionFree() {\n");
      outputFile.append("    assertExceptionFree(get_" + varName + "());\n");
      outputFile.append("  }\n\n");
      outputFile.append("  @Test\n");
      outputFile.append("  public void test" + test.name + "TODOFree() {\n");
      outputFile.append("    assertTODOFree(get_" + varName + "());\n");
      outputFile.append("  }\n\n");
      outputFile.append("  @Test\n");
      outputFile.append("  public void stable_" + varName + "() {\n");
      outputFile.append("    String live = get_" + varName + "();\n");
      outputFile.append("    StringBuilder gold = new StringBuilder();\n");
      final var gold = constructOutput(test.success, varName, xyz.toPath(), xyz.getParentFile().toPath());
      final var lines = gold.split("\n");
      for (var k = 0; k < lines.length; k++) {
        lines[k] = lines[k].stripTrailing();
      }
      if (lines.length > 0) {
        outputFile.append(String.format("    gold.append(\"%s\");\n", escapeLine(lines[0])));
        for (var k = 1; k < lines.length; k++) {
          outputFile.append(String.format("    gold.append(\"\\n%s\");\n", escapeLine(lines[k])));
        }
      }
      outputFile.append("    assertStable(live, gold);\n");
      outputFile.append("  }\n");
    }

    private void finish(final File root) throws IOException {
      outputFile.append("}\n");
      Files.writeString(new File(root, "Generated" + clazz + "Tests.java").toPath(), outputFile.toString());
    }
  }
  private static String WTF = "" + (char) 92;
  public static String constructOutput(final boolean emission, final String className, final Path path, final Path inputRoot) {
    var outputToWrite = constructOutputWithChaos(emission, className, path, inputRoot);
    // outputToWrite = Pattern.compile("\"__entropy\"\\s*:\\s*\"-?[0-9]*\"").matcher(outputToWrite).replaceAll("!EntropyHiddenForStability!");
    outputToWrite = Pattern.compile("\"__next_time\"\\s*:\\s*[0-9\\.E]*").matcher(outputToWrite).replaceAll("!TimeHiddenForStability!");
    return outputToWrite;
  }
  private static String constructOutputWithChaos(final boolean emission, final String className, final Path path, final Path inputRoot) {
    AtomicLong testTime = new AtomicLong(0);
    TimeSource time = new TimeSource() {
      @Override
      public long nowMilliseconds() {
        return testTime.get();
      }
    };
    CompilerOptions options = CompilerOptions.start().enableCodeCoverage().make();
    GlobalObjectPool globals = GlobalObjectPool.createPoolWithStdLib();
    EnvironmentState state = new EnvironmentState(globals, options);
    final var outputFile = new StringBuilder();
    final AtomicReference<Boolean> passedTests = new AtomicReference<>(true);
    var passedValidation = true;
    try {
      final var document = new Document();
      document.addSearchPath(inputRoot.toFile());
      document.importFile(path.toString(), DocumentPosition.ZERO);
      document.setClassName(className);
      document.check(state);
      outputFile.append("Path:").append(path.toString()).append("\n");
      if (emission) {
        outputFile.append("--EMISSION-----------------------------------------").append("\n");
        emission(path.toString(), path, outputFile);
      }
      outputFile.append("--ISSUES-------------------------------------------").append("\n");
      final var java = document.compileJava(state);
      final var issues = Utility.createArrayNode();
      document.writeErrorsAsLanguageServerDiagnosticArray(issues);
      outputFile.append(issues.toPrettyString()).append("\n");
      if (issues.size() > 0) {
        passedValidation = false;
      }
      outputFile.append("--JAVA---------------------------------------------").append("\n");
      outputFile.append(java).append("\n");
      LivingDocumentFactory factory = null;
      if (passedValidation) {
        final var memoryResultsCompiler = new ByteArrayOutputStream();
        final var ps = new PrintStream(memoryResultsCompiler);
        final var oldErr = System.err;
        System.setErr(ps);
        try {
          outputFile.append("--JAVA COMPILE RESULTS-----------------------------").append("\n");
          factory = new LivingDocumentFactory(className, java);
        } finally {
          ps.flush();
          System.setErr(oldErr);
          outputFile.append(new String(memoryResultsCompiler.toByteArray()));
        }
      }
      SilentDocumentMonitor monitor = new SilentDocumentMonitor() {
        @Override
        public void assertFailureAt(int startLine, int startPosition, int endLine, int endLinePosition, int total, int failures) {
          outputFile.append("ASSERT FAILURE:" + startLine + "," + startPosition + " --> " + endLine + "," + endLinePosition + " (" + failures + "/" + total + ")\n");
          passedTests.set(false);
        }
      };
      if (factory != null) {
        outputFile.append("--JAVA RUNNING-------------------------------------").append("\n");
        ObjectNodeLogger objectNodeLog = ObjectNodeLogger.fresh();
        TransactionLogger testTransactionLogger = new TransactionLogger() {
          @Override
          public void ingest(Transaction t) throws Exception {
            outputFile.append(t.request.toString() + "-->" + t.delta.toString() + " need:" + t.transactionResult.needsInvalidation + " in:" + t.transactionResult.whenToInvalidMilliseconds + "\n");
            objectNodeLog.ingest(t);
            testTime.addAndGet(Math.max(t.transactionResult.whenToInvalidMilliseconds / 2, 25));
          }

          @Override
          public void close() throws Exception {
          }
        };
        Transactor transactor = new Transactor(factory, monitor, time, testTransactionLogger);
        transactor.construct(NtClient.NO_ONE, Utility.createObjectNode(), "0");
        try {
          TransactionResult transactionResult = transactor.invalidate();
          while (transactionResult.needsInvalidation) {
            transactionResult = transactor.invalidate();
          }
        } catch (final GoodwillExhaustedException gee) {
          passedTests.set(false);
          outputFile.append("GOODWILL EXHAUSTED:" + gee.getMessage()).append("!!!\n!!!\n");
        }
        transactor.bill();
        outputFile.append("--JAVA RESULTS-------------------------------------").append("\n");
        outputFile.append(objectNodeLog.node.toString()).append("\n");
        outputFile.append("--PRIVACY QUERIES----------------------------------").append("\n");
        try {
          transactor.connect(NtClient.NO_ONE);
          transactor.invalidate();
          final var randoValue = (ObjectNode) transactor.getView(NtClient.NO_ONE);
          outputFile.append("AS NO_ONE:").append(randoValue.toString()).append("\n");
        } catch (DocumentRequestRejectedException drre) {
          outputFile.append("NO_ONE was DENIED\n");
        }
        NtClient rando = new NtClient("rando", "random-place");
        try {
          transactor.connect(rando);
          transactor.invalidate();
          final var randoValue = (ObjectNode) transactor.getView(rando);
          outputFile.append("AS RANDO:").append(randoValue.toString()).append("\n");
         } catch (DocumentRequestRejectedException drre) {
            outputFile.append("RANDO was DENIED\n");
        }
        outputFile.append("--JAVA TEST RESULTS--------------------------------").append("\n");
        final var report = new TestReportBuilder();
        factory.populateTestReport(report, monitor);
        if (report.toString().contains("HAS FAILURES")) {
          passedTests.set(false);
        }
        outputFile.append(report.toString()).append("\n");
      }
      if (passedValidation) {
        if (passedTests.get()) {
          outputFile.append("Success").append("\n");
        } else {
          outputFile.append("AlmostTestsNotPassing").append("\n");
        }
      } else {
        outputFile.append("FailedValidation").append("\n");
      }
    } catch (final Exception ioe) {
      outputFile.append("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!").append("\n");
      outputFile.append("!!EXCEPTION!!!!!!!!!!!!!!!!!!").append("\n");
      outputFile.append("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!").append("\n");
      outputFile.append(String.format("path: %s failed due to to exception", className)).append("\n");
      final var memory = new ByteArrayOutputStream();
      final var writer = new PrintWriter(memory);
      ioe.printStackTrace(writer);
      writer.flush();
      outputFile.append(new String(memory.toByteArray())).append("\n");
    }
    return outputFile.toString();
  }
  private static void emission(final String filename, final Path path, final StringBuilder outputFile) throws Exception {
    final var esb = new StringBuilderDocumentHandler();
    try {
      final var readIn = Files.readString(path);
      final var tokenEngine = new TokenEngine(filename, readIn.codePoints().iterator());
      final var parser = new Parser(tokenEngine);
      parser.document().accept(esb);
      if (!esb.builder.toString().equals(readIn)) {
        outputFile.append("!!!Emission Failure!!!\n");
        outputFile.append("==========================================================\n");
        outputFile.append(esb.builder.toString()).append("\n");
        outputFile.append("=VERSUS===================================================\n");
        outputFile.append(readIn).append("\n");
        outputFile.append("==========================================================\n");
      } else {
        outputFile.append("Emission Success, Yay\n");
      }
    } catch (final Exception eee) {
      outputFile.append("!!!Emission Not Done Yet!!!\n");
      outputFile.append(esb.builder.toString()).append("\n\n");
      final var memory = new ByteArrayOutputStream();
      final var writer = new PrintWriter(memory);
      eee.printStackTrace(writer);
      writer.flush();
      outputFile.append(new String(memory.toByteArray()));
    }
  }
  private static String escapeLine(final String line) {
    return line //
        .replaceAll(Pattern.quote(WTF), WTF + WTF + WTF + WTF) //
        .replaceAll("\"", WTF + WTF + "\"") //
    ;
  }

  public static void main(final String[] args) throws Exception {
    final var template = makeTemplate();
    final var tests = new ArrayList<Test>();
    tests.add(new Test("Functions", "AutoConvertAnonymousOnReturn", true));
    String hackSearch = null;
    final var root = new File("./test_code");
    final var classMap = new TreeMap<String, TestClass>();
    for (final Test test : tests) {
      final var file = new File(root, test.filename());
      final var path = file.toPath();
      if (!file.exists()) {
        Files.writeString(path, template);
      }
    }
    final var files = new ArrayList<File>();
    for (final File testFile : root.listFiles()) {
      if (hackSearch != null && !testFile.getName().contains(hackSearch)) {
        continue;
      }
      files.add(testFile);
    }
    files.sort(Comparator.comparing(File::getName));
    for (final File testFile : files) {
      final var test = Test.fromFilename(testFile.getName());
      System.out.println(test.clazz + "-->" + test.name + ":" + test.success);
      var testClass = classMap.get(test.clazz);
      if (testClass == null) {
        testClass = new TestClass(test.clazz);
        classMap.put(test.clazz, testClass);
      }
      testClass.addTest(test, Files.readString(testFile.toPath()));
    }
    final var outRoot = new File("./src/test/java/org/adamalang/translator");
    for (final Map.Entry<String, TestClass> entry : classMap.entrySet()) {
      entry.getValue().finish(outRoot);
    }
  }

  private static String makeTemplate() {
    final var templateBuilder = new StringBuilder();
    templateBuilder.append("\n");
    templateBuilder.append("@construct {\n");
    templateBuilder.append("}\n");
    templateBuilder.append("\n");
    templateBuilder.append("test PrimaryTest {\n");
    templateBuilder.append("  assert false;\n");
    templateBuilder.append("}\n");
    return templateBuilder.toString();
  }
}
