/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.async;

import java.util.ArrayList;
import java.util.HashSet;
import org.adamalang.runtime.natives.NtClient;
import org.adamalang.runtime.reactives.RxInt32;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/** this class has the job of trakcing futures which get created and assigning them persistent ids. This is the class which buffers asks from the code such that we can turn around and ask the people */
public class OutstandingFutureTracker {
  public final ArrayList<OutstandingFuture> created;
  private int maxId;
  private final RxInt32 source;

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

  /** dump the viewer's data into the provide node; this is how people learn that they must make a decision */
  public void dumpIntoView(final ObjectNode view, final NtClient who) {
    final var outstanding = view.putArray("outstanding");
    final var blockers = view.putArray("blockers");
    final var clientsBlocking = new HashSet<NtClient>();
    for (final OutstandingFuture exist : created) {
      if (exist.outstanding()) {
        if (!clientsBlocking.contains(exist.who)) {
          clientsBlocking.add(exist.who);
          exist.who.dump(blockers.addObject());
        }
        if (exist.who.equals(who)) {
          exist.dump(outstanding.addObject());
        }
      }
    }
  }

  /** create a future for the given channel and client should the client not
   * already know about one */
  public OutstandingFuture make(final String channel, final NtClient client, final ArrayNode options, final int min, final int max, final boolean distinct) {
    var newId = source.get() + 1;
    for (final OutstandingFuture exist : created) {
      // TODO: if this gets large, then this is bad. Should we index by... person
      if (exist.test(channel, client, options, min, max, distinct)) { return exist; }
      if (exist.id >= newId) {
        newId = exist.id + 1;
      }
    }
    if (newId > maxId) {
      maxId = newId;
    }
    final var future = new OutstandingFuture(newId, channel, client, options, min, max, distinct);
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
