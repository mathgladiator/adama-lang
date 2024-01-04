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
package org.adamalang.common;

import java.util.ArrayList;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** common logic for dealing with unix/web paths with / */
public class Pathing {
  private static final String SLASH = Pattern.quote("/");
  private static final String WINDOWS_SLASH = Pattern.quote("\\");
  private static final String UNIX_SLASH_REPLACE = Matcher.quoteReplacement("/");

  public static String normalize(String x) {
    return x.replaceAll(WINDOWS_SLASH, UNIX_SLASH_REPLACE);
  }

  public static String maxSharedSuffix(String a, String b) {
    String[] x = a.split(SLASH);
    String[] y = b.split(SLASH);
    int m = Math.min(x.length, y.length);
    Stack<String> reversed = new Stack<>();
    for (int k = 0; k < m; k++) {
      String u = x[x.length - 1 - k];
      String v = y[y.length - 1 - k];
      if (u.equals(v)) {
        reversed.push(u);
      } else {
        break;
      }
    }
    ArrayList<String> forward = new ArrayList<>();
    while (!reversed.isEmpty()) {
      forward.add(reversed.pop());
    }
    return String.join("/", forward);
  }

  public static String maxSharedPrefix(String a, String b) {
    String[] x = a.split(SLASH);
    String[] y = b.split(SLASH);
    ArrayList<String> common = new ArrayList<>();
    int m = Math.min(x.length, y.length);
    for (int k = 0; k < m; k++) {
      if (x[k].equals(y[k])) {
        common.add(x[k]);
      } else {
        break;
      }
    }
    return String.join("/", common);
  }

  /** find the common root between two files */
  public static String removeCommonRootFromB(String a, String b) {
    return b.substring(maxSharedPrefix(a, b).length());
  }

  public static String removeLast(String x) {
    int kLastSlash = x.lastIndexOf('/');
    if (kLastSlash > 0) {
      return x.substring(0, kLastSlash);
    }
    return x;
  }
}
