/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.common.web;

import java.util.ArrayList;
import java.util.function.Function;
import java.util.regex.Pattern;

/** a simple uri matcher */
public class UriMatcher {
  public final String name;
  private final ArrayList<Function<String, Boolean>> matchers;
  private final boolean lastHasStar;

  public UriMatcher(String name, ArrayList<Function<String, Boolean>> matchers, boolean lastHasStar) {
    this.name = name;
    this.matchers = matchers;
    this.lastHasStar = lastHasStar;
  }

  /** the uri to test if it matches */
  public boolean matches(String uri) {
    String[] parts = uri.substring(1).split(Pattern.quote("/"), -1);
    int at = 0;
    for (Function<String, Boolean> match : matchers) {
      if (at < parts.length) {
        if (!match.apply(parts[at])) {
          return false;
        }
      } else {
        return false;
      }
      at++;
    }
    if (at < parts.length) {
      return lastHasStar;
    } else {
      return true;
    }
  }
}
