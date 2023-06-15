/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.codegen;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.common.Json;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** dumb way to build a service */
public class SimpleOpenAPICodeGen {
  private static final String BEGIN_CODEGEN_METHODS = "/** BEGIN[CODEGEN-METHODS] **/";
  private static final String END_CODEGEN_METHODS = "/** END[CODEGEN-METHODS] **/";
  private static final String BEGIN_CODEGEN_DEFN = "/** BEGIN[CODEGEN-DEFN] **/";
  private static final String END_CODEGEN_DEFN = "/** END[CODEGEN-DEFN] **/";
  private static final Pattern VAREXTRACT = Pattern.compile("\\{([a-z0-9A-Z_]*)\\}");

  public static void main(String[] args) throws Exception {
    update("billing/Stripe", "stripe.json");
  }

  private static void update(String file, String spec) throws Exception {
    ObjectNode tree = Json.parseJsonObject(Files.readString(new File("services/openapi-specs/" + spec).toPath()));
    ObjectNode paths = (ObjectNode) tree.get("paths");
    Iterator<Map.Entry<String, JsonNode>> pathIt = paths.fields();
    StringBuilder methods = new StringBuilder();
    StringBuilder defn = new StringBuilder();
    String tab = "      ";
    defn.append("\n");
    methods.append("\n");
    while (pathIt.hasNext()) {
      Map.Entry<String, JsonNode> pathMethods = pathIt.next();
      String pathUrl = pathMethods.getKey();
      Iterator<Map.Entry<String, JsonNode>> pathMethodIt = pathMethods.getValue().fields();
      while (pathMethodIt.hasNext()) {
        Map.Entry<String, JsonNode> pathMethod = pathMethodIt.next();
        String method = pathMethod.getKey();
        String operationId = pathMethod.getValue().get("operationId").textValue();
        defn.append("    sb.append(\"  method<dynamic, dynamic> ").append(operationId).append(";\\n\");\n");
        methods.append(tab + "case \"").append(operationId).append("\":\n");
        String outputURL = "\"" + pathUrl + "\"";
        Matcher matcher = VAREXTRACT.matcher(outputURL);
        while (matcher.find()) {
          String name = matcher.group(1);
          outputURL = outputURL.replaceAll(Pattern.quote("{" + matcher.group(1) + "}"), "\" + Json.readString(node, \"" + name + "\") + \"");
          matcher = VAREXTRACT.matcher(outputURL);
        }
        if (outputURL.endsWith("+ \"\"")) {
          outputURL = outputURL.substring(0, outputURL.length() - 4).trim();
        }
        methods.append(tab + "  invoke(\"").append(method.toUpperCase()).append("\", ").append(outputURL).append(", node, callback);\n");
        methods.append(tab + "  return;\n");
      }
    }
    Path path = new File("services/src/main/java/org/adamalang/services/" + file + ".java").toPath();
    String result = Files.readString(path);
    {
      int kBegin = result.indexOf(BEGIN_CODEGEN_METHODS);
      int kEnd = result.indexOf(END_CODEGEN_METHODS);
      if (kBegin < 0 || kEnd < 0) {
        throw new Exception("malformed input:" + file);
      }
      result = result.substring(0, kBegin) + BEGIN_CODEGEN_METHODS + methods + result.substring(kEnd);
    }
    {
      int kBegin = result.indexOf(BEGIN_CODEGEN_DEFN);
      int kEnd = result.indexOf(END_CODEGEN_DEFN);
      if (kBegin < 0 || kEnd < 0) {
        throw new Exception("malformed input:" + file);
      }
      result = result.substring(0, kBegin) + BEGIN_CODEGEN_DEFN + defn + result.substring(kEnd);
    }
    Files.writeString(path, result);
  }
}
