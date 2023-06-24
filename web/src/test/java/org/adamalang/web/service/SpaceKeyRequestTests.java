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
