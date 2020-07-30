/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.netty.server;

import org.adamalang.netty.contracts.StaticSite;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;

public class MockStaticSite implements StaticSite {
  @Override
  public FullHttpResponse request(final String path, final HttpResponseStatus status, final FullHttpRequest request) {
    if (path.equals("crash")) { throw new UnsupportedOperationException(); }
    if (path.equals("hello")) { return StaticSite.ofHTML(request, "Hello World"); }
    return null;
  }
}
