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

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.netty.channel.EventLoopGroup;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.QueryStringDecoder;
import org.adamalang.api.AdamaService;
import org.adamalang.api.auth.AuthenticatorCallback;
import org.adamalang.api.auth.AuthenticatorService;
import org.adamalang.api.commands.Request;
import org.adamalang.api.commands.contracts.CommandResponder;
import org.adamalang.api.session.ImpersonatedSession;
import org.adamalang.api.session.Session;
import org.adamalang.api.session.UserSession;
import org.adamalang.api.util.Json;
import org.adamalang.runtime.contracts.Callback;
import org.adamalang.runtime.exceptions.ErrorCodeException;
import org.adamalang.runtime.natives.NtClient;
import org.junit.Test;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class NexusTests {

  public static enum Scenario {
    Mock1(15000),
    Mock2(15001),
    Mock3(15002),
    Dev(15003),
    Prod(15004),
    DevScope(15005),
    ProdScope(15006);

    public final int port;
    private Scenario(int port) {
      this.port = port;
    }
  }

  public static Nexus mockNexus(Scenario scenario) throws Exception {
    return mockNexus(scenario, null);
  }
  public static Nexus mockNexus(Scenario scenario, EventLoopGroup group) throws Exception {
    ObjectNode configNode = Json.newJsonObject();
    if (scenario == Scenario.DevScope || scenario == Scenario.ProdScope) {
      configNode.put("http_static_file_path", "./test_static/p");
    } else {
      configNode.put("http_static_file_path", "./test_static");
    }
    if (scenario == Scenario.Prod || scenario == Scenario.ProdScope) {
      configNode.put("production", true);
    }
    configNode.put("http_port", scenario.port);

    Config config = new Config(configNode);
    HashMap<String, UriHandler> handlers = new HashMap<>();

    // TODO: add some interesting handlers here
    handlers.put("/ex_500", new UriHandler() {
      @Override
      public void handle(FullHttpRequest input, QueryStringDecoder queryStringDecoder, Callback<FullHttpResponse> callback) {
        callback.failure(new ErrorCodeException(500));
      }
    });

    AuthenticatorService mockAuth = new AuthenticatorService() {
      @Override
      public void authenticateByToken(String token, AuthenticatorCallback callback) {
        if ("crash".equals(token)) {
          throw new NullPointerException();
        }
        if ("slow".equals(token)) {
          group.schedule(() -> {
            callback.success(token, new UserSession(new NtClient(token, "mock")));
          }, 1500, TimeUnit.MILLISECONDS);
          return;
        }
        if ("null".equals(token)) {
          callback.success(null, null);
          return;
        }
        if ("bad".equals(token)) {
          callback.failure();
        } else {
          callback.success(token, new UserSession(new NtClient(token, "mock")));
        }
      }

      @Override
      public void authenticateByEmail(String email, String password, AuthenticatorCallback callback) {
        if ("bad".equals(email) || !"pw".equals(password)) {
          callback.failure();
        } else {
          callback.success(email, new UserSession(new NtClient(email, "mock")));
        }
      }

      @Override
      public void authenticateImpersonation(Session session, NtClient other, AuthenticatorCallback callback) {
        if ("crash".equals(other.agent)) {
          throw new NullPointerException();
        }
        if ("free".equals(other.agent)) {
          callback.success(null, new ImpersonatedSession(other, session));
        } else {
          callback.failure();
        }
      }
    };
    AdamaService mockService = new AdamaService() {
      @Override
      public void handle(Session session, Request request, CommandResponder responder) {
        try {
          if ("single".equals(request.method())) {
            responder.finish("{\"once\":1}");
          }
          if ("stream".equals(request.method())) {
            responder.stream("{\"s\":1}");
            responder.stream("{\"s\":2}");
            responder.finish("{\"s\":3}");
          }
        } catch (ErrorCodeException ex) {
          responder.error(ex);
        }
      }
    };
    return new Nexus(config, handlers, config.produceStaticHandler(), mockAuth, mockService);
  }

  @Test
  public void coverage() throws Exception {
    mockNexus(Scenario.Mock1);
    mockNexus(Scenario.Mock2);
    mockNexus(Scenario.Mock3);
    mockNexus(Scenario.Dev);
    mockNexus(Scenario.Prod);
    mockNexus(Scenario.DevScope);
    mockNexus(Scenario.ProdScope);
  }
}
