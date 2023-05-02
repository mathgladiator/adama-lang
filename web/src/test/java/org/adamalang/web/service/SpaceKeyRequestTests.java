/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's Apache2); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.web.service;

import org.junit.Assert;
import org.junit.Test;

public class SpaceKeyRequestTests {
  @Test
  public void flow() {
    SpaceKeyRequest req;
    Assert.assertNull(SpaceKeyRequest.parse("/xyz"));
    req = SpaceKeyRequest.parse("/xyz/key-1234/uri/tail-wtf");
    Assert.assertEquals("xyz", req.space);
    Assert.assertEquals("key-1234", req.key);
    Assert.assertEquals("/uri/tail-wtf", req.uri);
    req = SpaceKeyRequest.parse("/xyz/da-key");// implicit root
    Assert.assertEquals("xyz", req.space);
    Assert.assertEquals("da-key", req.key);
    Assert.assertEquals("/", req.uri);
  }
}
