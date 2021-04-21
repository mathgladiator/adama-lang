/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * which is in the root directory of the repository. This file is part of the 'Adama'
 * project which is a programming language and document store for board games.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2021 by Jeffrey M. Barber (http://jeffrey.io)
*/
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
