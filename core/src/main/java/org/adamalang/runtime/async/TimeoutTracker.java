/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.runtime.async;

import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.reactives.RxInt64;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/** track the timeouts */
public class TimeoutTracker {
  public final RxInt64 time;
  public final HashMap<Integer, Timeout> timeouts;
  public final HashSet<Integer> created;

  public TimeoutTracker(RxInt64 time) {
    this.time = time;
    this.timeouts = new HashMap<>();
    this.created = new HashSet<>();
  }

  /** dump timeouts out under the living document __timeouts field */
  public void dump(JsonStreamWriter writer) {
    if (timeouts.size() > 0) {
      writer.writeObjectFieldIntro("__timeouts");
      writer.beginObject();
      for (Map.Entry<Integer, Timeout> timeout : timeouts.entrySet()) {
        writer.writeObjectFieldIntro(timeout.getKey());
        timeout.getValue().write(writer);
      }
      writer.endObject();
    }
  }

  /** restore the timeouts from a snapshot or patch */
  public void hydrate(JsonStreamReader reader) {
    if (reader.testLackOfNull()) {
      if (reader.startObject()) {
        while (reader.notEndOfObject()) {
          final var timeoutId = Integer.parseInt(reader.fieldName());
          if (reader.testLackOfNull()) {
            Timeout timeout = Timeout.readFrom(reader);
            if (timeout != null) {
              timeouts.put(timeoutId, timeout);
            }
          } else {
            timeouts.remove(timeoutId);
          }
        }
      }
    } else {
      timeouts.clear();
    }
  }

  public Timeout create(int id, double timeout) {
    Timeout to = timeouts.get(id);
    if (to != null) {
      return to;
    }
    to = new Timeout(time.get(), timeout);
    timeouts.put(id, to);
    created.add(id);
    return to;
  }

  public boolean needsInvalidationAndUpdateNext(RxInt64 next) {
    long expectedNext = next.get();
    boolean forceSetFirst = expectedNext <= time.get();
    for (Timeout to : timeouts.values()) {
      long computedNext = to.timestamp + (long) (to.timeoutSeconds * 1000L);
      if (computedNext < expectedNext || forceSetFirst) {
        next.set(computedNext);
        forceSetFirst = false;
      }
    }
    return timeouts.size() > 0;
  }

  public void revert() {
    for (Integer keyCreated : created) {
      timeouts.remove(keyCreated);
    }
  }

  public void commit(JsonStreamWriter forward, JsonStreamWriter reverse) {
    if (timeouts.size() > 0) {
      forward.writeObjectFieldIntro("__timeouts");
      forward.beginObject();
      reverse.writeObjectFieldIntro("__timeouts");
      reverse.beginObject();
      for (Integer keyCreated : created) {
        Timeout timeout = timeouts.get(keyCreated);
        forward.writeObjectFieldIntro(keyCreated);
        timeout.write(forward);
        reverse.writeObjectFieldIntro(keyCreated);
        reverse.writeNull();
      }
      forward.endObject();
      reverse.endObject();
      created.clear();
    }
  }

  public void nuke(JsonStreamWriter forward, JsonStreamWriter reverse) {
    if (timeouts.size() > 0) {
      forward.writeObjectFieldIntro("__timeouts");
      forward.beginObject();
      reverse.writeObjectFieldIntro("__timeouts");
      reverse.beginObject();
      for (Map.Entry<Integer, Timeout> timeout : timeouts.entrySet()) {
        forward.writeObjectFieldIntro(timeout.getKey());
        forward.writeNull();
        reverse.writeObjectFieldIntro(timeout.getKey());
        timeout.getValue().write(reverse);
      }
      forward.endObject();
      reverse.endObject();
    }
  }
}
