/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.web.client;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.helpers.NOPLogger;

import java.util.TreeMap;

public class StringCallbackHttpResponderTests {
  @Test
  public void flow_400() {
    StringCallbackHttpResponder responder = new StringCallbackHttpResponder(NOPLogger.NOP_LOGGER, new NoOpMetricsFactory().makeRequestResponseMonitor("x").start(), new Callback<String>() {
      @Override
      public void success(String value) {
        Assert.fail();
      }

      @Override
      public void failure(ErrorCodeException ex) {
        Assert.assertEquals(979143, ex.code);
      }
    });
    responder.start(new SimpleHttpResponseHeader(400, new TreeMap<>()));
    responder.bodyStart(32);
    responder.bodyFragment(new byte[32], 0, 32);
    responder.bodyEnd();
  }

  @Test
  public void flow_200() {
    StringCallbackHttpResponder responder = new StringCallbackHttpResponder(NOPLogger.NOP_LOGGER, new NoOpMetricsFactory().makeRequestResponseMonitor("x").start(), new Callback<String>() {
      @Override
      public void success(String value) {
        Assert.assertEquals("Hello World", value);
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
    StringCallbackHttpResponder responder = new StringCallbackHttpResponder(NOPLogger.NOP_LOGGER, new NoOpMetricsFactory().makeRequestResponseMonitor("x").start(), new Callback<String>() {
      @Override
      public void success(String value) {
        Assert.assertEquals("Hello World", value);
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
    StringCallbackHttpResponder responder = new StringCallbackHttpResponder(NOPLogger.NOP_LOGGER, new NoOpMetricsFactory().makeRequestResponseMonitor("x").start(), new Callback<String>() {
      @Override
      public void success(String value) {
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
