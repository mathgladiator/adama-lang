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
package org.adamalang.runtime.sys;

import org.adamalang.runtime.data.RemoteDocumentUpdate;
import org.adamalang.runtime.json.PrivateView;

import java.util.List;

/**
 * A living document is changing; this represents the side-effects of that change for both the data
 * store (i.e. durability) and the clients (i. the people). This change is bundled such that these
 * two concerns can be ordered such that clients never see artifacts of uncommitted changes.
 */
public class LivingDocumentChange {

  /** the data change */
  public final RemoteDocumentUpdate update;
  public final Object response;
  private final List<Broadcast> broadcasts;
  public final boolean needBroadcast;

  /** wrap both the update and broadcasts into a nice package */
  public LivingDocumentChange(RemoteDocumentUpdate update, List<Broadcast> broadcasts, Object response, boolean needBroadcast) {
    this.update = update;
    this.broadcasts = broadcasts;
    this.response = response;
    this.needBroadcast = needBroadcast;
  }

  /** complete the update. This is to be called once the change is made durable */
  public void complete() {
    if (broadcasts != null) {
      for (Broadcast bc : broadcasts) {
        bc.complete();
      }
    }
  }

  /** a closure to combine who to send too */
  public static class Broadcast {
    private final PrivateView view;
    private final String data;

    public Broadcast(PrivateView view, String data) {
      this.view = view;
      this.data = data;
    }

    public void complete() {
      view.deliver(data);
    }
  }
}
