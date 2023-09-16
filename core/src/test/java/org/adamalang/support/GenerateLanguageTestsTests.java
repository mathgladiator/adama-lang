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
    GenerateLanguageTests.generate("./test_data/code1", "./test_data/java-out1", "./test_data/errors.csv");
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
    GenerateLanguageTests.generate("./test_data/code2", "./test_data/java-out2", "./test_data/errors.csv");
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
