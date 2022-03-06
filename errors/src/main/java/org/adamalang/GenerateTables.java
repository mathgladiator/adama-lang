package org.adamalang;

import java.io.File;
import java.lang.reflect.Field;
import java.nio.file.Files;

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
    sb.append("  public final HashSet<Integer> userspace;\n");

    sb.append("  public ErrorTable() {\n");
    sb.append("    names = new HashMap<>();\n");
    sb.append("    descriptions = new HashMap<>();\n");
    sb.append("    userspace = new HashSet<>();\n");
    for (Field f : ErrorCodes.class.getFields()) {
      sb.append("    names.put(").append(f.getInt(null)).append(", \"").append(f.getName()).append("\");\n");
      Description description = f.getAnnotation(Description.class);
      if (description != null) {
        sb.append("    descriptions.put(").append(f.getInt(null)).append(", \"").append(description.value()).append("\");\n");
      } else {
        sb.append("    descriptions.put(").append(f.getInt(null)).append(", \"no description of error (yet)\");\n");
      }
      if (f.getAnnotation(User.class) != null) {
        sb.append("userspace.add(").append(f.getInt(null)).append(");\n");
      }
    }
    sb.append("  }\n");
    sb.append("}\n");
    return sb.toString();
  }

  // TODO: remove and make part of the primary code generator
  public static void main(String[] args) throws Exception {
    Files.writeString(new File("errors/src/main/java/org/adamalang/ErrorTable.java").toPath(), generate());
  }
}
