/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.apikit.codegen;

import org.adamalang.apikit.model.Common;
import org.adamalang.apikit.model.Method;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class AssembleConnectionRouter {
  public static String make(String packageName, Method[] methods) {
    HashMap<String, ArrayList<Method>> methodsBySubHandler = AssembleHandlers.shred(methods);
    HashSet<String> subHandlers = new HashSet<>(methodsBySubHandler.keySet());

    StringBuilder router = new StringBuilder();
    router.append("package ").append(packageName).append(";\n\n");
    router.append("\n");
    router.append("import org.adamalang.common.Callback;\n");
    router.append("import org.adamalang.common.ErrorCodeException;\n");
    router.append("import org.adamalang.web.io.*;\n");
    router.append("\n");
    router.append("import java.util.HashMap;\n");
    router.append("import java.util.Map;\n");
    router.append("\n");
    router.append("public class ConnectionRouter {\n");
    router.append("  public final ConnectionNexus nexus;\n");
    router.append("  public final RootHandler handler;\n");
    for (String subHandler : subHandlers) {
      if (!"Root".equals(subHandler)) {
        router
            .append("  public final HashMap<Long, ")
            .append(subHandler)
            .append("Handler> inflight")
            .append(subHandler)
            .append(";\n");
      }
    }
    router.append("\n");
    router.append("  public ConnectionRouter(ConnectionNexus nexus, RootHandler handler) {\n");
    router.append("    this.nexus = nexus;\n");
    router.append("    this.handler = handler;\n");
    for (String subHandler : subHandlers) {
      if (!"Root".equals(subHandler)) {
        router.append("    this.inflight").append(subHandler).append(" = new HashMap<>();\n");
      }
    }
    router.append("  }\n");
    router.append("\n");
    router.append("  public void disconnect() {\n");
    for (String subHandler : subHandlers) {
      if (!"Root".equals(subHandler)) {
        router
            .append("    for (Map.Entry<Long, ")
            .append(subHandler)
            .append("Handler> entry : inflight")
            .append(subHandler)
            .append(".entrySet()) {\n");
        router.append("      entry.getValue().disconnect(entry.getKey());\n");
        router.append("    }\n");
        router.append("    inflight").append(subHandler).append(".clear();\n");
      }
    }
    router.append("  }\n");
    router.append("\n");
    router.append("  public void route(JsonRequest request, JsonResponder responder) {\n");
    router.append("    try {\n");
    router.append("      long requestId = request.id();\n");
    router.append("      String method = request.method();\n");
    router.append("      nexus.executor.execute(() -> {\n");
    router.append("        switch (method) {\n");
    for (Method method : methods) {
      router.append("          case \"").append(method.name).append("\": {\n");
      router
          .append("            ")
          .append(method.camelName)
          .append("Request.resolve(nexus, request, new Callback<>() {\n");
      router.append("              @Override\n");
      router
          .append("              public void success(")
          .append(method.camelName)
          .append("Request resolved) {\n");
      if (method.findBy != null) {
        router
            .append("                ")
            .append(method.handler)
            .append("Handler handlerToUse = inflight")
            .append(method.handler)
            .append(method.destroy ? ".remove" : ".get")
            .append("(resolved.")
            .append(method.findBy)
            .append(");\n");
        router.append("                if (handlerToUse != null) {\n");
        router
            .append("                  handlerToUse.handle(resolved, new ")
            .append(method.responder.camelName)
            .append("Responder(responder));\n");
        router.append("                } else {\n");
        router
            .append("                  responder.error(new ErrorCodeException(")
            .append(method.errorCantFindBy)
            .append("));\n");
        router.append("                }\n");
      } else {
        if (method.create != null) {
          router
              .append("                ")
              .append(Common.camelize(method.create))
              .append("Handler handlerMade = handler.handle(resolved, new ")
              .append(method.responder.camelName)
              .append("Responder(new JsonResponderHashMapCleanupProxy<>(nexus.executor, inflight")
              .append(Common.camelize(method.create))
              .append(", requestId, responder)));\n");
          router
              .append("                ")
              .append("inflight")
              .append(Common.camelize(method.create))
              .append(".put(requestId, handlerMade);\n");
          router.append("                ").append("handlerMade.bind();\n");
        } else {
          router
              .append("                handler.handle(resolved, new ")
              .append(method.responder.camelName)
              .append("Responder(responder));\n");
        }
      }
      router.append("              }\n");
      router.append("              @Override\n");
      router.append("              public void failure(ErrorCodeException ex) {\n");
      router.append("                responder.error(ex);\n");
      router.append("              }\n");
      router.append("            });\n");
      router.append("          } return;\n");
    }
    router.append("        }\n");
    router.append("        responder.error(new ErrorCodeException(42));\n"); // TODO: need an error
    router.append("      });\n");
    router.append("    } catch (ErrorCodeException ex) {\n");
    router.append("      responder.error(ex);\n");
    router.append("    }\n");
    router.append("  }\n");
    router.append("}\n");
    return router.toString();
  }
}
