/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.web.client;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.helpers.NOPLogger;

import java.util.TreeMap;

public class VoidCallbackHttpResponderTests {
  @Test
  public void flow_400() {
    VoidCallbackHttpResponder responder = new VoidCallbackHttpResponder(NOPLogger.NOP_LOGGER, new NoOpMetricsFactory().makeRequestResponseMonitor("x").start(), new Callback<Void>() {
      @Override
      public void success(Void value) {
        Assert.fail();
      }

      @Override
      public void failure(ErrorCodeException ex) {
        Assert.assertEquals(931015, ex.code);
      }
    });
    responder.start(new SimpleHttpResponseHeader(400, new TreeMap<>()));
    responder.bodyStart(32);
    responder.bodyFragment(new byte[32], 0, 32);
    responder.bodyEnd();
  }

  @Test
  public void flow_200() {
    VoidCallbackHttpResponder responder = new VoidCallbackHttpResponder(NOPLogger.NOP_LOGGER, new NoOpMetricsFactory().makeRequestResponseMonitor("x").start(), new Callback<Void>() {
      @Override
      public void success(Void value) {
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
  public void failure() {
    VoidCallbackHttpResponder responder = new VoidCallbackHttpResponder(NOPLogger.NOP_LOGGER, new NoOpMetricsFactory().makeRequestResponseMonitor("x").start(), new Callback<Void>() {
      @Override
      public void success(Void value) {
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
