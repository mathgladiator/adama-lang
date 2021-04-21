/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * which is in the root directory of the repository. This file is part of the 'Adama'
 * project which is a programming language and document store for board games.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2021 by Jeffrey M. Barber (http://jeffrey.io)
*/
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
