/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.runtime.async;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;

public class EphemeralFutureTests {

  private class DumbCallback implements Callback<Object> {
    boolean success = false;
    Object got = null;
    Integer code = null;

    @Override
    public void success(Object value) {
      if (success) {
        Assert.fail();
      }
      success = true;
      got = value;
    }

    @Override
    public void failure(ErrorCodeException ex) {
      if (success || code != null) {
        Assert.fail();
      }
      code = ex.code;
    }
  };

  @Test
  public void happy_a() {
    EphemeralFuture future = new EphemeralFuture();
    DumbCallback cb = new DumbCallback();
    future.attach(cb);
    future.send("Hi");
    Assert.assertTrue(cb.success);
    Assert.assertEquals("Hi", cb.got);
  }

  @Test
  public void happy_a_redun() {
    EphemeralFuture future = new EphemeralFuture();
    DumbCallback cb = new DumbCallback();
    future.attach(cb);
    future.send("Hi");
    Assert.assertTrue(cb.success);
    Assert.assertEquals("Hi", cb.got);
    future.send("X");
    future.cancel();
    Assert.assertTrue(cb.success);
    Assert.assertEquals("Hi", cb.got);
  }

  @Test
  public void happy_b() {
    EphemeralFuture future = new EphemeralFuture();
    DumbCallback cb = new DumbCallback();
    future.send("Hi");
    future.attach(cb);
    Assert.assertTrue(cb.success);
    Assert.assertEquals("Hi", cb.got);
  }

  @Test
  public void happy_b_redun() {
    EphemeralFuture future = new EphemeralFuture();
    DumbCallback cb = new DumbCallback();
    future.send("Hi");
    future.attach(cb);
    Assert.assertTrue(cb.success);
    Assert.assertEquals("Hi", cb.got);
    future.send("X");
    future.cancel();
    Assert.assertTrue(cb.success);
    Assert.assertEquals("Hi", cb.got);
  }

  @Test
  public void sad_a() {
    EphemeralFuture future = new EphemeralFuture();
    DumbCallback cb = new DumbCallback();
    future.cancel();
    future.send("Hi");
    future.attach(cb);
    Assert.assertFalse(cb.success);
    Assert.assertEquals(114384, (int) cb.code);
  }

  @Test
  public void sad_a_redun() {
    EphemeralFuture future = new EphemeralFuture();
    DumbCallback cb = new DumbCallback();
    future.cancel();
    future.send("Hi");
    future.attach(cb);
    future.send("X");
    future.cancel();
    Assert.assertFalse(cb.success);
    Assert.assertEquals(114384, (int) cb.code);
  }

  @Test
  public void sad_b() {
    EphemeralFuture future = new EphemeralFuture();
    DumbCallback cb = new DumbCallback();
    future.send("Hi");
    future.cancel();
    future.attach(cb);
    Assert.assertFalse(cb.success);
    Assert.assertEquals(114384, (int) cb.code);
  }

  @Test
  public void sad_c() {
    EphemeralFuture future = new EphemeralFuture();
    DumbCallback cb = new DumbCallback();
    future.attach(cb);
    future.cancel();
    future.send("Hi");
    Assert.assertFalse(cb.success);
    Assert.assertEquals(114384, (int) cb.code);
  }
}
