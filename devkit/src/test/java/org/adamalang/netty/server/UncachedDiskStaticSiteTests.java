/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.netty.server;

import java.io.File;
import java.nio.file.Files;
import org.junit.Assert;
import org.junit.Test;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

public class UncachedDiskStaticSiteTests {
  private static final FullHttpRequest REQ_BASE = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "");

  @Test
  public void test() throws Exception {
    final var root = new File("./test_data");
    root.mkdir();
    final var udss = new UncachedDiskStaticSite(root);
    final var index = new File("./test_data/index.html");
    Files.writeString(index.toPath(), "Oh, Hello There");
    Assert.assertNotNull(udss.request("", HttpResponseStatus.OK, REQ_BASE));
    Assert.assertNotNull(udss.request("index.html", HttpResponseStatus.OK, REQ_BASE));
    Assert.assertNull(udss.request("../../password", HttpResponseStatus.OK, REQ_BASE));
    Assert.assertNull(udss.request("404.html", HttpResponseStatus.OK, REQ_BASE));
  }
}
