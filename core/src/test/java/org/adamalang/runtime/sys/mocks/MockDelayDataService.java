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
package org.adamalang.runtime.sys.mocks;

import org.adamalang.common.Callback;
import org.adamalang.runtime.contracts.DeleteTask;
import org.adamalang.runtime.data.*;
import org.junit.Assert;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class MockDelayDataService implements DataService {
  private final ArrayList<CountDownLatch> latches;
  private DataService parent;
  private boolean paused;
  private ArrayList<Runnable> actions;

  public MockDelayDataService(DataService parent) {
    this.paused = false;
    this.parent = parent;
    this.actions = new ArrayList<>();
    this.latches = new ArrayList<>();
  }

  public synchronized void pause() {
    System.out.println("PAUSE");
    this.paused = true;
  }

  public synchronized void set(DataService service) {
    this.parent = service;
  }

  public void unpause() {
    System.out.println("UNPAUSED");
    for (Runnable r : unpauseWithLock()) {
      r.run();
    }
  }

  private synchronized ArrayList<Runnable> unpauseWithLock() {
    ArrayList<Runnable> copy = new ArrayList<>(actions);
    actions.clear();
    paused = false;
    return copy;
  }

  public void once() {
    System.out.println("ONCE");
    onceWithLock().run();
  }

  private synchronized Runnable onceWithLock() {
    return actions.remove(0);
  }

  @Override
  public void get(Key key, Callback<LocalDocumentChange> callback) {
    enqueue(() -> parent.get(key, callback));
  }

  private void enqueue(Runnable run) {
    enqueueWithLock(run).run();
  }

  private synchronized Runnable enqueueWithLock(Runnable run) {
    if (paused) {
      actions.add(run);
      Iterator<CountDownLatch> it = latches.iterator();
      while (it.hasNext()) {
        CountDownLatch latch = it.next();
        latch.countDown();
        if (latch.getCount() == 0) {
          it.remove();
        }
      }
      return () -> {};
    } else {
      return run;
    }
  }

  @Override
  public void initialize(Key key, RemoteDocumentUpdate patch, Callback<Void> callback) {
    enqueue(() -> parent.initialize(key, patch, callback));
  }

  @Override
  public void patch(Key key, RemoteDocumentUpdate[] patches, Callback<Void> callback) {
    enqueue(() -> parent.patch(key, patches, callback));
  }

  @Override
  public void compute(
      Key key, ComputeMethod method, int seq, Callback<LocalDocumentChange> callback) {
    enqueue(() -> parent.compute(key, method, seq, callback));
  }

  @Override
  public void delete(Key key, DeleteTask task, Callback<Void> callback) {
    enqueue(() -> parent.delete(key, task, callback));
  }

  @Override
  public void snapshot(Key key, DocumentSnapshot snapshot, Callback<Integer> callback) {
    enqueue(() -> parent.snapshot(key, snapshot, callback));
  }

  @Override
  public void shed(Key key) {
    enqueue(() -> parent.shed(key));
  }

  @Override
  public void inventory(Callback<Set<Key>> callback) {
    enqueue(() -> parent.inventory(callback));
  }

  @Override
  public void close(Key key, Callback<Void> callback) {
    enqueue(() -> parent.close(key, callback));
  }

  public synchronized Runnable latchAt(int count) {
    CountDownLatch latch = new CountDownLatch(count);
    latches.add(latch);
    return () -> {
      try {
        Assert.assertTrue(latch.await(2000, TimeUnit.MILLISECONDS));
      } catch (InterruptedException ie) {
        Assert.fail();
      }
    };
  }
}
