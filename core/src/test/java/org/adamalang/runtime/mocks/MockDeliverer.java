/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
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
