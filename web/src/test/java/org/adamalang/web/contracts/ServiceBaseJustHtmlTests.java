/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.web.contracts;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.web.io.JsonResponder;
import org.junit.Assert;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class ServiceBaseJustHtmlTests {
  @Test
  public void coverage() throws Exception {
    ServiceBase base = ServiceBase.JUST_HTTP(new HttpHandler() {

      @Override
      public void handleOptions(String uri, Callback<Boolean> callback) {
        callback.success(uri.equalsIgnoreCase("/opt=yes"));
      }

      @Override
      public void handleGet(String uri, TreeMap<String, String> headers, String parametersJson, Callback<HttpResult> callback) {
        callback.success(new HttpResult("yay", "yay".getBytes(StandardCharsets.UTF_8)));
      }

      @Override
      public void handlePost(String uri, TreeMap<String, String> headers, String parametersJson, String body, Callback<HttpResult> callback) {
        callback.success(new HttpResult("post", "post".getBytes(StandardCharsets.UTF_8)));
      }
    });
    base.establish(null).execute(null, new JsonResponder() {
      @Override
      public void stream(String json) {

      }

      @Override
      public void finish(String json) {

      }

      @Override
      public void error(ErrorCodeException ex) {

      }
    });
    base.establish(null).keepalive();
    base.establish(null).kill();
    base.downloader();
    CountDownLatch latch = new CountDownLatch(3);
    base.http().handleOptions("/opt=yes", new Callback<Boolean>() {
      @Override
      public void success(Boolean value) {
        Assert.assertTrue(value);;
        latch.countDown();
      }

      @Override
      public void failure(ErrorCodeException ex) {

      }
    });
    base.http().handleGet("x", new TreeMap<>(), "{}", new Callback<HttpHandler.HttpResult>() {
      @Override
      public void success(HttpHandler.HttpResult value) {
        Assert.assertEquals("yay", new String(value.body, StandardCharsets.UTF_8));
        latch.countDown();
      }

      @Override
      public void failure(ErrorCodeException ex) {

      }
    });
    base.http().handlePost("x", new TreeMap<>(), "{}", null, new Callback<HttpHandler.HttpResult>() {
      @Override
      public void success(HttpHandler.HttpResult value) {
        Assert.assertEquals("post", new String(value.body, StandardCharsets.UTF_8));
        latch.countDown();
      }

      @Override
      public void failure(ErrorCodeException ex) {
      }
    });
    Assert.assertTrue(latch.await(5000, TimeUnit.MILLISECONDS));

  }
}
