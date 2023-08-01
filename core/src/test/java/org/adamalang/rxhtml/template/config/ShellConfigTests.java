/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.rxhtml.template.config;


import org.junit.Assert;
import org.junit.Test;

public class ShellConfigTests {
  @Test
  public void flow() {
    ShellConfig sc = ShellConfig.start().withFeedback(Feedback.NoOp).withUseLocalAdamaJavascript(true).end();
    Assert.assertTrue(sc.useLocalAdamaJavascript);
  }
}
