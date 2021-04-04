package org.adamalang.support.testgen;

import org.adamalang.runtime.DurableLivingDocument;
import org.adamalang.runtime.contracts.DataCallback;
import org.adamalang.runtime.contracts.DataService;
import org.adamalang.runtime.json.JsonAlgebra;
import org.adamalang.runtime.json.JsonStreamReader;

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
  public void create(DataCallback<Long> callback) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void get(long documentId, DataCallback<LocalDocumentChange> callback) {
    if (data != null) {
      callback.success(new LocalDocumentChange(data, 0));
    } else {
      callback.failure(0, new UnsupportedOperationException());
    }
  }

  public static final DataCallback<Integer> NOOPINT = new DataCallback<Integer>() {

    @Override
    public void success(Integer value) {
    }

    @Override
    public void progress(int stage) {
    }

    @Override
    public void failure(int stage, Exception ex) {
      throw new RuntimeException(ex);
    }
  };

  public static class DumbDurableLivingDocumentAcquire implements DataCallback<DurableLivingDocument> {
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
    public void progress(int stage) {
    }

    @Override
    public void failure(int stage, Exception ex) {
      throw new RuntimeException(ex);
    }
  }

  @Override
  public void patch(long documentId, RemoteDocumentUpdate patch, DataCallback<Void> callback) {
    updates.accept(patch);
    JsonStreamReader reader = new JsonStreamReader(patch.redo);
    tree = JsonAlgebra.merge(tree, reader.readJavaTree());
    callback.success(null);
  }

  @Override
  public long fork(long documentId, long seqEnd, DataCallback<LocalDocumentChange> callback) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void rewind(long documentId, long seqEnd, DataCallback<LocalDocumentChange> callback) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void unsend(long documentId, long seqBegin, long seqEnd, DataCallback<LocalDocumentChange> callback) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void delete(long documentId, DataCallback<Long> callback) {
    throw new UnsupportedOperationException();
  }
}
