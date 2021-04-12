/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.json;

import org.adamalang.runtime.contracts.Perspective;
import org.adamalang.runtime.natives.NtClient;

public abstract class PrivateView {
  public boolean alive;
  public final Perspective perspective;
  public final NtClient who;
  private PrivateView usurper;

  public PrivateView(final NtClient who, final Perspective perspective) {
    alive = true;
    this.who = who;
    this.perspective = perspective;
  }

  public void usurp(PrivateView usurper) {
    this.usurper = usurper;
  }

  public abstract void ingest(JsonStreamReader reader);

  public abstract void dumpViewer(JsonStreamWriter writer);

  public void deliver(final String delivery) {
    perspective.data(delivery);
  }

  public synchronized boolean isAlive() {
    return alive;
  }

  public synchronized void kill() {
    if (usurper != null) {
      usurper.kill();
    }
    alive = false;
  }

  public abstract void update(JsonStreamWriter writer);
}
