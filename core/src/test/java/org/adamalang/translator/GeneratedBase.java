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
package org.adamalang.translator;

import org.adamalang.support.testgen.TestForge;
import org.junit.Assert;

import java.io.File;

public class GeneratedBase {
  public String generateTestOutput(boolean emission, String className, String filename) {
    File file = new File(filename);
    Assert.assertTrue(file.exists());
    Assert.assertTrue(file.getParentFile().isDirectory());
    try {
      String output =
          TestForge.forge(emission, className, file.toPath(), file.getParentFile().toPath());
      String[] lines = output.split("\n");
      for (int k = 0; k < lines.length; k++) {
        lines[k] = lines[k].stripTrailing();
      }
      StringBuilder reconstructed = new StringBuilder();
      reconstructed.append(lines[0]);
      for (int k = 1; k < lines.length; k++) {
        reconstructed.append("\n").append(lines[k]);
      }
      return reconstructed.toString();
    } catch (Throwable t) {
      t.printStackTrace();
      return "FailedDueToException";
    }
  }

  public void assertTODOFree(String live) {
    Assert.assertFalse(live.contains("TODO"));
  }

  public void assertExceptionFree(String live) {
    if (live.contains("!!EXCEPTION!!")) {
      Assert.fail("contains an exception: " + live);
    }
    if (live.contains("FAILED PRIVATE VIEW DUE TO:180978")) {
      Assert.fail("failed due to private view having an exception!");
    }
  }

  public void assertGoodWillHappy(String live) {
    Assert.assertFalse(live.contains("GOODWILL EXHAUSTED"));
  }

  public void assertEmissionGood(String live) {
    Assert.assertTrue(live.contains("Emission Success, Yay"));
  }

  public void assertLivePass(String live) {
    if (!(live.endsWith("Success"))) {
      Assert.fail("Does not end with 'Success':" + live);
    }
  }

  public void assertNoFormatException(String live) {
    Assert.assertFalse(live.contains("FORMAT-EXCEPTION"));
  }

  public void assertLiveFail(String live) {
    Assert.assertTrue(!live.endsWith("Success"));
  }

  public void assertStable(String live, StringBuilder gold) {
    Assert.assertEquals(gold.toString(), live);
  }

  public void assertNotTerribleLineNumbers(String live) {
    if (live.contains("2147483647")) {
      System.err.println(live);
    }
    Assert.assertFalse(live.contains("2147483647"));
  }
}
