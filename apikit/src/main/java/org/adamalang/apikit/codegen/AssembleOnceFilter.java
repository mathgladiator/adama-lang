/*
* Adama Platform and Language
* Copyright (C) 2021 - 2024 by Adama Platform Engineering, LLC
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

public class AssembleOnceFilter {
  public static String make(String packageName, Method[] unfilteredMethods) {
    StringBuilder filter = new StringBuilder();
    filter.append("package ").append(packageName).append(";\n\n");
    filter.append("public class OnceFilter {\n");
    filter.append("  public static boolean allowed(String method) {\n");
    filter.append("    switch(method) {\n");
    for (Method method : unfilteredMethods) {
      if (method.once) {
        filter.append("      case \"").append(method.name).append("\":\n");
      }
    }
    filter.append("        return true;\n");
    filter.append("      default:\n");
    filter.append("        return false;\n");
    filter.append("    }\n");
    filter.append("  }\n");
    filter.append("}\n");
    return filter.toString();
  }
}
