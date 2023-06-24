/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
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
      this.text = new String[] { "[" };
      return;
    }
    this.type = type;
    this.text = text;
  }

  @Override
  public String toString() {
    return type + ":" + Arrays.toString(text);
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
          switch (cp) {
            case '[':
              state = State.FirstEscape;
              break;
            default:
              current.append(Character.toChars(cp));
              break;
          }
          break;
        }
        case FirstEscape: {
          state = State.Text;
          switch (cp) {
            case '[':
              cut.run();
              results.add(scan.get());
              break;
            default:
              current.append('[');
              current.append(Character.toChars(cp));
              break;
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

  private enum State {
    Text,
    FirstEscape
  }
}
