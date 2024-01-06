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
package org.adamalang.common.dns;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class NettyDNSTxtResolverTests {
  @Test
  public void sanity() throws Exception {
    NettyDNSTxtResolver resolver = new NettyDNSTxtResolver();
    CountDownLatch latch = new CountDownLatch(1);
    AtomicBoolean success = new AtomicBoolean(false);
    resolver.query("adama-platform.com", new Callback<String[]>() {
      @Override
      public void success(String[] value) {
        for (String str : value) {
          System.err.println("FOUND: [" +str + "]");
        }
        success.set(true);
        latch.countDown();
      }

      @Override
      public void failure(ErrorCodeException ex) {
        System.err.println("FAILED:" + ex.code);
        latch.countDown();
      }
    });
    Assert.assertTrue(latch.await(15000, TimeUnit.MILLISECONDS));
    Assert.assertTrue(success.get());
  }
}
