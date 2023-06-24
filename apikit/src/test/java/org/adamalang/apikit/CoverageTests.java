/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.apikit;

import org.junit.Test;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class CoverageTests {

  @Test
  public void dumb() throws Exception {
    HashMap<File, String> files = Tool.buildInMemoryFileSystem("saas/api.xml", new File(".."));
    for (Map.Entry<File, String> entry : files.entrySet()) {
      System.err.println(entry.getKey().getAbsolutePath() + "::" + entry.getValue());
    }
  }
}
