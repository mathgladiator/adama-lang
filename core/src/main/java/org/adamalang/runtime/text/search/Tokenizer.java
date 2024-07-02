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
package org.adamalang.runtime.text.search;

import java.util.PrimitiveIterator;
import java.util.TreeSet;

/** a very simple tokenizer */
public class Tokenizer {
  public static TreeSet<String> of(String sentence) {
    TreeSet<String> result = new TreeSet<>();
    StringBuilder word = new StringBuilder();
    PrimitiveIterator.OfInt it = sentence.codePoints().iterator();
    while (it.hasNext()) {
      int cp = it.nextInt();
      if (Character.isAlphabetic(cp)) {
        word.append(Character.toString(Character.toLowerCase(cp)));
      } else {
        if (word.length() > 0 && Character.isWhitespace(cp)) {
          result.add(word.toString());
          word.setLength(0);
        }
      }
    }
    if (word.length() > 0) {
      result.add(word.toString());
    }
    return result;
  }
}
