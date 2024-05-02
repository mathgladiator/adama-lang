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
package org.adamalang.runtime.natives;

import org.adamalang.runtime.json.JsonStreamWriter;

import java.util.List;
import java.util.Map;

/** provides an optimized way for working with Json trees */
public class NtJson {
  private Object tree;

  public NtJson() {
    tree = null;
  }

  public NtJson(Object tree) {
    this.tree = tree;
  }

  public NtDynamic to_dynamic() {
    JsonStreamWriter writer = new JsonStreamWriter();
    writer.writeTree(tree);
    return new NtDynamic(writer.toString());
  }

  public NtJson deref(String field) {
    if (tree instanceof Map<?,?>) {
      return new NtJson(((Map<?, ?>) tree).get(field));
    } else if (tree instanceof List<?>) {
      List<?> l = (List<?>) tree;
      try {
        int idx = Integer.parseInt(field);
        if (0 <= idx && idx < l.size()) {
          return new NtJson(l.get(idx));
        }
      } catch (NumberFormatException nfe) {
      }
    }
    return NULL_VALUE;
  }

  public NtJson deref(int idx) {
    if (tree instanceof Map<?,?>) {
      return new NtJson(((Map<?, ?>) tree).get("" + idx));
    } else if (tree instanceof List<?>) {
      List<?> l = (List<?>) tree;
      if (0 <= idx && idx < l.size()) {
        return new NtJson(l.get(idx));
      }
    }
    return NULL_VALUE;
  }

  public NtMaybe<String> to_s() {
    if (tree instanceof String) {
      return new NtMaybe<>((String) tree);
    }
    if (tree != null && (!(tree instanceof Map<?,?> || tree instanceof List<?>))) {
      return new NtMaybe<>(tree.toString());
    }
    return new NtMaybe<>();
  }

  public NtMaybe<Integer> to_i() {
    if (tree instanceof Integer) {
      return new NtMaybe<>((int) tree);
    }
    if (tree instanceof Long) {
      return new NtMaybe<>((int) ((long) tree));
    }
    if (tree instanceof Double) {
      return new NtMaybe<>((int) ((double) tree));
    }
    return new NtMaybe<>();
  }

  public NtMaybe<Double> to_d() {
    if (tree instanceof Double) {
      return new NtMaybe<>((double) tree);
    }
    if (tree instanceof Integer) {
      return new NtMaybe<>((double) ((int) tree));
    }
    if (tree instanceof Long) {
      return new NtMaybe<>((double) ((long) tree));
    }
    return new NtMaybe<>();
  }

  public NtMaybe<Long> to_l() {
    if (tree instanceof Long) {
      return new NtMaybe<>((long) tree);
    }
    if (tree instanceof Integer) {
      return new NtMaybe<>((long)((int) tree));
    }
    if (tree instanceof Double) {
      return new NtMaybe<>((long) ((double) tree));
    }
    return new NtMaybe<>();
  }


  public NtMaybe<Boolean> to_b() {
    if (tree instanceof Boolean) {
      return new NtMaybe<>((boolean) tree);
    }
    if (tree instanceof Integer) {
      return new NtMaybe<>(((int) tree) != 0);
    }
    if (tree instanceof Long) {
      return new NtMaybe<>(((long) tree) != 0);
    }
    return new NtMaybe<>();
  }

  private static final NtJson NULL_VALUE = new NtJson(null);
}
