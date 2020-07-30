/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.netty.client;

import org.adamalang.netty.server.CliServerOptions;
import org.junit.Assert;
import org.junit.Test;

public class AdamaCookieCodecTests {
  @Test
  public void flow() {
    Assert.assertEquals("x=y", AdamaCookieCodec.client("x", "y"));
    AdamaCookieCodec.server(new CliServerOptions(), "x", "z");
  }
}
