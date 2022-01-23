/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.runtime.async;

import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.natives.NtClient;
import org.adamalang.runtime.reactives.RxInt32;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * this class has the job of trakcing futures which get created and assigning them persistent ids.
 * This is the class which buffers asks from the code such that we can turn around and ask the
 * people
 */
public class OutstandingFutureTracker {
  public final ArrayList<OutstandingFuture> created;
  private final RxInt32 source;
  private int maxId;

  public OutstandingFutureTracker(final RxInt32 source) {
    this.source = source;
    created = new ArrayList<>();
    maxId = source.get();
  }

  /** the code completed, so let's commit our values and clean up! */
  public void commit() {
    if (source.get() != maxId) {
      source.set(maxId);
    }
    created.clear();
  }

  /**
   * dump the viewer's data into the provide node; this is how people learn that they must make a
   * decision
   */
  public void dump(final JsonStreamWriter writer, final NtClient who) {
    writer.writeObjectFieldIntro("outstanding");
    writer.beginArray();
    final var clientsBlocking = new HashSet<NtClient>();
    for (final OutstandingFuture exist : created) {
      if (exist.outstanding()) {
        clientsBlocking.add(exist.who);
        if (exist.who.equals(who)) {
          writer.injectJson(exist.json);
        }
      }
    }
    writer.endArray();
    writer.writeObjectFieldIntro("blockers");
    writer.beginArray();
    for (final NtClient blocker : clientsBlocking) {
      writer.writeNtClient(blocker);
    }
    writer.endArray();
  }

  /**
   * create a future for the given channel and client should the client not already know about one
   */
  public OutstandingFuture make(final String channel, final NtClient client) {
    var newId = source.get() + 1;
    for (final OutstandingFuture exist : created) {
      if (exist.test(channel, client)) {
        return exist;
      }
      if (exist.id >= newId) {
        newId = exist.id + 1;
      }
    }
    if (newId > maxId) {
      maxId = newId;
    }
    final var future = new OutstandingFuture(newId, channel, client);
    created.add(future);
    return future;
  }

  /** reset all the futures, this happens when the code gets reset */
  public void restore() {
    // in hindsight, we may not need this complexity... we MAY just need an index
    // for the client viewers
    for (final OutstandingFuture exist : created) {
      exist.reset();
    }
  }
}
