/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.caravan.index;

import org.adamalang.caravan.entries.DelKey;
import org.adamalang.caravan.entries.MapKey;
import org.adamalang.runtime.data.Key;

import java.util.*;

public class KeyMap {
  private final HashMap<Key, Integer> forward;
  private final HashMap<Integer, Key> reverse;
  private int idgen;

  public KeyMap() {
    this.forward = new HashMap<>();
    this.reverse = new HashMap<>();
    this.idgen = 0;
  }

  public void apply(MapKey map) {
    Key key = map.of();
    forward.put(key, map.id);
    reverse.put(map.id, key);
    if (map.id > idgen) {
      idgen = map.id;
    }
  }

  public void apply(DelKey del) {
    Integer result = forward.remove(del.of());
    if (result != null) {
      reverse.remove(result);
    }
  }

  public Integer get(Key key) {
    return forward.get(key);
  }

  public boolean exists(Key key) {
    return forward.containsKey(key);
  }

  public TreeMap<Key, Integer> copy() {
    return new TreeMap<>(forward);
  }

  public MapKey inventAndApply(Key key, int suggestion) {
    if (forward.containsKey(key)) {
      return null;
    }
    int id = suggestion;
    while (true) {
      if (!reverse.containsKey(id)) {
        MapKey result = new MapKey(key, id);
        apply(result);
        return result;
      }
      id = ++idgen;
    }
  }
}
