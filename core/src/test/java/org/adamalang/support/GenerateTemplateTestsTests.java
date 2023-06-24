/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.support;

import org.junit.Test;

import java.io.File;

public class GenerateTemplateTestsTests {
  @Test
  public void empty() throws Exception {
    final var testdata = new File("./test_data");
    testdata.mkdir();
    final var testdataCode = new File("./test_data/code1");
    testdataCode.mkdir();
    final var javaOut = new File("./test_data/java-out1");
    javaOut.mkdir();
    GenerateTemplateTests.generate(
        0,
        new String[] {
            "--input", "./test_data/code1", "--output", "./test_data/java-out1", "--what", "ok", "--errors", "./test_data/errors.csv"
        });
    javaOut.delete();
    testdataCode.delete();
  }
}
