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

import org.adamalang.apikit.model.FieldDefinition;
import org.adamalang.apikit.model.Method;
import org.adamalang.apikit.model.ParameterDefinition;
import org.adamalang.common.DefaultCopyright;

import java.util.ArrayList;
import java.util.TreeMap;
import java.util.TreeSet;

public class AssembleServiceDefn {
  public static String assembleAdamaService(Method[] methods) {
    StringBuilder sb = new StringBuilder();
    sb.append(DefaultCopyright.COPYRIGHT_FILE_PREFIX);
    sb.append("package org.adamalang.services;\n\n");
    sb.append("import com.fasterxml.jackson.databind.node.ObjectNode;\n");
    sb.append("import org.adamalang.ErrorCodes;\n");
    sb.append("import org.adamalang.api.*;\n");
    sb.append("import org.adamalang.common.Callback;\n");
    sb.append("import org.adamalang.common.ErrorCodeException;\n");
    sb.append("import org.adamalang.common.Json;\n");
    sb.append("import org.adamalang.internal.InternalSigner;\n");
    sb.append("import org.adamalang.metrics.FirstPartyMetrics;\n");
    sb.append("import org.adamalang.runtime.natives.NtPrincipal;\n");
    sb.append("import org.adamalang.runtime.remote.ServiceConfig;\n");
    sb.append("import org.adamalang.runtime.remote.SimpleService;\n");
    sb.append("import org.slf4j.Logger;\n");
    sb.append("import org.slf4j.LoggerFactory;\n");
    sb.append("import java.util.HashSet;\n");
    sb.append("import java.util.function.Consumer;\n\n");
    sb.append("public class Adama extends SimpleService {\n");
    sb.append("  private static final Logger LOGGER = LoggerFactory.getLogger(Adama.class);\n");
    sb.append("  private final FirstPartyMetrics metrics;\n");
    sb.append("  private final SelfClient client;\n");
    sb.append("  private final InternalSigner signer;\n\n");
    sb.append("  public Adama(FirstPartyMetrics metrics, SelfClient client, InternalSigner signer, ServiceConfig config) throws ErrorCodeException {\n");
    sb.append("    super(\"adama\", new NtPrincipal(\"adama\", \"service\"), true);\n");
    sb.append("    this.client = client;\n");
    sb.append("    this.metrics = metrics;\n");
    sb.append("    this.signer = signer;\n");
    sb.append("  }\n\n");
    sb.append("  public static String definition(int uniqueId, String params, HashSet<String> names, Consumer<String> error) {\n");
    sb.append("    StringBuilder sb = new StringBuilder();\n");
    TreeSet<String> alreadyMade = new TreeSet<>();
    for (Method method : methods) {
      if (!method.genService) continue;
      String reqName = "_Adama" + method.camelName + "Req";
      String resName = "_Adama" + method.responder.camelName + "Res";

      if (!alreadyMade.contains(reqName)) {
        alreadyMade.add(reqName);
        ArrayList<String> args = new ArrayList<>();
        for (ParameterDefinition pd : method.parameters) {
          if ("identity".equals(pd.name)) continue;
          if (pd.optional) {
            args.add("maybe<" + pd.type.adamaType() + "> " + pd.camelName + ";");
          } else {
            args.add(pd.type.adamaType() + " " + pd.camelName + ";");
          }
        }
        sb.append("    sb.append(\"message ").append(reqName).append(" { ").append(String.join(" ", args)).append(" }\\n\");\n");
      }
      if (!alreadyMade.contains(resName)) {
        alreadyMade.add(resName);
        ArrayList<String> args = new ArrayList<>();
        for (FieldDefinition fd : method.responder.fields) {
          args.add(fd.type.adamaType() + " " + fd.camelName);
        }
        sb.append("    sb.append(\"message ").append(resName).append(" { ").append(String.join(" ", args)).append(" }\\n\");\n");
      }
    }
    sb.append("    sb.append(\"service adama {\\n\");\n");
    sb.append("    sb.append(\"  class=\\\"adama\\\";\\n\");\n");
    for (Method method : methods) {
      if (!method.genService) continue;
      sb.append("    sb.append(\"  method secured<").append("_Adama" + method.camelName).append("Req, _Adama").append(method.responder.camelName).append("Res").append((method.responder.stream ? "[]" : "")).append(")> ").append(method.camelName2).append(";\\n\");\n");
    }
    sb.append("    sb.append(\"}\\n\");\n");
    sb.append("    return sb.toString();\n");
    sb.append("  }\n");
    sb.append("  @Override\n");
    sb.append("  public void request(NtPrincipal who, String method, String request, Callback<String> callback) {\n");
    sb.append("    String identity = signer.toIdentity(who);\n");
    sb.append("    ObjectNode requestNode = Json.parseJsonObject(request);\n");
    sb.append("    switch (method) {\n");
    for (Method method : methods) {
      if (!method.genService) continue;
      sb.append("      case \"").append(method.camelName2).append("\": {\n");
      sb.append("        Client").append(method.camelName).append("Request req = new Client").append(method.camelName).append("Request();\n");
      for (ParameterDefinition pd : method.parameters) {
        if ("identity".equals(pd.name)) {
          sb.append("        req.identity = identity;\n");
        } else {
          sb.append("        req.").append(pd.camelName).append(" = Json.").append(pd.type.readerMethod()).append("(requestNode, \"").append(pd.camelName).append("\");\n");
        }
      }
      if (method.responder.stream) {
        // TODO: figure out stream
      } else {
        // FOR EACH THING
        sb.append("        client.").append(method.camelName2).append("(req, new Callback<>() {\n");
        sb.append("          @Override\n");
        sb.append("          public void success(Client").append(method.responder.camelName).append("Response response) {\n");
        sb.append("            callback.success(response.toInternalJson());\n");
        sb.append("          }\n");
        sb.append("          @Override\n");
        sb.append("          public void failure(ErrorCodeException ex) {\n");
        sb.append("            callback.failure(ex);\n");
        sb.append("          }\n");
        sb.append("        });\n");
      }
      sb.append("        return;\n");
      sb.append("      } \n");
    }
    sb.append("      default:\n");
    sb.append("        callback.failure(new ErrorCodeException(ErrorCodes.FIRST_PARTY_SERVICES_METHOD_NOT_FOUND));\n");
    sb.append("    }\n");
    sb.append("  }\n");
    sb.append("}\n");
    return sb.toString();
  }
}
