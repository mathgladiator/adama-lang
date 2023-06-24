/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.edhtml.phases.generate;

import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Element;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** the core factory rule */
public class Rule {
  private final String type;
  private final String destination;
  private final DestinationType destinationType;
  private final HashSet<String> annotationsPresent;
  private final HashSet<String> annotationsAbsent;
  private final HashMap<String, String> annotationsEqual;
  private final String production;

  public Rule(Element element) {
    DestinationType _destinationType = DestinationType.Append;
    String _destination = "children";
    String _type = "*";
    annotationsPresent = new HashSet<>();
    annotationsAbsent = new HashSet<>();
    annotationsEqual = new HashMap<>();
    for (Attribute attr : element.attributes()) {
      AttributePair ap = new AttributePair(attr.getKey());
      if (ap.value != null) {
        switch (ap.key) {
          case "annotation-present":
            annotationsPresent.add(ap.value);
            break;
          case "annotation-absent":
            annotationsAbsent.add(ap.value);
            break;
          case "annotation-is":
            annotationsEqual.put(ap.value, attr.getValue());
        }
      } else {
        switch (attr.getKey()) {
          case "append":
            _destinationType = DestinationType.Append;
            _destination = attr.getValue();
            break;
          case "set":
            _destinationType = DestinationType.Set;
            _destination = attr.getValue();
            break;
          case "type":
            _type = attr.getValue();
            break;
        }
      }
    }
    this.type = _type;
    this.destination = _destination;
    this.destinationType = _destinationType;
    this.production = element.html();
  }

  public boolean test(Field field) {
    if (!field.type.equals(this.type) && !"*".equals(this.type)) {
      return false;
    }
    for (String annotation : annotationsPresent) {
      if (!field.annotations.has(annotation)) {
        return false;
      }
    }
    for (String annotation : annotationsAbsent) {
      if (field.annotations.has(annotation)) {
        return false;
      }
    }
    for (Map.Entry<String, String> entry : annotationsEqual.entrySet()) {
      if (!field.annotations.is(entry.getKey(), entry.getValue())) {
        return false;
      }
    }
    return true;
  }

  public void manifest(Field field, HashMap<String, String> defines) {
    HashMap<String, String> mapping = field.map(defines);
    String next = production;
    for (Map.Entry<String, String> entry : mapping.entrySet()) {
      next = next.replaceAll(Pattern.quote("%%" + entry.getKey() + "%%"), Matcher.quoteReplacement(entry.getValue()));
    }
    if (destinationType == DestinationType.Set) {
      defines.put(destination, next);
    } else {
      String prior = defines.get(destination);
      if (prior == null) {
        prior = "";
      }
      defines.put(destination, prior + next);
    }
  }
}
