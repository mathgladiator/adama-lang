/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
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
