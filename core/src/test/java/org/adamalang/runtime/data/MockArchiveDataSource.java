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
import org.adamalang.common.ErrorCodeException;

import java.util.ArrayList;

public class MockArchiveDataSource implements ArchivingDataService {
  private final DataService data;

  private ArrayList<String> events;

  public MockArchiveDataSource(DataService data) {
    this.data = data;
    this.events = new ArrayList<>();
  }

  @Override
  public synchronized void restore(Key key, String archiveKey, Callback<Void> callback) {
    if (key.key.equals("fail-restore")) {
      this.events.add("RESTORE[FAILURE]:" + key.space + "/" + key.key + "+" + archiveKey);
      callback.failure(new ErrorCodeException(-100));
      return;
    }
    this.events.add("RESTORE:" + key.space + "/" + key.key + "+" + archiveKey);
    callback.success(null);
  }

  @Override
  public synchronized void backup(Key key, Callback<String> callback) {
    if (key.key.equals("fail-backup")) {
      this.events.add("BACKUP[FAILURE]:" + key.space + "/" + key.key);
      callback.failure(new ErrorCodeException(-200));
      return;
    }
    this.events.add("BACKUP:" + key.space + "/" + key.key);
    String archiveKey = "ARCHIVE_KEY_" + events.size();
    callback.success(archiveKey);
  }

  @Override
  public void get(Key key, Callback<LocalDocumentChange> callback) {
    data.get(key, callback);
  }

  @Override
  public void initialize(Key key, RemoteDocumentUpdate patch, Callback<Void> callback) {
    data.initialize(key, patch, callback);
  }

  @Override
  public void patch(Key key, RemoteDocumentUpdate[] patches, Callback<Void> callback) {
    data.patch(key, patches, callback);
  }

  @Override
  public void compute(Key key, ComputeMethod method, int seq, Callback<LocalDocumentChange> callback) {
    data.compute(key, method, seq, callback);
  }

  @Override
  public void delete(Key key, Callback<Void> callback) {
    data.delete(key, callback);
  }

  @Override
  public void snapshot(Key key, int seq, String snapshot, int history, Callback<Integer> callback) {
    data.snapshot(key, seq, snapshot, history, callback);
  }

  @Override
  public void close(Key key, Callback<Void> callback) {
    data.close(key, callback);
  }
}
