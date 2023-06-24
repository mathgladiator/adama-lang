/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.web.service;

import org.junit.Assert;
import org.junit.Test;

public class AdamaWebRequestTests {
  @Test
  public void detection() {
    String xyz = AdamaWebRequest.detectBodyAsQueryString("x=123");
    Assert.assertEquals("{\"x\":\"123\"}", xyz);
    Assert.assertNull(AdamaWebRequest.detectBodyAsQueryString("{\"x\":123}"));
  }
}
