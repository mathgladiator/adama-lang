/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.runtime.sys.web;

import org.junit.Assert;
import org.junit.Test;

public class WebResponseTests {
  @Test
  public void flow() {
    {
      WebResponse response = new WebResponse();
      response.xml("x");
      Assert.assertEquals("x", response.body);
      Assert.assertEquals("application/xml", response.contentType);
    }
    {
      WebResponse response = new WebResponse();
      response.html("HTTTTMMMEl");
      Assert.assertEquals("HTTTTMMMEl", response.body);
      Assert.assertEquals("text/html; charset=utf-8", response.contentType);
    }
  }
}
