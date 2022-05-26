/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.runtime.sys.mocks;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.runtime.data.*;
import org.adamalang.runtime.natives.NtClient;

import java.util.HashMap;

public class MockArchiveDataServiceWrapper implements ArchivingDataService {
  private final DataService data;
  private final HashMap<String, String> archive;

  public MockArchiveDataServiceWrapper(DataService data) {
    this.data = data;
    this.archive = new HashMap<>();
  }

  @Override
  public void restore(Key key, String archiveKey, Callback<Void> callback) {
    if (key.key.contains("fail-restore")) {
      callback.failure(new ErrorCodeException(-2000));
      return;
    }
    String value;
    synchronized (archive) {
      value = archive.get(archiveKey);
    }
    if (value == null) {
      callback.failure(new ErrorCodeException(-3000));
      return;
    }
    // TODO: sort out a better way to restore an arbitrary data source for testing
    data.initialize(key, new RemoteDocumentUpdate(1, 1, NtClient.NO_ONE, "restore", value, "{}", false, 1, 0, UpdateType.Internal), callback);
  }

  @Override
  public void backup(Key key, Callback<String> callback) {
    String archiveKey = key.key + "_" + System.currentTimeMillis();
    if (key.key.contains("fail-backup")) {
      callback.failure(new ErrorCodeException(-1000));
      return;
    }
    data.get(key, new Callback<LocalDocumentChange>() {
      @Override
      public void success(LocalDocumentChange value) {
        synchronized (archiveKey) {
          archive.put(archiveKey, value.patch);
        }
        callback.success(archiveKey);
      }

      @Override
      public void failure(ErrorCodeException ex) {

      }
    });
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
