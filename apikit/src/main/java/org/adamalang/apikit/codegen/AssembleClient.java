package org.adamalang.apikit.codegen;

import org.adamalang.apikit.model.Method;

public class AssembleClient {
  public static String make(Method[] methods) throws Exception {
    StringBuilder ts = new StringBuilder();
    ts.append("export class AdamaAPI {\n");
    for (Method method : methods) {
      ts.append("  // ").append(method.camelName).append(" --> ").append(method.name).append("\n\n");
    }
    ts.append("}");
    return ts.toString();
  }
}
