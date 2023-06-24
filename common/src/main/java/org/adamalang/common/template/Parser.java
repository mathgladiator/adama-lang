/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
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

  /** the actual recursive descent method (super generic) */
  private T pull(Function<Fragment, Boolean> stopAt) {
    TConcat root = new TConcat();
    while(at < fragments.size()) {
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

  /** parse the given string and return a template tree node */
  public static T parse(String t) {
    return new Parser(Fragment.parse(t)).pull((x) -> false);
  }
}
