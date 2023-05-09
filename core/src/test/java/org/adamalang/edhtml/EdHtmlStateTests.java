/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.edhtml;

import org.junit.Test;

import java.io.File;

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
    EdHtmlTool.phases(state);
    String result = state.finish();
    System.out.println(result);
  }
}
