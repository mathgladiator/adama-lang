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
import org.adamalang.common.SimpleExecutor;
import org.adamalang.runtime.natives.NtAsset;
import org.adamalang.web.assets.transforms.capture.DiskCapture;
import org.adamalang.web.assets.transforms.capture.InflightAsset;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class DiskCaptureTests {
  private void body(Consumer<DiskCapture> body, int size, Callback<InflightAsset> callback) throws Exception {
    SimpleExecutor executor = SimpleExecutor.create("disk");
    try {
      NtAsset asset = new NtAsset("assetid", "name", "text/plain", size, "md5", "sha");
      File temp = File.createTempFile("tr-pr", "post");
      temp.delete();
      temp.mkdirs();
      CountDownLatch finished = new CountDownLatch(1);
      try {
        body.accept(new DiskCapture(executor, asset, temp, new Callback<InflightAsset>() {
          @Override
          public void success(InflightAsset value) {
            callback.success(value);
            finished.countDown();
          }

          @Override
          public void failure(ErrorCodeException ex) {
            callback.failure(ex);
            finished.countDown();
          }
        }));
        Assert.assertTrue(finished.await(50000, TimeUnit.MILLISECONDS));
      } finally {
        for (File f : temp.listFiles()) {
          f.delete();
        }
        temp.delete();
      }
    } finally {
      executor.shutdown().await(1000, TimeUnit.MILLISECONDS);
    }
  }

  @Test
  public void happy() throws Exception {
    CountDownLatch done = new CountDownLatch(1);
    Callback<InflightAsset> callback = new Callback<InflightAsset>() {
      @Override
      public void success(InflightAsset inflight) {
        try {
          InputStream input = inflight.open();
          String check = new String(input.readAllBytes(), StandardCharsets.UTF_8);
          Assert.assertEquals("hello world\nhello harry", check);
        } catch (Exception ex) {

        }
        inflight.finished();
        done.countDown();
      }

      @Override
      public void failure(ErrorCodeException ex) {
        System.err.println("Failed:" + ex);
        done.countDown();
      }
    };

    byte[] write1 = "hello world\n".getBytes(StandardCharsets.UTF_8);
    byte[] write2 = "hello harry".getBytes(StandardCharsets.UTF_8);
    body((dc) -> {
      dc.headers(write1.length + write2.length, "text/merged", "md5");
      dc.body(write1, 0, write1.length, false);
      dc.body(write2, 0, write2.length, true);
    }, write1.length + write2.length, callback);
    Assert.assertTrue(done.await(10000, TimeUnit.MILLISECONDS));
  }

  @Test
  public void sad_1_immediate() throws Exception {
    CountDownLatch done = new CountDownLatch(1);
    Callback<InflightAsset> callback = new Callback<InflightAsset>() {
      @Override
      public void success(InflightAsset inflight) {

      }

      @Override
      public void failure(ErrorCodeException ex) {
        System.err.println("Failed:" + ex);
        Assert.assertEquals(10000, ex.code);
        done.countDown();
      }
    };

    byte[] write1 = "hello world\n".getBytes(StandardCharsets.UTF_8);
    byte[] write2 = "hello harry".getBytes(StandardCharsets.UTF_8);
    body((dc) -> {
      dc.failure(10000);
    }, write1.length + write2.length, callback);
    Assert.assertTrue(done.await(10000, TimeUnit.MILLISECONDS));
  }

  @Test
  public void sad_2_after_headers() throws Exception {
    CountDownLatch done = new CountDownLatch(1);
    Callback<InflightAsset> callback = new Callback<InflightAsset>() {
      @Override
      public void success(InflightAsset inflight) {

      }

      @Override
      public void failure(ErrorCodeException ex) {
        System.err.println("Failed:" + ex);
        Assert.assertEquals(10000, ex.code);
        done.countDown();
      }
    };

    byte[] write1 = "hello world\n".getBytes(StandardCharsets.UTF_8);
    byte[] write2 = "hello harry".getBytes(StandardCharsets.UTF_8);
    body((dc) -> {
      dc.headers(write1.length + write2.length, "text/merged", "md5");
      dc.failure(10000);
    }, write1.length + write2.length, callback);
    Assert.assertTrue(done.await(10000, TimeUnit.MILLISECONDS));
  }

  @Test
  public void sad_3_after_headers_and_part_body() throws Exception {
    CountDownLatch done = new CountDownLatch(1);
    Callback<InflightAsset> callback = new Callback<InflightAsset>() {
      @Override
      public void success(InflightAsset inflight) {

      }

      @Override
      public void failure(ErrorCodeException ex) {
        System.err.println("Failed:" + ex);
        Assert.assertEquals(10000, ex.code);
        done.countDown();
      }
    };

    byte[] write1 = "hello world\n".getBytes(StandardCharsets.UTF_8);
    byte[] write2 = "hello harry".getBytes(StandardCharsets.UTF_8);
    body((dc) -> {
      dc.headers(write1.length + write2.length, "text/merged", "md5");
      dc.body(write1, 0, write1.length, false);
      dc.failure(10000);
    }, write1.length + write2.length, callback);
    Assert.assertTrue(done.await(10000, TimeUnit.MILLISECONDS));
  }
}
