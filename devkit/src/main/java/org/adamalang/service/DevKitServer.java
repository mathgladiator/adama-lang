/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.service;

import java.io.File;
import java.security.AccessControlException;
import java.util.HashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.adamalang.netty.ServiceHandler;
import org.adamalang.netty.api.AdamaSession;
import org.adamalang.netty.api.GameSpaceDB;
import org.adamalang.netty.client.AdamaCookieCodec;
import org.adamalang.netty.contracts.AuthCallback;
import org.adamalang.netty.contracts.Authenticator;
import org.adamalang.netty.contracts.JsonHandler;
import org.adamalang.netty.contracts.JsonResponder;
import org.adamalang.netty.server.CliServerOptions;
import org.adamalang.netty.server.ServerNexus;
import org.adamalang.netty.server.ServerRunnable;
import org.adamalang.netty.server.UncachedDiskStaticSite;
import org.adamalang.runtime.DurableLivingDocument;
import org.adamalang.runtime.contracts.DataCallback;
import org.adamalang.runtime.contracts.TimeSource;
import org.adamalang.runtime.exceptions.ErrorCodeException;
import org.adamalang.runtime.natives.NtClient;
import org.adamalang.runtime.stdlib.Utility;
import org.adamalang.translator.env.CompilerOptions;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class DevKitServer {
  public static void drive(final ServerRunnable serverRunnable) throws Exception {
    final var server = new Thread(serverRunnable);
    server.start();
    serverRunnable.waitForReady(5000);
    System.out.println("Server ready!");
    server.join();
  }

  private static boolean isString(final JsonNode node) {
    if (node == null || node.isNull()) { return false; }
    return node.isTextual();
  }

  public static void main(final String[] args) throws Exception {
    final var nexus = prepare(args);
    final var serverRunnable = new ServerRunnable(nexus);
    drive(serverRunnable);
    nexus.shutdown();
  }

  public static ServerNexus prepare(final String[] args) throws Exception {
    final var serverOptions = new CliServerOptions(args);
    final var compilerOptions = CompilerOptions.start().args(0, args).make();
    var data = "./data";
    var html = "./html";
    var schema = "./schema";
    for (var k = 0; k + 1 < args.length; k += 2) {
      switch (args[k]) {
        case "--data":
          data = args[k + 1];
          break;
        case "--html":
          html = args[k + 1];
          break;
        case "--schema":
          schema = args[k + 1];
          break;
      }
    }
    final var db = new GameSpaceDB(new File(schema), new File(data), compilerOptions, TimeSource.REAL_TIME);
    final var authenticator = new Authenticator() {
      @Override
      public void authenticate(final String token, final AuthCallback callback) {
        callback.success(new AdamaSession(new NtClient(token, "me")));
      }

      @Override
      public void close() {
      }
    };
    final var serviceHandler = new ServiceHandler(db);
    final var handler = new JsonHandler() {
      @Override
      public void handle(final AdamaSession session, final ObjectNode request, final JsonResponder responder) throws ErrorCodeException {
        final var methodNode = request.get("method");
        if (session == null || methodNode != null && isString(methodNode) && methodNode.textValue().equals("auth")) {
          final var usernameNode = request.get("username");
          final var passwordNode = request.get("password");
          if (isString(methodNode) && isString(usernameNode) && isString(passwordNode)) {
            final var method = methodNode.textValue();
            final var username = usernameNode.textValue();
            final var password = passwordNode.textValue();
            if (method.equals("auth") && password.equals("pw")) {
              final var headers = new HashMap<String, String>();
              headers.put("set-cookie", AdamaCookieCodec.server(serverOptions, AdamaCookieCodec.ADAMA_AUTH_COOKIE_NAME, username));
              responder.respond("{\"retry_connection\":true}", true, headers);
              return;
            }
            responder.failure(new ErrorCodeException(403, new AccessControlException("denied")));
          }
        } else {
          serviceHandler.handle(session, request, responder);
          return;
        }
      }

      @Override
      public void shutdown() {
        serviceHandler.shutdown();
      }
    };
    final var site = new UncachedDiskStaticSite(new File(html));
    return new ServerNexus(serverOptions, db, handler, authenticator, site);
  }
}
