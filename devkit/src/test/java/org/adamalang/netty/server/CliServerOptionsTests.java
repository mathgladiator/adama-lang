/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.netty.server;

import org.junit.Assert;
import org.junit.Test;

public class CliServerOptionsTests {
  @Test
  public void testCrash() {
    var crashes = 0;
    try {
      new CliServerOptions(new String[] { "--port", "-1", "--wss-frame-size", "4200", "--http-max-content-length", "5000", "--wss-timeout-handshake", "25000" });
      Assert.fail();
    } catch (final RuntimeException re) {
      crashes++;
    }
    try {
      new CliServerOptions(new String[] { "--port", "42", "--wss-frame-size", "2", "--http-max-content-length", "5000", "--wss-timeout-handshake", "25000" });
      Assert.fail();
    } catch (final RuntimeException re) {
      crashes++;
    }
    try {
      new CliServerOptions(new String[] { "--port", "7777", "--wss-frame-size", "4200", "--http-max-content-length", "1", "--wss-timeout-handshake", "25000" });
      Assert.fail();
    } catch (final RuntimeException re) {
      crashes++;
    }
    try {
      new CliServerOptions(new String[] { "--port", "7777", "--wss-frame-size", "4200", "--http-max-content-length", "5000", "--wss-timeout-handshake", "1" });
      Assert.fail();
    } catch (final RuntimeException re) {
      crashes++;
    }
    try {
      new CliServerOptions(new String[] { "--wss-path", "foo", "--health-check-path", "/goo" });
      Assert.fail();
    } catch (final RuntimeException re) {
      crashes++;
    }
    try {
      new CliServerOptions(new String[] { "--wss-path", "/foo", "--health-check-path", "goo" });
      Assert.fail();
    } catch (final RuntimeException re) {
      crashes++;
    }
    Assert.assertEquals(6, crashes);
  }

  @Test
  public void testDefaults() {
    final var options = new CliServerOptions(new String[] {});
    Assert.assertEquals(8080, options.port());
    Assert.assertEquals(65536, options.maxWebSocketFrameSize());
    Assert.assertEquals(4194304, options.maxContentLengthSize());
    Assert.assertEquals(2500, options.timeoutWebsocketHandshake());
    Assert.assertEquals("/~socket", options.websocketPath());
    Assert.assertEquals("/~health_check_lb", options.healthCheckPath());
  }

  @Test
  public void testIntegerOverrides() {
    final var options = new CliServerOptions(new String[] { "--port", "7777", "--wss-frame-size", "4200", "--http-max-content-length", "5000", "--wss-timeout-handshake", "25000" });
    Assert.assertEquals(7777, options.port());
    Assert.assertEquals(4200, options.maxWebSocketFrameSize());
    Assert.assertEquals(5000, options.maxContentLengthSize());
    Assert.assertEquals(25000, options.timeoutWebsocketHandshake());
  }

  @Test
  public void testSSLCoverage() {
    new CliServerOptions(new String[] { "--tls-snakeoil", "www.what.com", "--tls-file", "cert.cerf" });
  }

  @Test
  public void testStringOverrides() {
    final var options = new CliServerOptions(new String[] { "--wss-path", "/foo", "--health-check-path", "/goo" });
    Assert.assertEquals("/foo", options.websocketPath());
    Assert.assertEquals("/goo", options.healthCheckPath());
  }
}
