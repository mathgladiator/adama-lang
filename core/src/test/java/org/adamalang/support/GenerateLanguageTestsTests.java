/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's Apache2); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.support;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.nio.file.Files;

public class GenerateLanguageTestsTests {
  @Test
  public void empty() throws Exception {
    final var testdata = new File("./test_data");
    testdata.mkdir();
    final var testdataCode = new File("./test_data/code1");
    testdataCode.mkdir();
    final var javaOut = new File("./test_data/java-out1");
    javaOut.mkdir();
    GenerateLanguageTests.generate(
        0,
        new String[] {
          "--input", "./test_data/code1", "--output", "./test_data/java-out1", "--what", "ok", "--errors", "./test_data/errors.csv"
        });
    javaOut.delete();
    testdataCode.delete();
  }

  @Test
  public void something() throws Exception {
    final var testdata = new File("./test_data");
    testdata.mkdir();
    final var testdataCode = new File("./test_data/code2");
    testdataCode.mkdir();
    Files.writeString(new File(testdataCode, "Clazz_X_success.a").toPath(), "#sm {}");
    final var javaOut = new File("./test_data/java-out2");
    javaOut.mkdir();
    GenerateLanguageTests.generate(
        0, new String[] {"--input", "./test_data/code2", "--output", "./test_data/java-out2", "--errors", "./test_data/errors.csv"});
    final var testExists = new File(javaOut, "GeneratedClazzTests.java");
    Assert.assertTrue(testExists.exists());
    testExists.delete();
    javaOut.delete();
    new File(testdataCode, "Clazz_X_success.a").delete();
    testdataCode.delete();
  }

  @Test
  public void csv() throws Exception {
    File errorMessages = new File("./test_data/error-messages.csv");
    GenerateLanguageTests.writeErrorCSV("./test_code", "./test_data/error-messages.csv");
    errorMessages.delete();
  }
}
