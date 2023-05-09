/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.edhtml.phases.generate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

public class Annotations implements Iterable<Map.Entry<String, String>> {
  private final HashSet<String> tags;
  private final HashMap<String, String> pairs;

  public Annotations() {
    this.tags = new HashSet<>();
    this.pairs = new HashMap<>();
  }

  public boolean has(String annotation) {
    return tags.contains(annotation.toLowerCase()) || pairs.containsKey(annotation.toLowerCase());
  }

  public boolean is(String annotation, String value) {
    return value.equals(pairs.get(annotation.toLowerCase()));
  }

  public Annotations of(ArrayNode an) {
    if (an == null) {
      return this;
    }
    Annotations next = new Annotations();
    next.tags.addAll(this.tags);
    next.pairs.putAll(this.pairs);
    if (an != null) {
      for (int k = 0; k < an.size(); k++) {
        JsonNode element = an.get(k);
        if (element.isObject()) {
          Iterator<Map.Entry<String, JsonNode>> it = ((ObjectNode) element).fields();
          while (it.hasNext()) {
            Map.Entry<String, JsonNode> val = it.next();
            next.pairs.put(val.getKey().toLowerCase(), val.getValue().textValue());
          }
        } else {
          next.tags.add(element.textValue().toLowerCase());
        }
      }
    }
    return next;
  }

  public static Annotations union(Annotations a, Annotations b) {
    Annotations result = new Annotations();
    result.tags.addAll(b.tags);
    result.tags.addAll(a.tags);
    result.pairs.putAll(b.pairs);
    result.pairs.putAll(a.pairs);
    return result;
  }

  @Override
  public Iterator<Map.Entry<String, String>> iterator() {
    return pairs.entrySet().iterator();
  }
}
