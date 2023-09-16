/*
* Adama Platform and Language
* Copyright (C) 2021 - 2023 by Adama Platform Initiative, LLC
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as published
* by the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Affero General Public License for more details.
* 
* You should have received a copy of the GNU Affero General Public License
* along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
package org.adamalang.apikit.codegen;

import org.adamalang.apikit.model.Common;
import org.adamalang.apikit.model.Method;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;

public class AssembleConnectionRouter {
  public static String make(String packageName, String sessionImport, String prefix,  Method[] methods) {
    HashMap<String, ArrayList<Method>> methodsBySubHandler = AssembleHandlers.shred(methods);
    HashSet<String> subHandlers = new HashSet<>(methodsBySubHandler.keySet());

    StringBuilder router = new StringBuilder();
    router.append("package ").append(packageName).append(";\n\n");
    router.append("\n");
    router.append("import com.fasterxml.jackson.databind.node.ObjectNode;\n");
    router.append("import org.adamalang.common.*;\n");
    router.append("import org.adamalang.common.metrics.*;\n");
    router.append("import org.adamalang.contracts.data.DefaultPolicyBehavior;\n");
    router.append("import " + sessionImport + ";\n");
    router.append("import org.adamalang.web.io.*;\n");
    router.append("import org.adamalang.ErrorCodes;\n");
    router.append("\n");
    router.append("import java.util.HashMap;\n");
    router.append("import java.util.Map;\n");
    router.append("\n");
    router.append("public class ").append(prefix).append("ConnectionRouter {\n");
    router.append("  public final Session session;\n");
    router.append("  public final ").append(prefix).append("ConnectionNexus nexus;\n");
    router.append("  public final Root").append(prefix).append("Handler handler;\n");

    for (String subHandler : subHandlers) {
      if (!subHandler.startsWith("Root")) {
        router.append("  public final HashMap<Long, ").append(subHandler).append("Handler> inflight").append(subHandler).append(";\n");
      }
    }
    router.append("\n");
    router.append("  public ").append(prefix).append("ConnectionRouter(Session session, ").append(prefix).append("ConnectionNexus nexus, Root").append(prefix).append("Handler handler) {\n");
    router.append("    this.session = session;\n");
    router.append("    this.nexus = nexus;\n");
    router.append("    this.handler = handler;\n");
    for (String subHandler : subHandlers) {
      if (!subHandler.startsWith("Root")) {
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
      if (!subHandler.startsWith("Root")) {
        router.append("        for (Map.Entry<Long, ").append(subHandler).append("Handler> entry : inflight").append(subHandler).append(".entrySet()) {\n");
        router.append("          entry.getValue().disconnect(entry.getKey());\n");
        router.append("        }\n");
        router.append("        inflight").append(subHandler).append(".clear();\n");
      }
    }
    router.append("        handler.disconnect();\n");
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
      if (method.checkPolicy) {
        router.append("                  if (!resolved.policy.checkPolicy(\"").append(method.name).append("\", DefaultPolicyBehavior.").append(method.defaultPolicyBehavior).append(", resolved.who)) {\n");
        router.append("                    responder.error(new ErrorCodeException(").append(method.policyErrorCode).append("));\n");
        router.append("                    return;\n");
        router.append("                  }\n");
      }
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
          router.append("                  if (handlerMade != null) {\n");
          router.append("                    ").append("inflight").append(Common.camelize(method.create)).append(".put(requestId, handlerMade);\n");
          router.append("                    ").append("handlerMade.bind();\n");
          router.append("                  }\n");
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
