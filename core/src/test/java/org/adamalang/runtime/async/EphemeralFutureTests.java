/*
* Adama Platform and Language
* Copyright (C) 2021 - 2023 by Adama Platform Initiative, LLC
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as published
* by the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Affero General Public License for more details.
* 
* You should have received a copy of the GNU Affero General Public License
* along with this program.  If not, see <https://www.gnu.org/licenses/>.
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
  public void abort_a() {
    EphemeralFuture future = new EphemeralFuture();
    DumbCallback cb = new DumbCallback();
    future.abort(123);
    future.attach(cb);
    Assert.assertFalse(cb.success);
    Assert.assertEquals(123, (int) cb.code);
  }

  @Test
  public void abort_b() {
    EphemeralFuture future = new EphemeralFuture();
    DumbCallback cb = new DumbCallback();
    future.attach(cb);
    future.abort(123);
    Assert.assertFalse(cb.success);
    Assert.assertEquals(123, (int) cb.code);
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
