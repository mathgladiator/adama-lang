package org.adamalang.caravan.index;

import org.adamalang.caravan.entries.DelKey;
import org.adamalang.caravan.entries.MapKey;
import org.adamalang.runtime.data.Key;

import java.util.HashMap;
import java.util.Map;

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

  public MapKey inventAndApply(Key key) {
    if (forward.containsKey(key)) {
      return null;
    }
    while (true) {
      int id = ++idgen;
      if (!reverse.containsKey(id)) {
        MapKey result = new MapKey(key, id);
        apply(result);
        return result;
      }
    }
  }
}
