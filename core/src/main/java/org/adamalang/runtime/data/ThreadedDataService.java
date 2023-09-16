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
package org.adamalang.runtime.data;

import org.adamalang.common.Callback;
import org.adamalang.common.NamedThreadFactory;
import org.adamalang.runtime.contracts.DeleteTask;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Supplier;

/** an implementation of data service that federates out by hashing keys to executors */
public class ThreadedDataService implements DataService {

  private final ExecutorService[] executors;
  private final DataService[] services;

  public ThreadedDataService(int nThread, Supplier<DataService> dataServiceSupplier) {
    this.executors = new ExecutorService[nThread];
    this.services = new DataService[nThread];
    for (int k = 0; k < nThread; k++) {
      this.executors[k] = Executors.newSingleThreadExecutor(new NamedThreadFactory("dataservice-" + k));
      this.services[k] = dataServiceSupplier.get();
    }
  }

  public CountDownLatch shutdown() {
    CountDownLatch latch = new CountDownLatch(executors.length);
    for (int k = 0; k < executors.length; k++) {
      ExecutorService executor = executors[k];
      executor.execute(() -> {
        executor.shutdown();
        latch.countDown();
      });
    }
    return latch;
  }

  @Override
  public void get(Key key, Callback<LocalDocumentChange> callback) {
    at(key, (service) -> service.get(key, callback));
  }

  /** internal: enter an executor for the given key */
  private void at(Key key, Consumer<DataService> next) {
    int tId = key.hashCode() % executors.length;
    executors[tId].execute(() -> {
      next.accept(services[tId]);
    });
  }

  @Override
  public void initialize(Key key, RemoteDocumentUpdate patch, Callback<Void> callback) {
    at(key, (service) -> service.initialize(key, patch, callback));
  }

  @Override
  public void patch(Key key, RemoteDocumentUpdate[] patches, Callback<Void> callback) {
    at(key, (service) -> service.patch(key, patches, callback));
  }

  @Override
  public void compute(Key key, ComputeMethod method, int seq, Callback<LocalDocumentChange> callback) {
    at(key, (service) -> service.compute(key, method, seq, callback));
  }

  @Override
  public void delete(Key key, DeleteTask task, Callback<Void> callback) {
    at(key, (service) -> service.delete(key, task, callback));
  }

  @Override
  public void snapshot(Key key, DocumentSnapshot snapshot, Callback<Integer> callback) {
    at(key, (service) -> service.snapshot(key, snapshot, callback));
  }

  @Override
  public void shed(Key key) {
    at(key, service -> service.shed(key));
  }

  @Override
  public void close(Key key, Callback<Void> callback) {
    at(key, (service) -> service.close(key, callback));
  }

}
