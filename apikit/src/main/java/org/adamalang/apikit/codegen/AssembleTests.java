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

import org.adamalang.apikit.model.Method;
import org.adamalang.apikit.model.ParameterDefinition;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class AssembleTests {
  public static Map<String, String> make(String packageName, Method[] methods) throws Exception {
    HashMap<String, String> tests = new HashMap<>();
    StringBuilder sb = new StringBuilder();
    sb.append("package ").append(packageName).append(";\n\n");
    sb.append("import com.fasterxml.jackson.databind.node.ObjectNode;\n");
    sb.append("import org.adamalang.TestFrontEnd;\n");
    sb.append("import org.adamalang.common.Json;\n");
    sb.append("import org.junit.Assert;\n");
    sb.append("import org.junit.Test;\n\n");
    sb.append("import java.util.Iterator;\n\n");
    sb.append("public class GeneratedMissingParameterTest {\n");
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
    sb.append("}\n");
    tests.put("GeneratedMissingParameterTest.java", sb.toString());
    return tests;
  }
}
