/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.web.client;

import org.junit.Assert;
import org.junit.Test;

import java.nio.charset.StandardCharsets;

public class SimpleHttpRequestBodyTests {
  @Test
  public void empty() throws Exception {
    Assert.assertEquals(0, SimpleHttpRequestBody.EMPTY.size());
    SimpleHttpRequestBody.EMPTY.read(null);
    SimpleHttpRequestBody.EMPTY.finished(true);
  }
  @Test
  public void bytearray() throws Exception {
    SimpleHttpRequestBody body = SimpleHttpRequestBody.WRAP("Hello World".getBytes(StandardCharsets.UTF_8));
    Assert.assertEquals(11, body.size());
    byte[] chunk = new byte[4];
    Assert.assertEquals(4, body.read(chunk));
    Assert.assertEquals("Hell", new String(chunk, StandardCharsets.UTF_8));
    Assert.assertEquals(4, body.read(chunk));
    Assert.assertEquals("o Wo", new String(chunk, StandardCharsets.UTF_8));
    Assert.assertEquals(3, body.read(chunk));
    Assert.assertEquals("rld", new String(chunk, 0, 3, StandardCharsets.UTF_8));
    body.finished(true);
  }
}
