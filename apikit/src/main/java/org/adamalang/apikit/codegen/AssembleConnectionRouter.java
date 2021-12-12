package org.adamalang.apikit.codegen;

import org.adamalang.apikit.model.Common;
import org.adamalang.apikit.model.Lookup;
import org.adamalang.apikit.model.Method;
import org.adamalang.apikit.model.ParameterDefinition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class AssembleConnectionRouter {
    public static String make(String packageName, Method[] methods) {
        HashMap<String, ArrayList<Method>> methodsBySubHandler = AssembleHandlers.shred(methods);
        HashSet<String> subHandlers = new HashSet<>(methodsBySubHandler.keySet());

        StringBuilder router = new StringBuilder();
        router.append("package ").append(packageName).append(";\n\n");
        router.append("\n");
        router.append("import org.adamalang.runtime.contracts.Callback;\n");
        router.append("import org.adamalang.runtime.exceptions.ErrorCodeException;\n");
        router.append("import org.adamalang.web.io.*;\n");
        router.append("\n");
        router.append("import java.util.HashMap;\n");
        router.append("import java.util.Map;\n");
        router.append("\n");
        router.append("class ConnectionRouter {\n");
        router.append("  public final ConnectionNexus nexus;\n");
        router.append("  public final RootHandler handler;\n");
        for (String subHandler : subHandlers) {
            router.append("  public final HashMap<Long, ").append(subHandler).append("Handler> inflight").append(subHandler).append(";\n");
        }
        router.append("\n");
        router.append("  public ConnectionRouter(ConnectionNexus nexus, RootHandler handler) {\n");
        router.append("    this.nexus = nexus;\n");
        router.append("    this.handler = handler;\n");
        for (String subHandler : subHandlers) {
            router.append("    this.inflight").append(subHandler).append(" = new HashMap<>();\n");
        }
        router.append("  }\n");
        router.append("\n");
        router.append("  public void disconnect() {\n");
        for (String subHandler : subHandlers) {
            for (Method method : methodsBySubHandler.get(subHandler)) {
                if (method.callOnDisconnect) {
                    router.append("    for (Map.Entry<Long, ").append(subHandler).append("Handler> entry : inflight").append(subHandler).append(".entrySet()) {\n");
                    router.append("      entry.getValue().handle(new ").append(method.camelName).append("Request(entry.getKey()), new ").append(method.responder.camelName).append("Responder(new NoOpJsonResponder()));\n");
                    router.append("    }\n");
                }
            }
        }
        router.append("  }\n");
        router.append("\n");
        router.append("  public void route(JsonRequest request, JsonResponder responder)  throws ErrorCodeException {\n");
        router.append("    long requestId = request.id();\n");
        router.append("    String method = request.method();\n");
        router.append("    nexus.executor.execute(() -> {\n");
        router.append("      switch (method) {\n");
        for (Method method : methods) {
            router.append("        case \"").append(method.name).append("\": {\n");

            router.append("          ").append(method.camelName).append("Request.resolve(nexus, request, new Callback<>() {\n");
            router.append("            @Override\n");
            router.append("            public void success(").append(method.camelName).append("Request resolved) {\n");
            if (method.findBy != null) {
                router.append("              ").append(method.handler).append("Handler handlerToUse = inflight").append(method.handler).append(method.destroy ? ".remove" : ".get").append("(resolved.").append(method.findBy).append(");\n");
                router.append("              if (handlerToUse != null) {\n");
                router.append("                handlerToUse.handle(resolved, new ").append(method.responder.camelName).append("Responder(responder));\n");
                router.append("              } else {\n");
                router.append("                responder.error(new ErrorCodeException(").append(method.errorCantFindBy).append("));\n");
                router.append("              }\n");
            } else {
                if (method.create != null) {
                    router.append("              ").append("inflight").append(Common.camelize(method.create)).append(".put(requestId, handler.handle(resolved, new ").append(method.responder.camelName).append("Responder(new JsonResponderHashMapCleanupProxy<>(nexus.executor, inflight").append(Common.camelize(method.create)).append(", requestId, responder))));\n");
                } else {
                    router.append("              handler.handle(resolved, new ").append(method.responder.camelName).append("Responder(responder));\n");
                }
            }
            router.append("            }\n");
            router.append("            @Override\n");
            router.append("            public void failure(ErrorCodeException ex) {\n");
            router.append("              responder.error(ex);\n");
            router.append("            }\n");
            router.append("          });\n");
            router.append("        } return;\n");
        }
        router.append("      }\n");
        router.append("    });\n");
        router.append("  }\n");
        router.append("}\n");
        return router.toString();

    }
}