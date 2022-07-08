/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.runtime.text;

import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.natives.NtDynamic;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/** the core Text class for representing large text which can undergo OT transformations */
public class Text {
  public final boolean upgraded;
  public final HashMap<String, String> fragments;
  public final HashMap<Integer, String> order;
  public final HashMap<Integer, String> changes;
  public final HashMap<Integer, String> uncommitedChanges;
  public final int seq;

  /** fresh Text */
  public Text() {
    this.fragments = new HashMap<>();
    this.order = new HashMap<>();
    this.changes = new HashMap<>();
    this.uncommitedChanges = new HashMap<>();
    this.seq = 0;
    this.upgraded = false;
  }

  /** shallow copy as all values in maps are immutable */
  public Text(Text other) {
    this.fragments = new HashMap<>(other.fragments);
    this.order = new HashMap<>(other.order);
    this.changes = new HashMap<>(other.changes);
    this.uncommitedChanges = new HashMap<>(other.uncommitedChanges);
    this.seq = other.seq;
    this.upgraded = other.upgraded;
  }

  /** read from JSON */
  public Text(JsonStreamReader reader) {
    this.fragments = new HashMap<>();
    this.order = new HashMap<>();
    this.changes = new HashMap<>();
    this.uncommitedChanges = new HashMap<>();
    int _seq = 0;
    boolean _upgraded = false;
    if (reader.startObject()) {
      while (reader.notEndOfObject()) {
        switch (reader.fieldName()) {
          case "fragments":
            if (reader.startObject()) {
              while (reader.notEndOfObject()) {
                String key = reader.fieldName();
                fragments.put(key, reader.readString());
              }
            } else {
              reader.skipValue();
            }
            break;
          case "order":
            if (reader.startObject()) {
              while (reader.notEndOfObject()) {
                int index = Integer.parseInt(reader.fieldName());
                order.put(index, reader.readString());
              }
            } else {
              reader.skipValue();
            }
            break;
          case "changes":
            if (reader.startObject()) {
              while (reader.notEndOfObject()) {
                int __seq = Integer.parseInt(reader.fieldName());
                changes.put(__seq, reader.skipValueIntoJson());
              }
            } else {
              reader.skipValue();
            }
            break;
          case "seq":
            _seq = reader.readInteger();
            break;
        }
      }
    } else {
      set(reader.readString());
      _upgraded = true;
    }
    this.seq = _seq;
    this.upgraded = _upgraded;
  }

  public void set(String str) {
    HashMap<String, String> inverse = new HashMap<>();
    for (Map.Entry<String, String> prior : fragments.entrySet()) {
      inverse.put(prior.getValue(), prior.getKey());
    }
    fragments.clear();
    order.clear();
    int at = 0;
    for (String ln : str.split(Pattern.quote("\n"), -1)) {
      String key = inverse.get(ln);
      if (key == null) {
        key = keyFor(ln, inverse);
        inverse.put(ln, key);
      }
      fragments.put(key, ln);
      order.put(at, key);
      at++;
    }
    changes.clear();
    uncommitedChanges.clear();
  }

  public static String keyFor(String ln, HashMap<String, String> inverse) {
    while (true) {
      String candidate = Integer.toString(Math.abs(ln.hashCode()), 16) + "X" + Integer.toString(Math.abs(ln.hashCode()), 16) + "Y" + (long) (System.currentTimeMillis() * Math.random());
      for (int k = 2; k < candidate.length(); k++) {
        String test = candidate.substring(0, k);
        if (!inverse.containsKey(test)) {
          return test;
        }
      }
    }
  }

  /** write the text entirely to the given writer */
  public void write(JsonStreamWriter writer) {
    writer.beginObject();
    writer.writeObjectFieldIntro("fragments");
    writer.beginObject();
    for (Map.Entry<String, String> fragmentEntry : fragments.entrySet()) {
      writer.writeObjectFieldIntro(fragmentEntry.getKey());
      writer.writeString(fragmentEntry.getValue());
    }
    writer.endObject();
    writer.writeObjectFieldIntro("order");
    writer.beginObject();
    for (Map.Entry<Integer, String> orderEntry : order.entrySet()) {
      writer.writeObjectFieldIntro(orderEntry.getKey());
      writer.writeString(orderEntry.getValue());
    }
    writer.endObject();
    writer.writeObjectFieldIntro("changes");
    writer.beginObject();
    for (Map.Entry<Integer, String> changeEntry : changes.entrySet()) {
      writer.writeObjectFieldIntro(changeEntry.getKey());
      writer.injectJson(changeEntry.getValue());
    }
    writer.endObject();
    writer.writeObjectFieldIntro("seq");
    writer.writeInteger(seq);
    writer.endObject();
  }

  public void change(int seq, NtDynamic changes) {
    uncommitedChanges.put(seq, changes.json);
  }

  public String get() {
    return null;
  }
}
