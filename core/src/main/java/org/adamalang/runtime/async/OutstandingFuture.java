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

import org.adamalang.runtime.natives.NtClient;

/** This represents a future which has been vended to the runtime.
 *
 * A future which has been vended, and it is assigned a unique id. Given how the
 * async element works within Adama, it is vital that order of futures vended be
 * stable. */
public class OutstandingFuture {
  public final String channel;
  private boolean claimed;
  public final int id;
  public String json;
  private boolean taken;
  public final NtClient who;

  /** @param id  the unique id of the future (for client's reference)
   * @param channel the channel for the future to wait on
   * @param who     the client we are waiting on */
  public OutstandingFuture(final int id, final String channel, final NtClient who) {
    this.id = id;
    this.channel = channel;
    this.who = who;
    claimed = true; // creation is an act of claiming
    taken = false;
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
  public boolean test(final String testChannel, final NtClient testClientId) {
    if (channel.equals(testChannel) && who.equals(testClientId) && !claimed) {
      claimed = true;
      return true;
    }
    return false;
  }
}
