/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.support.testgen;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.runtime.data.*;
import org.adamalang.runtime.json.JsonAlgebra;
import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.json.PrivateView;
import org.adamalang.runtime.sys.DurableLivingDocument;

import java.util.HashMap;
import java.util.HashSet;
import java.util.function.Consumer;

public class DumbDataService implements DataService {
  public static Callback<PrivateView> makePrinterPrivateView(String prefix, StringBuilder sb) {
    return new Callback<PrivateView>() {
      @Override
      public void success(PrivateView value) {
        sb.append(prefix + ": CREATED PRIVATE VIEW\n");
      }

      @Override
      public void failure(ErrorCodeException ex) {
        sb.append(prefix + ": FAILED PRIVATE VIEW DUE TO:" + ex.code + "\n");
      }
    };
  }
  public static Callback<Integer> makePrinterInt(String prefix, StringBuilder sb) {
    return new Callback<>() {
      @Override
      public void success(Integer value) {
        sb.append(prefix + "|SUCCESS:" + value + "\n");
      }

      @Override
      public void failure(ErrorCodeException ex) {
        sb.append(prefix + "|FAILURE:" + ex.code + "\n");
      }
    };
  }
  public final HashSet<Key> deleted;
  public boolean deletesWork = true;
  public boolean computesWork = true;
  public boolean dropPatches = false;
  private Object tree;
  private String data;
  private final Consumer<RemoteDocumentUpdate> updates;

  public DumbDataService(Consumer<RemoteDocumentUpdate> updates) {
    this.tree = new HashMap<String, Object>();
    this.deleted = new HashSet<>();
    this.data = null;
    this.updates = updates;
  }

  public void setData(String data) {
    this.data = data;
  }

  @Override
  public void get(Key key, Callback<LocalDocumentChange> callback) {
    if (data != null) {
      callback.success(new LocalDocumentChange(data, 1, 1));
    } else {
      callback.failure(new ErrorCodeException(0, new UnsupportedOperationException()));
    }
  }

  @Override
  public void initialize(Key key, RemoteDocumentUpdate patch, Callback<Void> callback) {
    patch(key, new RemoteDocumentUpdate[]{patch}, callback);
  }

  @Override
  public void patch(Key key, RemoteDocumentUpdate[] patches, Callback<Void> callback) {
    if (dropPatches) {
      return;
    }
    for (RemoteDocumentUpdate patch : patches) {
      updates.accept(patch);
      JsonStreamReader reader = new JsonStreamReader(patch.redo);
      tree = JsonAlgebra.merge(tree, reader.readJavaTree(), false);
    }
    callback.success(null);
  }

  @Override
  public void compute(Key key, ComputeMethod method, int seq, Callback<LocalDocumentChange> callback) {
    if (computesWork) {
      if (method == ComputeMethod.Rewind) {
        callback.success(new LocalDocumentChange("{\"x\":1000}", 1, 1));
      }
    } else {
      callback.failure(new ErrorCodeException(23456));
    }
  }

  @Override
  public void delete(Key key, Callback<Void> callback) {
    if (deletesWork) {
      deleted.add(key);
      callback.success(null);
    } else {
      callback.failure(new ErrorCodeException(1234567));
    }
  }

  @Override
  public void snapshot(Key key, DocumentSnapshot snapshot, Callback<Integer> callback) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void close(Key key, Callback<Void> callback) {
    callback.success(null);
  }

  public static class DumbDurableLivingDocumentAcquire implements Callback<DurableLivingDocument> {
    private DurableLivingDocument value;

    public DumbDurableLivingDocumentAcquire() {
      this.value = null;
    }

    public DurableLivingDocument get() {
      if (value == null) {
        throw new NullPointerException();
      }
      return value;
    }

    @Override
    public void success(DurableLivingDocument value) {
      this.value = value;
    }

    @Override
    public void failure(ErrorCodeException ex) {
      throw new RuntimeException(ex);
    }
  }
}
