/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.apikit.codegen;

import org.adamalang.apikit.model.Common;
import org.adamalang.apikit.model.FieldDefinition;
import org.adamalang.apikit.model.Method;
import org.adamalang.apikit.model.ParameterDefinition;

import java.util.Locale;
import java.util.regex.Pattern;

public class AssembleAPIDocs {

  public static String docify(Method[] methods) {
    StringBuilder markdown = new StringBuilder();
    markdown.append("# API Reference \n");
    markdown.append(" Methods: \n");
    {
      boolean notfirst = false;
      for (Method method : methods) {
        if (method.internal) {
          continue;
        }

        if (notfirst) {
          markdown.append(", ");
        }
        notfirst = true;
        markdown.append("[").append(method.camelName).append("](#method-").append(method.camelName.toLowerCase(Locale.ROOT)).append(")");
      }
    }
    markdown.append("\n");
    for (Method method : methods) {
      if (method.internal) {
        continue;
      }

      boolean specialHandler = !method.handler.startsWith("Root");
      markdown.append("\n## Method: ").append(method.camelName).append("\n");
      for (String ln : method.documentation.trim().split(Pattern.quote("\n"))) {
        markdown.append(ln.trim()).append("\n");
      }
      if (method.parameters.length > 0) {
        markdown.append("\n");
        markdown.append("### Parameters\n");
        markdown.append("| name | required | type | documentation |\n");
        markdown.append("| --- | --- | --- | --- |\n");
        for (ParameterDefinition pd : method.parameters) {
          if (specialHandler && pd.name.equals(method.findBy)) {
            continue;
          }
          markdown.append("| ").append(pd.name).append(" | ").append(pd.optional ? "no" : "yes").append(" | ").append(pd.type.javaType()).append(" | ").append(String.join(" ", pd.documentation.split(Pattern.quote("\n"))).trim()).append(" |\n");
        }
        markdown.append("\n");
      } else {
        markdown.append("This method has no parameters.\n");
      }

      markdown.append("\n");
      markdown.append("### Template\n");
      markdown.append("```js\n");
      String methodNameToUse = method.name;
      if (methodNameToUse.contains("/") && specialHandler) {
        methodNameToUse = methodNameToUse.substring(methodNameToUse.indexOf('/') + 1);
      }
      if (specialHandler) {
        markdown.append("stream.").append(Common.camelize(methodNameToUse)).append("(");
      } else {
        markdown.append("connection.").append(Common.camelize(methodNameToUse)).append("(");
      }
      boolean notfirst = false;
      for (ParameterDefinition pd : method.parameters) {
        if (specialHandler && pd.name.equals(method.findBy)) {
          continue;
        }
        if (notfirst) {
          markdown.append(", ");
        }
        notfirst = true;
        markdown.append(pd.name);
      }
      if (notfirst) {
        markdown.append(", ");
      }
      markdown.append("{\n");
      if (method.responder.stream) {
        markdown.append("  next: function(payload) {\n");
        for (FieldDefinition fd : method.responder.fields) {
          markdown.append("    // payload.").append(fd.camelName).append("\n");
        }
        markdown.append("  },\n");
        markdown.append("  complete: function() {\n");
        markdown.append("  },\n");
        markdown.append("  failure: function(reason) {\n");
        markdown.append("  }\n");
        markdown.append("});\n");
      } else {
        if (method.responder.fields.length > 0) {
          markdown.append("  success: function(response) {\n");
          for (FieldDefinition fd : method.responder.fields) {
            markdown.append("    // response.").append(fd.camelName).append("\n");
          }
        } else {
          markdown.append("  success: function() {\n");
        }
        markdown.append("  },\n");
        markdown.append("  failure: function(reason) {\n");
        markdown.append("  }\n");
        markdown.append("});\n");
      }
      markdown.append("```\n");
      markdown.append("\n");
      if (method.responder.fields.length > 0) {
        markdown.append("\n");
        if (method.responder.stream) {
          markdown.append("### Streaming payload fields\n");
        } else {
          markdown.append("### Request response fields\n");
        }
        markdown.append("| name | type | documentation |\n");
        markdown.append("| --- | --- | --- |\n");
        for (FieldDefinition fd : method.responder.fields) {
          markdown.append("| ").append(fd.name).append(" | ").append(fd.type.javaType()).append(" | ").append(String.join(" ", fd.documentation.split(Pattern.quote("\n"))).trim()).append(" |\n");
        }
      } else {
        markdown.append("This method simply returns void.\n");
      }
    }

    return markdown.toString();
  }
}
