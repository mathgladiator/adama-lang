/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.runtime.data;

import org.adamalang.common.Callback;

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
  public void delete(Key key, Callback<Void> callback) {
    ds(key).delete(key, callback);
  }

  @Override
  public void snapshot(Key key, int seq, String snapshot, int history, Callback<Integer> callback) {
    ds(key).snapshot(key, seq, snapshot, history, callback);
  }

  @Override
  public void close(Key key, Callback<Void> callback) {
    ds(key).close(key, callback);
  }

  @Override
  public void archive(Key key, ArchiveWriter writer) {
    ds(key).archive(key, writer);
  }
}
