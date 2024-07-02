/*
* Adama Platform and Language
* Copyright (C) 2021 - 2024 by Adama Platform Engineering, LLC
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
package org.adamalang.runtime.stdlib;

import org.adamalang.runtime.text.search.Tokenizer;
import org.adamalang.translator.reflect.Skip;

import java.util.Locale;
import java.util.TreeSet;

/** simple search related functions */
public class LibSearch {
  /** operator for searching operator =?

   * X =? Y is true IF
   *   * X is empty string
   *   * X is a substring of Y
   *   * X has words that are zpart of the haystack
   * */
  @Skip
  public static boolean test(String needleRaw, String haystackRaw) {
    String needle = needleRaw.trim().toLowerCase(Locale.ENGLISH);
    // condition 1: needle is empty string
    if ("".equals(needle)) {
      return true;
    }

    // condition 2: the entire substring was found within the haystack
    String haystack = haystackRaw.trim().toLowerCase(Locale.ENGLISH);
    if (haystack.contains(needle.trim())) {
      return true;
    }

    // condition 3: we tokenize and check each word is within haystack
    TreeSet<String> a = Tokenizer.of(needle);
    for (String x : a) {
      if (haystack.contains(x)) {
        return true;
      }
    }
    return false;
  }
}
