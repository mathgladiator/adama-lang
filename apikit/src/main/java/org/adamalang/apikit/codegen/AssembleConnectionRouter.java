/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.apikit.codegen;

import org.adamalang.apikit.model.Common;
import org.adamalang.apikit.model.Method;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;

public class AssembleConnectionRouter {
  public static String make(String packageName, Method[] methods) {
    HashMap<String, ArrayList<Method>> methodsBySubHandler = AssembleHandlers.shred(methods);
    HashSet<String> subHandlers = new HashSet<>(methodsBySubHandler.keySet());

    StringBuilder router = new StringBuilder();
    router.append("package ").append(packageName).append(";\n\n");
    router.append("\n");
    router.append("import com.fasterxml.jackson.databind.node.ObjectNode;\n");
    router.append("import org.adamalang.common.*;\n");
    router.append("import org.adamalang.common.metrics.*;\n");
    router.append("import org.adamalang.connection.*;\n");
    router.append("import org.adamalang.web.io.*;\n");
    router.append("import org.adamalang.ErrorCodes;\n");
    router.append("\n");
    router.append("import java.util.HashMap;\n");
    router.append("import java.util.Map;\n");
    router.append("\n");
    router.append("public class ConnectionRouter {\n");
    router.append("  public final Session session;\n");
    router.append("  public final ConnectionNexus nexus;\n");
    router.append("  public final RootHandler handler;\n");

    for (String subHandler : subHandlers) {
      if (!"Root".equals(subHandler)) {
        router.append("  public final HashMap<Long, ").append(subHandler).append("Handler> inflight").append(subHandler).append(";\n");
      }
    }
    router.append("\n");
    router.append("  public ConnectionRouter(Session session, ConnectionNexus nexus, RootHandler handler) {\n");
    router.append("    this.session = session;\n");
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
    router.append("    nexus.executor.execute(new NamedRunnable(\"disconnect\") {\n");
    router.append("      @Override\n");
    router.append("      public void execute() throws Exception {\n");
    for (String subHandler : subHandlers) {
      if (!"Root".equals(subHandler)) {
        router.append("        for (Map.Entry<Long, ").append(subHandler).append("Handler> entry : inflight").append(subHandler).append(".entrySet()) {\n");
        router.append("          entry.getValue().disconnect(entry.getKey());\n");
        router.append("        }\n");
        router.append("        inflight").append(subHandler).append(".clear();\n");
      }
    }
    router.append("      }\n");
    router.append("    });\n");
    router.append("  }\n");
    router.append("\n");

    router.append("  public void route(JsonRequest request, JsonResponder responder) {\n");
    router.append("    try {\n");
    router.append("      ObjectNode _accessLogItem = Json.newJsonObject();\n");
    router.append("      long requestId = request.id();\n");
    router.append("      String method = request.method();\n");
    router.append("      _accessLogItem.put(\"method\", method);\n");
    router.append("      request.dumpIntoLog(_accessLogItem);\n");
    router.append("      nexus.executor.execute(new NamedRunnable(\"handle\", method) {\n");
    router.append("        @Override\n");
    router.append("        public void execute() throws Exception {\n");
    router.append("          session.activity();\n");
    router.append("          switch (method) {\n");
    for (Method method : methods) {
      router.append("            case \"").append(method.name).append("\": {\n");
      if (method.create != null) {
        router.append("              StreamMonitor.StreamMonitorInstance mInstance = nexus.metrics.monitor_").append(method.camelName).append(".start();\n");
      } else {
        router.append("              RequestResponseMonitor.RequestResponseMonitorInstance mInstance = nexus.metrics.monitor_").append(method.camelName).append(".start();\n");
      }
      router.append("              ").append(method.camelName).append("Request.resolve(session, nexus, request, new Callback<>() {\n");
      router.append("                @Override\n");
      router.append("                public void success(").append(method.camelName).append("Request resolved) {\n");
      router.append("                  resolved.logInto(_accessLogItem);\n");
      if (method.findBy != null) {
        router.append("                  ").append(method.handler).append("Handler handlerToUse = inflight").append(method.handler).append(method.destroy ? ".remove" : ".get").append("(resolved.").append(method.findBy).append(");\n");
        router.append("                  if (handlerToUse != null) {\n");
        router.append("                    handlerToUse.logInto(_accessLogItem);\n");
        router.append("                    handlerToUse.handle(resolved, new ").append(method.responder.camelName).append("Responder(new SimpleMetricsProxyResponder(mInstance, responder, _accessLogItem, nexus.logger)));\n");
        router.append("                  } else {\n");
        router.append("                    _accessLogItem.put(\"success\", false);\n");
        router.append("                    _accessLogItem.put(\"failure-code\", ").append(method.errorCantFindBy).append(");\n");
        router.append("                    nexus.logger.log(_accessLogItem);\n");
        router.append("                    mInstance.failure(").append(method.errorCantFindBy).append(");\n");
        router.append("                    responder.error(new ErrorCodeException(").append(method.errorCantFindBy).append("));\n");
        router.append("                  }\n");
      } else {
        if (method.create != null) {
          router.append("                  ").append(Common.camelize(method.create)).append("Handler handlerMade = handler.handle(session, resolved, new ").append(method.responder.camelName).append("Responder(new JsonResponderHashMapCleanupProxy<>(mInstance, nexus.executor, inflight").append(Common.camelize(method.create)).append(", requestId, responder, _accessLogItem, nexus.logger)));\n");
          router.append("                  ").append("inflight").append(Common.camelize(method.create)).append(".put(requestId, handlerMade);\n");
          router.append("                  ").append("handlerMade.bind();\n");
        } else {
          router.append("                  handler.handle(session, resolved, new ").append(method.responder.camelName).append("Responder(new SimpleMetricsProxyResponder(mInstance, responder, _accessLogItem, nexus.logger)));\n");
        }
      }
      router.append("                }\n");
      router.append("                @Override\n");
      router.append("                public void failure(ErrorCodeException ex) {\n");
      router.append("                  mInstance.failure(ex.code);\n");
      router.append("                  _accessLogItem.put(\"success\", false);\n");
      router.append("                  _accessLogItem.put(\"failure-code\", ex.code);\n");
      router.append("                  nexus.logger.log(_accessLogItem);\n");
      router.append("                  responder.error(ex);\n");
      router.append("                }\n");
      router.append("              });\n");
      router.append("            } return;\n");
    }
    router.append("          }\n");
    router.append("          responder.error(new ErrorCodeException(ErrorCodes.API_METHOD_NOT_FOUND));\n");
    router.append("        }\n");
    router.append("      });\n");
    router.append("    } catch (ErrorCodeException ex) {\n");
    router.append("      responder.error(ex);\n");
    router.append("    }\n");
    router.append("  }\n");
    router.append("}\n");
    return router.toString();
  }
}
