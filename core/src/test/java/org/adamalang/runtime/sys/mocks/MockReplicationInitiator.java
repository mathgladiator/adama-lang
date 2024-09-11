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
package org.adamalang.runtime.sys.mocks;

import org.adamalang.common.Callback;
import org.adamalang.runtime.data.DataObserver;
import org.adamalang.runtime.data.Key;
import org.adamalang.runtime.sys.readonly.ReplicationInitiator;

import java.util.concurrent.atomic.AtomicInteger;

public class MockReplicationInitiator implements ReplicationInitiator {
  private final AtomicInteger starts;
  private final AtomicInteger cancels;
  private boolean simpleMode;
  private final String initialize;
  private final String change;
  private DataObserver lastObserver;

  public MockReplicationInitiator() {
    this.starts = new AtomicInteger(0);
    this.cancels = new AtomicInteger(0);
    this.simpleMode = true;
    this.initialize = "{}";
    this.change = "{}";
  }

  public MockReplicationInitiator(String initialize, String change) {
    this.starts = new AtomicInteger(0);
    this.cancels = new AtomicInteger(0);
    this.simpleMode = true;
    this.initialize = initialize;
    this.change = change;
  }

  public DataObserver getLastObserver() {
    return lastObserver;
  }

  @Override
  public void startDocumentReplication(Key key, DataObserver observer, Callback<Runnable> cancel) {
    lastObserver = observer;
    starts.incrementAndGet();
    cancel.success(() -> {
      cancels.incrementAndGet();
    });
    if (simpleMode) {
      observer.start(initialize);
      if (change != null) {
        observer.change(change);
      }
    }
  }
}
