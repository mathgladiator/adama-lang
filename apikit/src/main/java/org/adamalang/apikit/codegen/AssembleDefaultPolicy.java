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

import org.adamalang.apikit.model.Method;

/** generate a default policy based on the codegen */
public class AssembleDefaultPolicy {
  public static String make_default_policy_as_code(String packageName, Method[] methods) {
    StringBuilder policyDefault = new StringBuilder();
    policyDefault.append("package ").append(packageName).append(";\n");
    policyDefault.append("\n");
    policyDefault.append("import org.adamalang.common.Json;\n");
    policyDefault.append("import com.fasterxml.jackson.databind.node.ObjectNode;\n");
    policyDefault.append("\n");
    policyDefault.append("public class DefaultPolicy {\n");
    policyDefault.append("  public static ObjectNode make() {\n");
    policyDefault.append("    ObjectNode policy = Json.newJsonObject();\n");
    policyDefault.append("    ObjectNode child;\n");
    for (Method method : methods) {
      if (method.internal || method.findBy != null || method.noPolicyAvailable) {
        continue;
      }
      boolean developers = method.defaultPolicyBehavior.contains("Developers");
      policyDefault.append("    child = policy.putObject(\"").append(method.name).append("\");\n");
      policyDefault.append("    child.put(\"developers\",").append(developers ? "true" : "false").append(");\n");
      policyDefault.append("    child.putArray(\"allowed-authorities\");\n");
      policyDefault.append("    child.putArray(\"allowed-documents\");\n");
      policyDefault.append("    child.putArray(\"allowed-document-spaces\");\n");
    }
    policyDefault.append("    return policy;\n");
    policyDefault.append("  }\n");
    policyDefault.append("}\n");
    return policyDefault.toString();
  }
}
