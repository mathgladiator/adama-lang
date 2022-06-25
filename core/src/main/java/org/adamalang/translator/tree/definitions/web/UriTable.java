/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.translator.tree.definitions.web;

import java.util.HashMap;

/** a table which provides a recursive mapping of URIs to actions */
public class UriTable {
  public class UriLevel {
    public final HashMap<String, UriLevel> fixed;
    public final HashMap<String, UriLevel> bools;
    public final HashMap<String, UriLevel> ints;
    public final HashMap<String, UriLevel> longs;
    public final HashMap<String, UriLevel> doubles;
    public final HashMap<String, UriLevel> strings;
    public UriAction action;

    public UriLevel() {
      this.fixed = new HashMap<>();
      this.bools = new HashMap<>();
      this.ints = new HashMap<>();
      this.doubles = new HashMap<>();
      this.longs = new HashMap<>();
      this.strings = new HashMap<>();
      this.action = null;
    }

    public UriLevel next(String id, HashMap<String, UriLevel> map) {
      UriLevel next = map.get(id);
      if (next == null) {
        next = new UriLevel();
        map.put(id, next);
      }
      return next;
    }
  }

  private final UriLevel root;
  private int count;

  public UriTable() {
    this.root = new UriLevel();
    this.count = 0;
  }

  public int size() {
    return count;
  }

  public boolean map(Uri uri, UriAction action) {
    UriLevel level = uri.dive(root);
    if (level.action == null) {
      count++;
      level.action = action;
      return true;
    } else {
      return false;
    }
  }
}
