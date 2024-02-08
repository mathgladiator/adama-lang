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
package org.adamalang.runtime.json;

import org.adamalang.runtime.contracts.Perspective;
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.runtime.sys.StreamHandle;

/** a private view of the document where private lives; code is generated to use this */
public abstract class PrivateView {
  private static final String DEFAULT_FUTURES = "\"outstanding\":[],\"blockers\":[]";
  public final Perspective perspective;
  public final NtPrincipal who;
  private final int viewId;
  private boolean alive;
  private StreamHandle handle;
  private String lastWrittenFutures;
  private Runnable refresh;

  /** construct the view based on the person (who) and the connection (perspective) */
  public PrivateView(final int viewId, final NtPrincipal who, final Perspective perspective) {
    this.viewId = viewId;
    this.alive = true;
    this.who = who;
    this.perspective = perspective;
    this.lastWrittenFutures = DEFAULT_FUTURES;
    this.refresh = null;
  }

  public void link(StreamHandle handle) {
    this.handle = handle;
  }

  /** this is an optimized way to update the viewer without causing an invalidate */
  public void setRefresh(Runnable refresh) {
    this.refresh = refresh;
  }

  public void triggerRefresh() {
    if (refresh != null) {
      refresh.run();
    }
  }

  /**
   * a new private view was created on a different document which is usurping the existing document.
   * Since private views leak outside the document, this creates a proxy link for the client to kill
   * both views.
   */
  public void usurp(PrivateView usurper) {
    handle.set(usurper);
    usurper.link(handle);
    synchronized (this) {
      alive = false;
    }
  }

  public int getViewId() {
    return viewId;
  }

  public void ingestViewUpdate(JsonStreamReader reader) {
    ingest(reader);
  }

  /** codegen: seed the state held by the view */
  public abstract void ingest(JsonStreamReader reader);

  /** codegen: dump the data held by the view */
  public abstract void dumpViewer(JsonStreamWriter writer);

  /** send the user a delivery update */
  public void deliver(final String delivery) {
    perspective.data(delivery);
  }

  /** is the view still alive and interesting to the user */
  public synchronized boolean isAlive() {
    return alive;
  }

  /** get the memory of the view */
  public abstract long memory();

  /** dedupe excessive outstanding and blockers sharing */
  public boolean futures(String futures) {
    String futuresToTest = futures;
    if (futuresToTest.equals(DEFAULT_FUTURES)) { // save some memory
      futuresToTest = DEFAULT_FUTURES;
    }
    if (futuresToTest == lastWrittenFutures || lastWrittenFutures.equals(futures)) {
      return false;
    } else {
      lastWrittenFutures = futures;
      return true;
    }
  }

  /** the client is no longer interested */
  public synchronized void kill() {
    alive = false;
  }

  /** codegen: the data inside the view has been updated, stream it out to the writer */
  public abstract void update(JsonStreamWriter writer);
}
