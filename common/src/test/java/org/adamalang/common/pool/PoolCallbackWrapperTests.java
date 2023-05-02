/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.common.pool;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

public class PoolCallbackWrapperTests {
  @Test
  public void coverage() {
    ArrayList<String> events = new ArrayList<>();
    PoolItem<String> item = new PoolItem<String>() {
      @Override
      public String item() {
        return "xyz";
      }

      @Override
      public void signalFailure() {
        events.add("ITEM:FAILURE");
      }

      @Override
      public void returnToPool() {
        events.add("ITEM:SUCCESS->RETURN");
      }
    };
    Callback<String> cb = new Callback<String>() {
      @Override
      public void success(String value) {
        events.add("CB:SUCCESS==" + value);
      }

      @Override
      public void failure(ErrorCodeException ex) {
        events.add("CB:FAILURE:" + ex.code);
      }
    };
    PoolCallbackWrapper<String, String> w = new PoolCallbackWrapper<>(cb, item);
    w.success("xyz");
    Assert.assertEquals(2, events.size());
    w.failure(new ErrorCodeException(123));
    Assert.assertEquals(4, events.size());
    Assert.assertEquals("CB:SUCCESS==xyz", events.get(0));
    Assert.assertEquals("ITEM:SUCCESS->RETURN", events.get(1));
    Assert.assertEquals("CB:FAILURE:123", events.get(2));
    Assert.assertEquals("ITEM:FAILURE", events.get(3));
  }
}
