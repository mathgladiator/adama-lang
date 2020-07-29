/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.json;

import java.util.function.Consumer;
import org.adamalang.runtime.natives.NtClient;

public abstract class PrivateView {
  public boolean alive;
  private final Consumer<String> updates;
  private final NtClient who;

  public PrivateView(final NtClient who, final Consumer<String> updates) {
    alive = true;
    this.who = who;
    this.updates = updates;
  }

  public void deliver(final String delivery) {
    updates.accept(delivery);
  }

  public synchronized boolean isAlive() {
    return alive;
  }

  public synchronized void kill() {
    alive = false;
  }

  public abstract void update(JsonStreamWriter writer);
}
