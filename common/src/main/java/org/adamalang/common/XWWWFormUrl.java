/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.common;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

/** convert a JSON object into a application/x-www-form-urlencoded string */
public class XWWWFormUrl {
  private static void pump(ObjectNode node, String prefix, ArrayList<String> output) {
    Iterator<Map.Entry<String, JsonNode>> it = node.fields();
    while (it.hasNext()) {
      Map.Entry<String, JsonNode> field = it.next();
      if (field.getValue().isObject()) {
        pump((ObjectNode) field.getValue(), prefix + field.getKey() + ".", output);
      } else if (field.getValue().isArray()) {
        // TODO: figure this out
      } else {
        if (field.getValue().isTextual()) {
          output.add(prefix + field.getKey() + "=" + URL.encode(field.getValue().textValue(), false));
        } else {
          output.add(prefix + field.getKey() + "=" + URL.encode(field.getValue().toString(), false));
        }
      }
    }
  }

  public static String encode(ObjectNode node) {
    ArrayList<String> output = new ArrayList<>();
    pump(node, "", output);
    return String.join("&", output);
  }
}
