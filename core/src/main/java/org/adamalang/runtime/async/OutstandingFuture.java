/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.async;

import org.adamalang.runtime.natives.NtClient;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/** This represents a future which has been vended to the runtime.
 *
 * A future which has been vended, and it is assigned a unique id. Given how the
 * async element works within Adama, it is vital that order of futures vended be
 * stable. */
public class OutstandingFuture {
  public final String channel;
  private boolean claimed;
  private boolean distinct;
  public final int id;
  private int max;
  private int min;
  private ArrayNode options;
  private boolean taken;
  public final NtClient who;

  /** @param id   the unique id of the future (for client's reference)
   * @param channel  the channel for the future to wait on
   * @param who      the client we are waiting on
   * @param options  what are the possible options for the future (if any)
   * @param min      how many of the options must be used
   * @param max      how many of the options must be used
   * @param distinct should all the options be unique (i.e. choose 3 cards) */
  public OutstandingFuture(final int id, final String channel, final NtClient who, final ArrayNode options, final int min, final int max, final boolean distinct) {
    this.id = id;
    this.channel = channel;
    this.who = who;
    claimed = true; // creation is an act of claiming
    taken = false;
    this.options = options;
    this.min = min;
    this.max = max;
    this.distinct = distinct;
  }

  /** persist the future into the given node */
  public void dump(final ObjectNode node) {
    node.put("id", id);
    node.put("channel", channel);
    if (options != null) {
      node.set("options", options);
      node.put("min", min);
      node.put("max", max);
      node.put("distinct", distinct);
    }
  }

  /** has this future been claimed and not taken */
  public boolean outstanding() {
    return claimed && !taken;
  }

  /** release the claim and free it up */
  public void reset() {
    claimed = false;
    taken = false;
  }

  /** take the future */
  public void take() {
    taken = true;
  }

  /** does this future match the given channel and person; that is, can this
   * future pair up */
  public boolean test(final String testChannel, final NtClient testClientId, final ArrayNode options, final int min, final int max, final boolean distinct) {
    if (channel.equals(testChannel) && who.equals(testClientId) && !claimed) {
      claimed = true;
      this.options = options;
      this.min = min;
      this.max = max;
      this.distinct = distinct;
      return true;
    }
    return false;
  }
}
