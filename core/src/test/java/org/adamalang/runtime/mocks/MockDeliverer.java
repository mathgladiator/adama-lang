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
package org.adamalang.runtime.mocks;

import org.adamalang.common.Callback;
import org.adamalang.runtime.data.Key;
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.runtime.remote.Deliverer;
import org.adamalang.runtime.remote.RemoteResult;
import org.adamalang.runtime.remote.RxCache;

import java.util.ArrayList;

public class MockDeliverer implements Deliverer  {
  private static class Delivery {
    public final NtPrincipal agent;
    public final Key key;
    public final int id;
    public final RemoteResult result;
    public final boolean firstParty;
    public final Callback<Integer> callback;

    public Delivery(NtPrincipal agent, Key key, int id, RemoteResult result, boolean firstParty, Callback<Integer> callback) {
      this.agent = agent;
      this.key = key;
      this.id = id;
      this.result = result;
      this.firstParty = firstParty;
      this.callback = callback;
    }
  }

  public final ArrayList<Delivery> deliveries;

  public MockDeliverer() {
    this.deliveries = new ArrayList<>();
  }

  public void deliverAllTo(RxCache cache) {
    while (deliveries.size() > 0) {
      Delivery d = deliveries.remove(0);
      cache.deliver(d.id, d.result);
    }
    deliveries.clear();
  }

  @Override
  public synchronized void deliver(NtPrincipal agent, Key key, int id, RemoteResult result, boolean firstParty, Callback<Integer> callback) {
    this.deliveries.add(new Delivery(agent, key, id, result, firstParty, callback));
  }
}
