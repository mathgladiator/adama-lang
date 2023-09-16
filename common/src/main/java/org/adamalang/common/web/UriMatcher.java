/*
* Adama Platform and Language
* Copyright (C) 2021 - 2023 by Adama Platform Initiative, LLC
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
