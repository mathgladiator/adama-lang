/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * which is in the root directory of the repository. This file is part of the 'Adama'
 * project which is a programming language and document store for board games.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2021 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.web.service;

import io.netty.handler.codec.http.*;
import org.adamalang.web.service.mocks.MockHttpCallback;
import org.junit.Test;

public class ProductionStaticSiteHandlerTests {
  @Test
  public void flow() throws Exception {
    FullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/");
    Nexus nexus = NexusTests.mockNexus(NexusTests.Scenario.Prod);
    MockHttpCallback callback = new MockHttpCallback();
    nexus.passthroughHandler.handle(request, new QueryStringDecoder(request.uri()), callback);
    callback.awaitDone();
    callback.assertStatusCode(200);
    callback.assertBody("<!DOCTYPE html>\n" +
            "<html lang=\"en\">\n" +
            "  <head>\n" +
            "    <meta charset=\"UTF-8\">\n" +
            "    <title>Title</title>\n" +
            "  </head>\n" +
            "<body>\n" +
            "  Index\n" +
            "</body>\n" +
            "</html>");
  }

  @Test
  public void index1() throws Exception {
    FullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/p");
    Nexus nexus = NexusTests.mockNexus(NexusTests.Scenario.Prod);
    MockHttpCallback callback = new MockHttpCallback();
    nexus.passthroughHandler.handle(request, new QueryStringDecoder(request.uri()), callback);
    callback.awaitDone();
    callback.assertStatusCode(200);
    callback.assertBody("P-Index");
  }

  @Test
  public void index2() throws Exception {
    FullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/p///");
    Nexus nexus = NexusTests.mockNexus(NexusTests.Scenario.Prod);
    MockHttpCallback callback = new MockHttpCallback();
    nexus.passthroughHandler.handle(request, new QueryStringDecoder(request.uri()), callback);
    callback.awaitDone();
    callback.assertStatusCode(200);
    callback.assertBody("P-Index");
  }

  @Test
  public void file() throws Exception {
    FullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/p/person.htm");
    Nexus nexus = NexusTests.mockNexus(NexusTests.Scenario.Prod);
    MockHttpCallback callback = new MockHttpCallback();
    nexus.passthroughHandler.handle(request, new QueryStringDecoder(request.uri()), callback);
    callback.awaitDone();
    callback.assertStatusCode(200);
    callback.assertBody("This is a person path");
  }

  @Test
  public void special_404() throws Exception {
    FullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/nope");
    Nexus nexus = NexusTests.mockNexus(NexusTests.Scenario.Prod);
    MockHttpCallback callback = new MockHttpCallback();
    nexus.passthroughHandler.handle(request, new QueryStringDecoder(request.uri()), callback);
    callback.awaitDone();
    callback.assertStatusCode(404);
    callback.assertBody("WOAH, Not Found");
  }

  @Test
  public void special_404_not_found() throws Exception {
    FullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/nope");
    Nexus nexus = NexusTests.mockNexus(NexusTests.Scenario.ProdScope);
    MockHttpCallback callback = new MockHttpCallback();
    nexus.passthroughHandler.handle(request, new QueryStringDecoder(request.uri()), callback);
    callback.awaitDone();
    callback.assertErrorCode(404);
  }

  @Test
  public void cantPost() throws Exception {
    FullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, "/");
    Nexus nexus = NexusTests.mockNexus(NexusTests.Scenario.Prod);
    MockHttpCallback callback = new MockHttpCallback();
    nexus.passthroughHandler.handle(request, new QueryStringDecoder(request.uri()), callback);
    callback.awaitDone();
    callback.assertErrorCode(400);
  }
}
