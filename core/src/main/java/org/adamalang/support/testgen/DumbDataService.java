/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * which is in the root directory of the repository. This file is part of the 'Adama'
 * project which is a programming language and document store for board games.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2021 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.support.testgen;

import org.adamalang.runtime.DurableLivingDocument;
import org.adamalang.runtime.contracts.Callback;
import org.adamalang.runtime.contracts.DataService;
import org.adamalang.runtime.exceptions.ErrorCodeException;
import org.adamalang.runtime.json.JsonAlgebra;
import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.json.PrivateView;
import org.adamalang.runtime.natives.NtClient;

import java.util.HashMap;
import java.util.function.Consumer;

public class DumbDataService implements DataService {
  private Object tree;
  private String data;
  private Consumer<RemoteDocumentUpdate> updates;
  public DumbDataService(Consumer<RemoteDocumentUpdate> updates) {
    this.tree = new HashMap<String, Object>();
    this.data = null;
    this.updates = updates;
  }

  public void setData(String data) {
    this.data = data;
  }

  @Override
  public void create(Callback<Long> callback) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void get(long documentId, Callback<LocalDocumentChange> callback) {
    if (data != null) {
      callback.success(new LocalDocumentChange(data, 0));
    } else {
      callback.failure(new ErrorCodeException(0, new UnsupportedOperationException()));
    }
  }

  public static final Callback<PrivateView> NOOPPrivateView = new Callback<PrivateView>() {

    @Override
    public void success(PrivateView value) {
    }

    @Override
    public void failure(ErrorCodeException ex) {
      throw new RuntimeException(ex);
    }
  };


  public static final Callback<Integer> NOOPINT = new Callback<Integer>() {

    @Override
    public void success(Integer value) {
    }


    @Override
    public void failure(ErrorCodeException ex) {
      throw new RuntimeException(ex);
    }
  };

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

  @Override
  public void initialize(long documentId, RemoteDocumentUpdate patch, Callback<Void> callback) {
    patch(documentId, patch, callback);
  }

  @Override
  public void patch(long documentId, RemoteDocumentUpdate patch, Callback<Void> callback) {
    updates.accept(patch);
    JsonStreamReader reader = new JsonStreamReader(patch.redo);
    tree = JsonAlgebra.merge(tree, reader.readJavaTree());
    callback.success(null);
  }

  @Override
  public void fork(long oldDocumentId, long newDocumentId, NtClient who, String marker, Callback<LocalDocumentChange> callback) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void rewind(long documentId, NtClient who, String marker, Callback<LocalDocumentChange> callback) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void unsend(long documentId, NtClient who, String marker, Callback<LocalDocumentChange> callback) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void delete(long documentId, Callback<Long> callback) {
    throw new UnsupportedOperationException();
  }
}
