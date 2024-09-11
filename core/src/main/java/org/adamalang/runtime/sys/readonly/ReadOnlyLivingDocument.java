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
package org.adamalang.runtime.sys.readonly;

import org.adamalang.runtime.contracts.Perspective;
import org.adamalang.runtime.data.Key;
import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.json.PrivateView;
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.runtime.sys.LivingDocument;
import org.adamalang.runtime.sys.StreamHandle;

/** a version of the document that is read only */
public class ReadOnlyLivingDocument {
  public final Key key;
  private final ReadOnlyReplicaThreadBase base;
  private final LivingDocument document;
  private boolean alreadyStarted;
  private boolean dead;
  private Runnable cancel;
  private long lastActivityMS;

  public ReadOnlyLivingDocument(ReadOnlyReplicaThreadBase base, Key key, LivingDocument document) {
    this.base = base;
    this.key = key;
    this.document = document;
    this.alreadyStarted = false;
    this.dead = false;
    ping();
  }

  private void ping() {
    this.lastActivityMS = base.time.nowMilliseconds();
  }

  public StreamHandle join(NtPrincipal who, String viewerState, Perspective perspective) {
    PrivateView view = document.__createView(who, perspective);
    if (viewerState != null) {
      view.ingest(new JsonStreamReader(viewerState));
    }
    if (alreadyStarted) {
      document.__forceBroadcastForViewer(who);
    }
    StreamHandle handle = new StreamHandle(view);
    ping();
    return handle;
  }

  public void setCancel(Runnable cancel) {
    ping();
    if (dead) {
      this.cancel.run();
    } else {
      this.cancel = cancel;
    }
  }

  public void start(String snapshot) {
    if (alreadyStarted) {
      // TODO: figure out deployment stuff
    }
    document.__insert(new JsonStreamReader(snapshot));
    document.__forceBroadcastToKeepReadonlyObserverUpToDate();
    alreadyStarted = true;
    ping();
  }

  public void change(String change) {
    document.__patch(new JsonStreamReader(change));
    document.__forceBroadcastToKeepReadonlyObserverUpToDate();
    ping();
  }

  public void forceUpdate(NtPrincipal who) {
    document.__forceBroadcastForViewer(who);
    ping();
  }

  public void kill() {
    dead = true;
    document.__nukeViews();
    if (cancel != null) {
      cancel.run();
    }
  }

  public int getCodeCost() {
    return document.__getCodeCost();
  }

  public long getCpuMilliseconds() {
    return document.__getCpuMilliseconds();
  }

  public void zeroOutCodeCost() {
    document.__zeroOutCodeCost();
  }

  public int getConnectionsCount() {
    return document.__getConnectionsCount();
  }

  public long getMemoryBytes() {
    return document.__memory();
  }

  public boolean testInactive() {
    long timeSinceLastActivity = base.time.nowMilliseconds() - lastActivityMS;
    return timeSinceLastActivity > base.getMillisecondsInactivityBeforeCleanup() && document.__canRemoveFromMemory();
  }
}
