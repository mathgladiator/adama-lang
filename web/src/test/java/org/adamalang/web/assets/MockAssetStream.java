/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.web.assets;

import org.junit.Assert;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

public class MockAssetStream implements AssetStream {

  private ByteArrayOutputStream memory = new ByteArrayOutputStream();
  private long length = -1;
  private String type = "";
  private boolean done = false;
  private Integer failureCode = null;

  @Override
  public void headers(long length, String contentType) {
    this.length = length;
    this.type = contentType;
  }

  public void assertHeaders(long exLength, String exType) {
    Assert.assertEquals(exLength, this.length);
    Assert.assertEquals(exType, this.type);
  }

  @Override
  public void body(byte[] chunk, int offset, int length, boolean last) {
    memory.write(chunk, offset, length);
    if (last) {
      Assert.assertFalse(done);
      done = true;
    }
  }

  public void assertNotDone() {
    Assert.assertFalse(done);
  }

  public void assertDone() {
    Assert.assertTrue(done);
  }

  public void assertBody(String expect) {
    Assert.assertEquals(expect, new String(memory.toByteArray(), StandardCharsets.UTF_8));
  }

  public void assertNoFailure() {
    Assert.assertNull(failureCode);
  }

  public void assertFailure(int code) {
    Assert.assertNotNull(failureCode);
    Assert.assertEquals(code, (int) failureCode);
  }

  @Override
  public void failure(int code) {
    this.failureCode = code;
  }
}
