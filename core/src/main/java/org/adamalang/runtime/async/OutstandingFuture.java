/*
* Adama Platform and Language
* Copyright (C) 2021 - 2024 by Adama Platform Engineering, LLC
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

import org.adamalang.runtime.natives.NtPrincipal;

/**
 * This represents a future which has been vended to the runtime.
 *
 * <p>A future which has been vended, and it is assigned a unique id. Given how the async element
 * works within Adama, it is vital that order of futures vended be stable.
 */
public class OutstandingFuture {
  public final String channel;
  public final int id;
  public final NtPrincipal who;
  public String json;
  private boolean claimed;
  private boolean taken;

  /**
   * @param id the unique id of the future (for client's reference)
   * @param channel the channel for the future to wait on
   * @param who the client we are waiting on
   */
  public OutstandingFuture(final int id, final String channel, final NtPrincipal who) {
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

  /** does this future match the given channel and person; that is, can this future pair up */
  public boolean test(final String testChannel, final NtPrincipal testClientId) {
    if (channel.equals(testChannel) && who.equals(testClientId) && !claimed) {
      claimed = true;
      return true;
    }
    return false;
  }
}
