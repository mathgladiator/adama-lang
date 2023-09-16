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
package org.adamalang.runtime.async;

import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.natives.NtPrincipal;
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
  public final TimeoutTracker timeouts;
  private final RxInt32 source;
  private int maxId;

  public OutstandingFutureTracker(final RxInt32 source, TimeoutTracker timeouts) {
    this.source = source;
    this.timeouts = timeouts;
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
  public void dump(final JsonStreamWriter writer, final NtPrincipal who) {
    writer.writeObjectFieldIntro("outstanding");
    writer.beginArray();
    final var clientsBlocking = new HashSet<NtPrincipal>();
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
    for (final NtPrincipal blocker : clientsBlocking) {
      writer.writeNtPrincipal(blocker);
    }
    writer.endArray();
  }

  /**
   * create a future for the given channel and client should the client not already know about one
   */
  public OutstandingFuture make(final String channel, final NtPrincipal client) {
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
