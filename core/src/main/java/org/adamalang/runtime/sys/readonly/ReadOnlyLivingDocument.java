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
import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.json.PrivateView;
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.runtime.sys.LivingDocument;
import org.adamalang.runtime.sys.StreamHandle;

/** a version of the document that is read only */
public class ReadOnlyLivingDocument {
  private final LivingDocument document;
  private boolean alreadyStarted;
  private boolean dead;
  private Runnable cancel;

  public ReadOnlyLivingDocument(LivingDocument document) {
    this.document = document;
    this.alreadyStarted = false;
    this.dead = false;
  }

  public StreamHandle join(NtPrincipal who, Perspective perspective) {
    PrivateView view = document.__createPrivateView(who, perspective);
    StreamHandle handle = new StreamHandle(view);
    return handle;
  }

  public void setCancel(Runnable cancel) {
    if (dead) {
      this.cancel.run();
    } else {
      this.cancel = cancel;
    }
  }

  public void start(String snapshot) {
    if (alreadyStarted) {
      // TODO: deployment of factory is needed
    }
    document.__insert(new JsonStreamReader(snapshot));
    document.__forceBroadcastToKeepReadonlyObserverUpToDate();
  }

  public void change(String change) {
    document.__patch(new JsonStreamReader(change));
    document.__forceBroadcastToKeepReadonlyObserverUpToDate();
  }

  public void kill() {
    dead = true;
    document.__nukeViews();
    if (cancel != null) {
      cancel.run();
    }
  }
}
