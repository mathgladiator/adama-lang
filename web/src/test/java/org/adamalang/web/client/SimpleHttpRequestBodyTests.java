/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.web.client;

import org.junit.Assert;
import org.junit.Test;

import java.nio.charset.StandardCharsets;

public class SimpleHttpRequestBodyTests {
  @Test
  public void empty() {
    Assert.assertEquals(0, SimpleHttpRequestBody.EMPTY.size());
    SimpleHttpRequestBody.EMPTY.read(null);
    SimpleHttpRequestBody.EMPTY.finished();
  }
  @Test
  public void bytearray() {
    SimpleHttpRequestBody body = SimpleHttpRequestBody.WRAP("Hello World".getBytes(StandardCharsets.UTF_8));
    Assert.assertEquals(11, body.size());
    byte[] chunk = new byte[4];
    Assert.assertEquals(4, body.read(chunk));
    Assert.assertEquals("Hell", new String(chunk, StandardCharsets.UTF_8));
    Assert.assertEquals(4, body.read(chunk));
    Assert.assertEquals("o Wo", new String(chunk, StandardCharsets.UTF_8));
    Assert.assertEquals(3, body.read(chunk));
    Assert.assertEquals("rld", new String(chunk, 0, 3, StandardCharsets.UTF_8));
    body.finished();
  }
}
