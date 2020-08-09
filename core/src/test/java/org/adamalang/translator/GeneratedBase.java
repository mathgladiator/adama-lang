/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
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
      String output = TestForge.forge(emission, className, file.toPath(), file.getParentFile().toPath());
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
    Assert.assertFalse(live.contains("!!EXCEPTION!!"));
  }
  public void assertGoodWillHappy(String live) {
    Assert.assertFalse(live.contains("GOODWILL EXHAUSTED"));
  }
  public void assertEmissionGood(String live) {
    Assert.assertTrue(live.contains("Emission Success, Yay"));
  }
  public void assertLivePass(String live) {
    Assert.assertTrue(live.endsWith("Success"));
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
