/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.netty.server;

import org.junit.Assert;
import org.junit.Test;

public class InferContentTypeTests {
  @Test
  public void cov() {
    new InferContentType();
  }

  @Test
  public void css() {
    Assert.assertEquals("text/css", InferContentType.fromFilename("file.css"));
  }

  @Test
  public void html() {
    Assert.assertEquals("text/html; charset=UTF-8", InferContentType.fromFilename("file.html"));
    Assert.assertEquals("text/html; charset=UTF-8", InferContentType.fromFilename("file.htm"));
  }

  @Test
  public void jpeg() {
    Assert.assertEquals("image/jpeg", InferContentType.fromFilename("file.jpeg"));
    Assert.assertEquals("image/jpeg", InferContentType.fromFilename("file.jpg"));
  }

  @Test
  public void js() {
    Assert.assertEquals("text/javascript", InferContentType.fromFilename("file.js"));
  }

  @Test
  public void noext() {
    Assert.assertNull(InferContentType.fromFilename("noext"));
  }

  @Test
  public void png() {
    Assert.assertEquals("image/png", InferContentType.fromFilename("file.png"));
  }

  @Test
  public void webp() {
    Assert.assertEquals("image/webp", InferContentType.fromFilename("file.webp"));
  }

  @Test
  public void wasm() {
    Assert.assertEquals("application/wasm", InferContentType.fromFilename("file.wasm"));
  }

  @Test
  public void unknown() {
    Assert.assertNull(InferContentType.fromFilename("what.zzzzzzzzzzzzzzzzz"));
  }
}
