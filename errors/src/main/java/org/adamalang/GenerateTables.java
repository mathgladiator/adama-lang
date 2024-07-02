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
package org.adamalang;

import java.lang.reflect.Field;

public class GenerateTables {
  public static String generate() throws Exception {
    StringBuilder sb = new StringBuilder();
    sb.append("package org.adamalang;\n\n");
    sb.append("import java.util.HashMap;\n");
    sb.append("import java.util.HashSet;\n");
    sb.append("\n");
    sb.append("public class ErrorTable {\n");
    sb.append("  public static final ErrorTable INSTANCE = new ErrorTable();\n");
    sb.append("  public final HashMap<Integer, String> names;\n");
    sb.append("  public final HashMap<Integer, String> descriptions;\n");
    sb.append("  private final HashSet<Integer> userspace;\n");
    sb.append("  private final HashSet<Integer> notproblem;\n");
    sb.append("  private final HashSet<Integer> retry;\n");
    sb.append("\n");
    sb.append("  public boolean shouldRetry(int code) {\n");
    sb.append("    return retry.contains(code);\n");
    sb.append("  }\n");
    sb.append("\n");
    sb.append("  public boolean isUserProblem(int code) {\n");
    sb.append("    return userspace.contains(code);\n");
    sb.append("  }\n");
    sb.append("\n");
    sb.append("  public boolean isNotAProblem(int code) {\n");
    sb.append("    return notproblem.contains(code);\n");
    sb.append("  }\n");
    sb.append("\n");
    sb.append("  public ErrorTable() {\n");
    sb.append("    names = new HashMap<>();\n");
    sb.append("    descriptions = new HashMap<>();\n");
    sb.append("    userspace = new HashSet<>();\n");
    sb.append("    notproblem = new HashSet<>();\n");
    sb.append("    retry = new HashSet<>();\n");
    for (int error : ManualUserTable.ERRORS) {
      sb.append("    userspace.add(").append(error).append(");\n");
    }
    for (Field f : ErrorCodes.class.getFields()) {
      sb.append("    names.put(").append(f.getInt(null)).append(", \"").append(f.getName()).append("\");\n");
      Description description = f.getAnnotation(Description.class);
      if (description != null) {
        sb.append("    descriptions.put(").append(f.getInt(null)).append(", \"").append(description.value()).append("\");\n");
      } else {
        sb.append("    descriptions.put(").append(f.getInt(null)).append(", \"no description of error (yet)\");\n");
      }
      if (f.getAnnotation(User.class) != null) {
        sb.append("    userspace.add(").append(f.getInt(null)).append(");\n");
      }
      if (f.getAnnotation(NotProblem.class) != null) {
        sb.append("    notproblem.add(").append(f.getInt(null)).append(");\n");
      }
      if (f.getAnnotation(RetryInternally.class) != null) {
        sb.append("    retry.add(").append(f.getInt(null)).append(");\n");
      }
    }
    sb.append("  }\n");
    sb.append("}\n");
    return sb.toString();
  }
}
