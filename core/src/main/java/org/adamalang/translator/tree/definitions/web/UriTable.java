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
package org.adamalang.translator.tree.definitions.web;

import org.adamalang.common.AlphaHex;
import org.adamalang.common.Hashing;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

/** a table which provides a recursive mapping of URIs to actions */
public class UriTable {
  public class UriLevel {
    public final TreeMap<String, UriLevel> fixed;
    public final TreeMap<String, UriLevel> bools;
    public final TreeMap<String, UriLevel> ints;
    public final TreeMap<String, UriLevel> longs;
    public final TreeMap<String, UriLevel> doubles;
    public final TreeMap<String, UriLevel> strings;
    public boolean tail;
    public UriAction action;
    public int count;
    public String name;

    public UriLevel() {
      this.fixed = new TreeMap<>();
      this.bools = new TreeMap<>();
      this.ints = new TreeMap<>();
      this.doubles = new TreeMap<>();
      this.longs = new TreeMap<>();
      this.strings = new TreeMap<>();
      this.action = null;
      this.tail = false;
      this.count = 0;
    }

    public UriLevel next(String id, TreeMap<String, UriLevel> map) {
      UriLevel next = map.get(id);
      if (next == null) {
        next = new UriLevel();
        map.put(id, next);
        count++;
      }
      return next;
    }

    public UriLevel tail() {
      this.tail = true;
      count++;
      return this;
    }

    public boolean check() {
      if (action != null || count > 0) {
        return true;
      }
      return false;
    }

    private void walkAndAssign(String prefix, TreeMap<String, UriLevel> children, TreeSet<String> taken, TreeMap<String, UriAction> actions) {
      for (Map.Entry<String, UriLevel> entry : children.entrySet()) {
        entry.getValue().assignName(prefix + entry.getKey() + "/", taken, actions);
      }
    }

    private void pickStableName(String stableCandidate, TreeSet<String> taken) {
      for (int k = 1; k < stableCandidate.length(); k++) {
        String candidate = stableCandidate.substring(0, k);
        if (!taken.contains(candidate)) {
          this.name = candidate;
          taken.add(candidate);
          return;
        }
      }
      pickStableName(AlphaHex.encode(Hashing.sha384().digest((stableCandidate + ":" + stableCandidate).getBytes(StandardCharsets.UTF_8))), taken);
    }

    public void assignName(String prefix, TreeSet<String> taken, TreeMap<String, UriAction> actions) {
      walkAndAssign(prefix + "fixed:", fixed, taken, actions);
      walkAndAssign(prefix + "bools:", bools, taken, actions);
      walkAndAssign(prefix + "ints:", ints, taken, actions);
      walkAndAssign(prefix + "longs:", longs, taken, actions);
      walkAndAssign(prefix + "doubles:", doubles, taken, actions);
      walkAndAssign(prefix + "strings:", strings, taken, actions);
      if (action != null) {
        String testName = prefix + (tail ? "*TAIL" : "");
        pickStableName(AlphaHex.encode(Hashing.sha384().digest(testName.getBytes(StandardCharsets.UTF_8))), taken);
        actions.put(this.name, action);
      }
    }
  }

  public final UriLevel root;
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

  public TreeMap<String, UriAction> ready(String prefix) {
    TreeSet<String> taken = new TreeSet<>();
    TreeMap<String, UriAction> actions = new TreeMap<>();
    root.assignName(prefix, taken, actions);
    return actions;
  }
}
