/*
* Adama Platform and Language
* Copyright (C) 2021 - 2024 by Adama Platform Engineering, LLC
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
package org.adamalang.web.client;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.helpers.NOPLogger;

import java.nio.charset.StandardCharsets;
import java.util.TreeMap;

public class ByteArrayCallbackHttpResponderTests {
  @Test
  public void flow_400() {
    ByteArrayCallbackHttpResponder responder = new ByteArrayCallbackHttpResponder(NOPLogger.NOP_LOGGER, new NoOpMetricsFactory().makeRequestResponseMonitor("x").start(), new Callback<byte[]>() {
      @Override
      public void success(byte[] value) {
        Assert.fail();
      }

      @Override
      public void failure(ErrorCodeException ex) {
        Assert.assertEquals(924928, ex.code);
      }
    });
    responder.start(new SimpleHttpResponseHeader(400, new TreeMap<>()));
    responder.bodyStart(32);
    responder.bodyFragment(new byte[32], 0, 32);
    responder.bodyEnd();
  }

  @Test
  public void flow_200() {
    ByteArrayCallbackHttpResponder responder = new ByteArrayCallbackHttpResponder(NOPLogger.NOP_LOGGER, new NoOpMetricsFactory().makeRequestResponseMonitor("x").start(), new Callback<byte[]>() {
      @Override
      public void success(byte[] value) {
        Assert.assertEquals("Hello World", new String(value, StandardCharsets.UTF_8));
      }

      @Override
      public void failure(ErrorCodeException ex) {
        Assert.fail();
      }
    });
    responder.start(new SimpleHttpResponseHeader(200, new TreeMap<>()));
    byte[] hello = "Hello World".getBytes();
    responder.bodyStart(hello.length);
    responder.bodyFragment(hello, 0, hello.length);
    responder.bodyEnd();
  }

  @Test
  public void flow_200_fragments() {
    ByteArrayCallbackHttpResponder responder = new ByteArrayCallbackHttpResponder(NOPLogger.NOP_LOGGER, new NoOpMetricsFactory().makeRequestResponseMonitor("x").start(), new Callback<byte[]>() {
      @Override
      public void success(byte[] value) {
        Assert.assertEquals("Hello World", new String(value, StandardCharsets.UTF_8));
      }

      @Override
      public void failure(ErrorCodeException ex) {
        Assert.fail();
      }
    });
    responder.start(new SimpleHttpResponseHeader(200, new TreeMap<>()));
    byte[] hello = "Hello World".getBytes();
    responder.bodyStart(hello.length);
    responder.bodyFragment(hello, 0, 5);
    responder.bodyFragment(hello, 5, hello.length - 5);
    responder.bodyEnd();
  }

  @Test
  public void failure() {
    ByteArrayCallbackHttpResponder responder = new ByteArrayCallbackHttpResponder(NOPLogger.NOP_LOGGER, new NoOpMetricsFactory().makeRequestResponseMonitor("x").start(), new Callback<byte[]>() {
      @Override
      public void success(byte[] value) {
        Assert.fail();
      }

      @Override
      public void failure(ErrorCodeException ex) {
        Assert.assertEquals(123, ex.code);
      }
    });
    responder.failure(new ErrorCodeException(123));
  }
}
