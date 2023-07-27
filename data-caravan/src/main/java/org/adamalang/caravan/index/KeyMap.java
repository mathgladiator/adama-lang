/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.caravan.index;

import io.netty.buffer.ByteBuf;
import org.adamalang.caravan.entries.DelKey;
import org.adamalang.caravan.entries.MapKey;
import org.adamalang.runtime.data.Key;

import java.nio.charset.StandardCharsets;
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

  /** take a snapshot of the index */
  public void snapshot(ByteBuf buf) {
    for (Map.Entry<Key, Integer> entry : forward.entrySet()) {
      buf.writeBoolean(true);
      byte[] space = entry.getKey().space.getBytes(StandardCharsets.UTF_8);
      byte[] key = entry.getKey().key.getBytes(StandardCharsets.UTF_8);
      buf.writeIntLE(space.length);
      buf.writeBytes(space);
      buf.writeIntLE(key.length);
      buf.writeBytes(key);
      buf.writeIntLE(entry.getValue());
    }
    buf.writeBoolean(false);
  }

  /** load an index from a snapshot */
  public void load(ByteBuf buf) {
    forward.clear();
    reverse.clear();
    while (buf.readBoolean()) {
      int sizeSpace = buf.readIntLE();
      byte[] bytesSpace = new byte[sizeSpace];
      buf.readBytes(bytesSpace);
      int sizeKey = buf.readIntLE();
      byte[] bytesKey = new byte[sizeKey];
      buf.readBytes(bytesKey);
      int id = buf.readIntLE();
      Key key = new Key(new String(bytesSpace, StandardCharsets.UTF_8), new String(bytesKey, StandardCharsets.UTF_8));
      forward.put(key, id);
      reverse.put(id, key);
    }
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("[keymap;");
    for (Map.Entry<Key, Integer> entry : forward.entrySet()) {
      sb.append(entry.getKey().space + "/" + entry.getKey().key + "==" + entry.getValue() + ";");
    }
    sb.append("]");
    return sb.toString();
  }
}
