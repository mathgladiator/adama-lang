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

import org.adamalang.common.Callback;
import org.adamalang.runtime.data.DataObserver;
import org.adamalang.runtime.data.Key;
import org.adamalang.runtime.sys.CoreService;

/** a basic proxy implementation of ReplicationInitiator which will adapt CoreServce to ReplicationInitiator IF it is the machine requested or no machine was requested */
public class LocalReplicationProxy implements ReplicationInitiator {
  private final String localName;
  private final ReplicationInitiator proxy;
  private CoreService service = null;

  public LocalReplicationProxy(String me, ReplicationInitiator proxy) {
    this.localName = me;
    this.proxy = proxy;
  }

  public void initialize(CoreService service) {
    this.service = service;
  }

  @Override
  public void startDocumentReplication(Key key, DataObserver observer, Callback<Runnable> cancel) {
    String machineHint = observer.machine();
    if (service != null && (machineHint == null || localName.equals(machineHint))) {
      service.watch(key, observer);
      cancel.success(() -> {
        service.unwatch(key, observer);
      });
    } else {
      proxy.startDocumentReplication(key, observer, cancel);
    }
  }
}
