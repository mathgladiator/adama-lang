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
package org.adamalang.common.template.fragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.PrimitiveIterator;
import java.util.function.Supplier;
import java.util.stream.IntStream;

/** a simplified templating language inspired by mustache */
public class Fragment {
  public final FragmentType type;
  public final String[] text;

  public Fragment(FragmentType type, String... text) {
    if (text.length == 0) {
      this.type = FragmentType.Text;
      this.text = new String[]{"["};
      return;
    }
    this.type = type;
    this.text = text;
  }

  /** parse the template */
  public static ArrayList<Fragment> parse(String template) {
    ArrayList<Fragment> results = new ArrayList<>();
    IntStream codepoints = template.codePoints();
    PrimitiveIterator.OfInt it = codepoints.iterator();
    StringBuilder current = new StringBuilder();
    State state = State.Text;
    Runnable cut = () -> {
      String r = current.toString();
      current.setLength(0);
      if (r.length() > 0) {
        results.add(new Fragment(FragmentType.Text, r));
      }
    };
    Supplier<Fragment> scan = () -> {
      ArrayList<String> parts = new ArrayList<>();
      StringBuilder part = new StringBuilder();
      FragmentType ty = FragmentType.Expression;
      boolean first = true;
      while (it.hasNext()) {
        int ecp = it.next();
        switch (ecp) {
          case '#':
            if (first) {
              ty = FragmentType.If;
              break;
            }
          case '^':
            if (first) {
              ty = FragmentType.IfNot;
              break;
            }
          case '/':
            if (first) {
              ty = FragmentType.End;
              break;
            }
          case ']':
          case '(':
          case ')':
          case '|':
          case '>':
          case '<':
          case '+':
          case '*':
          case '-':
            String partial = part.toString().trim();
            if (partial.length() > 0) {
              parts.add(partial);
            }
            part.setLength(0);
            if (ecp == ']') {
              if (!(it.hasNext() && it.next() == ']')) {
                throw new RuntimeException("']' encountered without additional ']' during scan");
              }
              return new Fragment(ty, parts.toArray(new String[parts.size()]));
            } else {
              parts.add("" + Character.toString(ecp));
            }
            break;
          default:
            part.append(Character.toChars(ecp));
            break;
        }
        first = false;
      }
      throw new RuntimeException("scan() failed due to missing ']'");
    };
    while (it.hasNext()) {
      int cp = it.next();
      switch (state) {
        case Text: {
          if (cp == '[') {
            state = State.FirstEscape;
          } else {
            current.append(Character.toChars(cp));
          }
          break;
        }
        case FirstEscape: {
          state = State.Text;
          if (cp == '[') {
            cut.run();
            results.add(scan.get());
          } else {
            current.append('[');
            current.append(Character.toChars(cp));
          }
          break;
        }
      }
    }
    if (state == State.FirstEscape) {
      current.append('[');
    }
    cut.run();
    return results;
  }

  @Override
  public String toString() {
    return type + ":" + Arrays.toString(text);
  }

  private enum State {
    Text, FirstEscape
  }
}
