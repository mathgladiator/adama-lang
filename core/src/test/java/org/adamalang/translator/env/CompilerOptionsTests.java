/*
* Adama Platform and Language
* Copyright (C) 2021 - 2024 by Adama Platform Engineering, LLC
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
package org.adamalang.translator.env;

import org.junit.Assert;
import org.junit.Test;

public class CompilerOptionsTests {
  @Test
  public void args1() {
    final var options =
        CompilerOptions.start()
            .args(
                0,
                "--billing",
                "no",
                "--code-coverage",
                "no",
                "--remove-tests",
                "no",
                "--silent",
                "no",
                "--goodwill-budget",
                "0",
                "--class",
                "ClassName")
            .make();
    Assert.assertTrue(options.disableBillingCost);
    Assert.assertFalse(options.produceCodeCoverage);
    Assert.assertFalse(options.removeTests);
    Assert.assertTrue(options.stderrLoggingCompiler);
    Assert.assertEquals(0, options.goodwillBudget);
    Assert.assertEquals("ClassName", options.className);
  }

  @Test
  public void args2() {
    final var options =
        CompilerOptions.start()
            .args(
                0,
                "--billing",
                "yes",
                "--code-coverage",
                "yes",
                "--remove-tests",
                "yes",
                "--silent",
                "yes",
                "--goodwill-budget",
                "5000",
                "--class",
                "ClassName2")
            .make();
    Assert.assertFalse(options.disableBillingCost);
    Assert.assertTrue(options.produceCodeCoverage);
    Assert.assertTrue(options.removeTests);
    Assert.assertFalse(options.stderrLoggingCompiler);
    Assert.assertEquals(5000, options.goodwillBudget);
    // Assert.assertEquals("org.foo2", options.packageName);
    Assert.assertEquals("ClassName2", options.className);
  }

  @Test
  public void builder() {
    final var options = CompilerOptions.start().enableCodeCoverage().noCost().make();
    Assert.assertTrue(options.disableBillingCost);
    Assert.assertTrue(options.produceCodeCoverage);
  }

  @Test
  public void core() {
    final var options =
        CompilerOptions.start()
            .args(
                0,
                "--input",
                "a.a",
                "--input",
                "b.a",
                "--add-search-path",
                "foo",
                "--add-search-path",
                "goo",
                "--output",
                "oout")
            .make();
    Assert.assertEquals("a.a", options.inputFiles[0]);
    Assert.assertEquals("b.a", options.inputFiles[1]);
    Assert.assertEquals("foo", options.searchPaths[0]);
    Assert.assertEquals("goo", options.searchPaths[1]);
    Assert.assertEquals("oout", options.outputFile);
  }

  @Test
  public void offset() {
    final var options =
        CompilerOptions.start()
            .args(
                1,
                "compile",
                "--billing",
                "yes",
                "--code-coverage",
                "yes",
                "--remove-tests",
                "yes",
                "--silent",
                "yes",
                "--goodwill-budget",
                "5000",
                "--package",
                "org.foo2",
                "--class",
                "ClassName2")
            .make();
    Assert.assertFalse(options.disableBillingCost);
    Assert.assertTrue(options.produceCodeCoverage);
    Assert.assertTrue(options.removeTests);
    Assert.assertFalse(options.stderrLoggingCompiler);
    Assert.assertEquals(5000, options.goodwillBudget);
    Assert.assertEquals("ClassName2", options.className);
  }
}
