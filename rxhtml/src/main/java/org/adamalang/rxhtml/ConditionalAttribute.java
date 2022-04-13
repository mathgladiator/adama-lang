package org.adamalang.rxhtml;

import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConditionalAttribute {
  public static final Pattern INITIATE = Pattern.compile("[\\[]([a-zA-Z_]\\w*)[\\]]");

  public static TreeSet<String> guards(String value) {
    TreeSet<String> g = new TreeSet<>();
    if (value != null) {
      Matcher matcher = INITIATE.matcher(value);
      while (matcher.find()) {
        g.add(matcher.group(1));
      }
    }
    return g;
  }
}
