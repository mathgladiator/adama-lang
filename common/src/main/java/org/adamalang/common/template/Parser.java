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
package org.adamalang.common.template;

import org.adamalang.common.template.fragment.Fragment;
import org.adamalang.common.template.fragment.FragmentType;
import org.adamalang.common.template.tree.*;

import java.util.ArrayList;
import java.util.function.Function;

/** parser for convert strings into template [T] trees. */
public class Parser {
  private final ArrayList<Fragment> fragments;
  private int at;

  private Parser(ArrayList<Fragment> fragments) {
    this.fragments = fragments;
    this.at = 0;
  }

  /** parse the given string and return a template tree node */
  public static T parse(String t) {
    return new Parser(Fragment.parse(t)).pull((x) -> false);
  }

  /** the actual recursive descent method (super generic) */
  private T pull(Function<Fragment, Boolean> stopAt) {
    TConcat root = new TConcat();
    while (at < fragments.size()) {
      Fragment curr = fragments.get(at);
      if (stopAt.apply(curr)) {
        at++;
        return root;
      }
      at++;
      if (curr.type == FragmentType.Text) {
        root.add(new TText(curr.text[0]));
      }
      if (curr.type == FragmentType.Expression) {
        boolean unescape = false;
        if (curr.text.length > 2) {
          unescape = "|".equals(curr.text[1]) && "unescape".equalsIgnoreCase(curr.text[2]);
        }
        root.add(new TVariable(curr.text[0], unescape));
      }
      if (curr.type == FragmentType.If) {
        // TODO: interpret beyond text[0]
        root.add(new TIf(curr.text[0], pull((x) -> x.type == FragmentType.End)));
      }
      if (curr.type == FragmentType.IfNot) {
        // TODO: interpret beyond text[0]
        root.add(new TIfNot(curr.text[0], pull((x) -> x.type == FragmentType.End)));
      }
    }
    return root;
  }
}
