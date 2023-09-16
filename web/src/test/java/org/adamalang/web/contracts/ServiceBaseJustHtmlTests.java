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
      public void handleOptions(String uri, TreeMap<String, String> headers, String parametersJson, Callback<HttpResult> callback) {
        callback.success(new HttpResult("","".getBytes(StandardCharsets.UTF_8), uri.equalsIgnoreCase("/opt=yes")));
      }

      @Override
      public void handleGet(String uri, TreeMap<String, String> headers, String parametersJson, Callback<HttpResult> callback) {
        callback.success(new HttpResult("yay", "yay".getBytes(StandardCharsets.UTF_8), true));
      }

      @Override
      public void handleDelete(String uri, TreeMap<String, String> headers, String parametersJson, Callback<HttpResult> callback) {
        callback.success(new HttpResult("yay", "yay".getBytes(StandardCharsets.UTF_8), true));
      }

      @Override
      public void handlePost(String uri, TreeMap<String, String> headers, String parametersJson, String body, Callback<HttpResult> callback) {
        callback.success(new HttpResult("post", "post".getBytes(StandardCharsets.UTF_8), true));
      }

      @Override
      public void handleDeepHealth(Callback<String> callback) {
        callback.success("COVERAGE");
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
    base.assets();
    CountDownLatch latch = new CountDownLatch(4);
    base.http().handleOptions("/opt=yes", new TreeMap<>(), "{}", new Callback<HttpHandler.HttpResult>() {
      @Override
      public void success(HttpHandler.HttpResult value) {
        Assert.assertTrue(value.cors);
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
    base.http().handleDelete("x", new TreeMap<>(), "{}", new Callback<HttpHandler.HttpResult>() {
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
