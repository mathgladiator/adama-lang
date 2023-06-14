/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.web.contracts;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.junit.Assert;
import org.junit.Test;

import java.util.TreeMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class NullHttpHandlerTests {
  @Test
  public void coverage() throws Exception {
    CountDownLatch latch = new CountDownLatch(5);
    HttpHandler.NULL.handleGet("", new TreeMap<>(), "{}", new Callback<HttpHandler.HttpResult>() {
      @Override
      public void success(HttpHandler.HttpResult value) {
        latch.countDown();
      }

      @Override
      public void failure(ErrorCodeException ex) {

      }
    });
    HttpHandler.NULL.handleOptions("", new TreeMap<>(), "{}", new Callback<HttpHandler.HttpResult>() {
      @Override
      public void success(HttpHandler.HttpResult value) {
        latch.countDown();
      }

      @Override
      public void failure(ErrorCodeException ex) {

      }
    });
    HttpHandler.NULL.handleDelete("", new TreeMap<>(), "{}", new Callback<HttpHandler.HttpResult>() {
      @Override
      public void success(HttpHandler.HttpResult value) {
        latch.countDown();
      }

      @Override
      public void failure(ErrorCodeException ex) {

      }
    });
    HttpHandler.NULL.handleDeepHealth(new Callback<String>() {
      @Override
      public void success(String value) {
        latch.countDown();
      }

      @Override
      public void failure(ErrorCodeException ex) {

      }
    });
    HttpHandler.NULL.handlePost("", new TreeMap<>(), "{}", null, new Callback<HttpHandler.HttpResult>() {
      @Override
      public void success(HttpHandler.HttpResult value) {
        latch.countDown();
      }

      @Override
      public void failure(ErrorCodeException ex) {

      }
    });
    Assert.assertTrue(latch.await(5000, TimeUnit.MILLISECONDS));
  }
}
