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
import org.adamalang.web.io.ConnectionContext;
import org.junit.Assert;
import org.junit.Test;

import java.util.TreeMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class NullHttpHandlerTests {
  @Test
  public void coverage() throws Exception {
    CountDownLatch latch = new CountDownLatch(5);
    ConnectionContext context = new ConnectionContext("origin", "remote-ip", "user-agent", new TreeMap<>());
    HttpHandler.NULL.handle(context, HttpHandler.Method.GET, "", "", new TreeMap<>(), "{}", "body", new Callback<HttpHandler.HttpResult>() {
      @Override
      public void success(HttpHandler.HttpResult value) {
        latch.countDown();
      }

      @Override
      public void failure(ErrorCodeException ex) {

      }
    });
    HttpHandler.NULL.handle(context, HttpHandler.Method.PUT, "", "", new TreeMap<>(), "{}", "body", new Callback<HttpHandler.HttpResult>() {
      @Override
      public void success(HttpHandler.HttpResult value) {
        latch.countDown();
      }

      @Override
      public void failure(ErrorCodeException ex) {

      }
    });
    HttpHandler.NULL.handle(context, HttpHandler.Method.DELETE, "", "", new TreeMap<>(), "{}", "body", new Callback<HttpHandler.HttpResult>() {
      @Override
      public void success(HttpHandler.HttpResult value) {
        latch.countDown();
      }

      @Override
      public void failure(ErrorCodeException ex) {

      }
    });
    HttpHandler.NULL.handle(context, HttpHandler.Method.OPTIONS, "", "", new TreeMap<>(), "{}", "body", new Callback<HttpHandler.HttpResult>() {
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
    Assert.assertTrue(latch.await(5000, TimeUnit.MILLISECONDS));
  }
}
