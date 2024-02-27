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
package org.adamalang.web.assets.transforms.capture;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.web.assets.transforms.capture.InflightAsset;
import org.adamalang.web.assets.transforms.capture.MemoryCapture;
import org.junit.Assert;
import org.junit.Test;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class MemoryCaptureTests {
  @Test
  public void happy() throws Exception {
    CountDownLatch success = new CountDownLatch(1);
    MemoryCapture mc = new MemoryCapture(new Callback<InflightAsset>() {
      @Override
      public void success(InflightAsset inflight) {
        try {
          InputStream input = inflight.open();
          String check = new String(input.readAllBytes(), StandardCharsets.UTF_8);
          Assert.assertEquals("hello world\nhello harry", check);
          inflight.finished();
          success.countDown();
        } catch (Exception ex) {
        }
      }

      @Override
      public void failure(ErrorCodeException ex) {
      }
    });
    byte[] write1 = "hello world\n".getBytes(StandardCharsets.UTF_8);
    byte[] write2 = "hello harry".getBytes(StandardCharsets.UTF_8);
    mc.headers(write1.length + write2.length, "text/merged", "md5");
    mc.body(write1, 0, write1.length, false);
    mc.body(write2, 0, write2.length, true);
    Assert.assertTrue(success.await(15000, TimeUnit.MILLISECONDS));
  }

  @Test
  public void sad() throws Exception {
    CountDownLatch failed = new CountDownLatch(1);
    MemoryCapture mc = new MemoryCapture(new Callback<InflightAsset>() {
      @Override
      public void success(InflightAsset inflight) {
      }

      @Override
      public void failure(ErrorCodeException ex) {
        Assert.assertEquals(100000, ex.code);
        failed.countDown();
      }
    });
    mc.failure(100000);
    Assert.assertTrue(failed.await(15000, TimeUnit.MILLISECONDS));
  }
}
