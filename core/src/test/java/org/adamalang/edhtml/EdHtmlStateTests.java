/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.edhtml;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.NoSuchFileException;

public class EdHtmlStateTests {
  public static String test_edhtml_path() {
    File test = new File("core/test_edhtml");
    if (test.exists() && test.isDirectory()) {
      return "core/test_edhtml";
    }
    return "test_edhtml";
  }

  @Test
  public void validate_test_edhtml_path() throws Exception {
    EdHtmlState state = new EdHtmlState(new String[] { "--base", test_edhtml_path(), "--input", "sample.ed.html" });
    String result = EdHtmlTool.phases(state);
    System.out.println(result);
  }

  @Test
  public void failure_no_factory() {
    try {
      EdHtmlState state = new EdHtmlState(new String[]{"--base", test_edhtml_path(), "--input", "failure-no-factory.ed.html"});
      EdHtmlTool.phases(state);
    } catch (Exception ex) {
      Assert.assertEquals("No 'factory' field available for generate", ex.getMessage());
    }
  }

  @Test
  public void failure_no_from() {
    try {
      EdHtmlState state = new EdHtmlState(new String[]{"--base", test_edhtml_path(), "--input", "failure-no-from.ed.html"});
      EdHtmlTool.phases(state);
    } catch (Exception ex) {
      Assert.assertEquals("No 'from' field available for generate", ex.getMessage());
    }
  }

  @Test
  public void failure_no_manifest() {
    try {
      EdHtmlState state = new EdHtmlState(new String[]{"--base", test_edhtml_path(), "--input", "failure-no-manifest.ed.html"});
      EdHtmlTool.phases(state);
    } catch (Exception ex) {
      Assert.assertEquals("no manifest; need either a channel or type (or both)", ex.getMessage());
    }
  }

  @Test
  public void failure_bad_from() {
    try {
      EdHtmlState state = new EdHtmlState(new String[]{"--base", test_edhtml_path(), "--input", "failure-bad-from.ed.html"});
      EdHtmlTool.phases(state);
    } catch (Exception ex) {
      Assert.assertTrue(ex.getCause() instanceof FileNotFoundException);
    }
  }

  @Test
  public void failure_bad_factory() {
    try {
      EdHtmlState state = new EdHtmlState(new String[]{"--base", test_edhtml_path(), "--input", "failure-bad-factory.ed.html"});
      EdHtmlTool.phases(state);
    } catch (Exception ex) {
      Assert.assertTrue(ex.getCause() instanceof NoSuchFileException);
    }
  }
}
