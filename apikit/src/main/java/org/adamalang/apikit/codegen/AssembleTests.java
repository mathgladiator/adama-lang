/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.apikit.codegen;

import org.adamalang.apikit.model.Method;
import org.adamalang.apikit.model.ParameterDefinition;
import org.adamalang.apikit.model.Responder;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class AssembleTests {
  public static Map<String, String> make(String packageName, Method[] methods, Map<String, Responder> responders) throws Exception {
    HashMap<String, String> tests = new HashMap<>();
    {
      StringBuilder sb = new StringBuilder();
      sb.append("package ").append(packageName).append(";\n\n");
      sb.append("import com.fasterxml.jackson.databind.node.ObjectNode;\n");
      sb.append("import org.adamalang.TestFrontEnd;\n");
      sb.append("import org.adamalang.common.Json;\n");
      sb.append("import org.junit.Assert;\n");
      sb.append("import org.junit.Test;\n\n");
      sb.append("import java.util.Iterator;\n\n");
      sb.append("public class GeneratedMissingParameterTest {\n");
      if ("org.adamalang.api".equals(packageName)) { // TODO: this is super lame, but need to sort out split APIs
        sb.append("  @Test\n");
        sb.append("  public void missing() throws Exception {\n");
        sb.append("    try (TestFrontEnd fe = new TestFrontEnd()) {\n");
        sb.append("      ObjectNode node;\n");
        sb.append("      String _identity = fe.setupDevIdentity();\n");
        int rId = 1;
        for (Method method : methods) {
          sb.append("      //").append(method.camelName).append("\n");
          sb.append("      node = Json.newJsonObject();\n");
          sb.append("      node.put(\"id\", ").append(rId).append(");\n");
          sb.append("      node.put(\"method\", \"").append(method.name).append("\");\n");
          for (ParameterDefinition pd : method.parameters) {
            if (!pd.optional) {
              rId++;
              sb.append("      Iterator<String> c").append(rId).append(" = fe.execute(node.toString());\n");
              sb.append("      Assert.assertEquals(\"ERROR:").append(pd.errorCodeIfMissing).append("\", c").append(rId).append(".next());\n");
              sb.append("      node.put(\"").append(pd.name).append("\", ").append(pd.invent()).append(");\n");
            }
          }
        }
        sb.append("    }\n");
        sb.append("  }\n");
      }

      sb.append("}\n");
      tests.put("GeneratedMissingParameterTest.java", sb.toString());
    }
    {
      StringBuilder sb = new StringBuilder();
      sb.append("package ").append(packageName).append(";\n\n");
      sb.append("import com.fasterxml.jackson.databind.node.ObjectNode;\n");
      sb.append("import org.adamalang.common.ErrorCodeException;\n");
      sb.append("import org.adamalang.web.io.JsonResponder;;\n");
      sb.append("import org.junit.Assert;\n");
      sb.append("import org.junit.Test;\n\n");
      sb.append("import java.util.concurrent.atomic.AtomicInteger;\n\n");
      sb.append("public class GeneratedResponderErrorProxyTest {\n");
      sb.append("  @Test\n");
      sb.append("  public void proxy() throws Exception {\n");
      sb.append("    AtomicInteger errorCount = new AtomicInteger(0);\n");
      sb.append("    JsonResponder responder = new JsonResponder() {\n");
      sb.append("      @Override\n");
      sb.append("      public void stream(String json) {\n");
      sb.append("\n");
      sb.append("      }\n");
      sb.append("\n");
      sb.append("      @Override\n");
      sb.append("      public void finish(String json) {\n");
      sb.append("\n");
      sb.append("      }\n");
      sb.append("\n");
      sb.append("      @Override\n");
      sb.append("      public void error(ErrorCodeException ex) {\n");
      sb.append("        errorCount.addAndGet(ex.code);\n");
      sb.append("      }\n");
      sb.append("    };\n");
      int c = 1;
      int sum = 0;
      for (Responder responder : responders.values()) {
        sb.append("    new ").append(responder.camelName).append("Responder(responder).error(new ErrorCodeException(").append(c).append("));\n");
        sum += c;
        c++;
      }
      sb.append("    Assert.assertEquals(").append(sum).append(", errorCount.get());\n");
      sb.append("  }\n");
      sb.append("}\n");
      tests.put("GeneratedResponderErrorProxyTest.java", sb.toString());
    }
    return tests;
  }
}
