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
import org.adamalang.runtime.contracts.DeleteTask;

public class PrefixSplitDataService implements DataService {
  private final DataService dataServiceA;
  private final String prefixB;
  private final DataService dataServiceB;

  public PrefixSplitDataService(DataService dataServiceA, String prefixB, DataService dataServiceB) {
    this.dataServiceA = dataServiceA;
    this.prefixB = prefixB;
    this.dataServiceB = dataServiceB;
  }

  @Override
  public void get(Key key, Callback<LocalDocumentChange> callback) {
    ds(key).get(key, callback);
  }

  public DataService ds(Key key) {
    if (key.key.startsWith(prefixB)) {
      return dataServiceB;
    }
    return dataServiceA;
  }

  @Override
  public void initialize(Key key, RemoteDocumentUpdate patch, Callback<Void> callback) {
    ds(key).initialize(key, patch, callback);
  }

  @Override
  public void patch(Key key, RemoteDocumentUpdate[] patches, Callback<Void> callback) {
    ds(key).patch(key, patches, callback);
  }

  @Override
  public void compute(Key key, ComputeMethod method, int seq, Callback<LocalDocumentChange> callback) {
    ds(key).compute(key, method, seq, callback);
  }

  @Override
  public void delete(Key key, DeleteTask task, Callback<Void> callback) {
    ds(key).delete(key, task, callback);
  }

  @Override
  public void snapshot(Key key, DocumentSnapshot snapshot, Callback<Integer> callback) {
    ds(key).snapshot(key, snapshot, callback);
  }

  @Override
  public void shed(Key key) {
    ds(key).shed(key);
  }

  @Override
  public void close(Key key, Callback<Void> callback) {
    ds(key).close(key, callback);
  }
}
