/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.common.web;

import java.util.ArrayList;
import java.util.function.Function;
import java.util.regex.Pattern;

/** a simple uri matcher */
public class UriMatcher {
  private final ArrayList<Function<String, Boolean>> matchers;
  private final boolean lastHasStar;

  public UriMatcher(ArrayList<Function<String, Boolean>> matchers, boolean lastHasStar) {
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
