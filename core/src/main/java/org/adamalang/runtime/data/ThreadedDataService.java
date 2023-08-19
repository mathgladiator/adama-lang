/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
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
