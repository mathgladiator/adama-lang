package org.adamalang.caravan;

import org.adamalang.ErrorCodes;
import org.adamalang.caravan.contracts.TranslateKeyService;
import org.adamalang.caravan.data.DurableListStore;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.NamedRunnable;
import org.adamalang.common.SimpleExecutor;
import org.adamalang.runtime.data.*;

import java.util.function.Consumer;

public class CaravanDataService implements DataService {
  private final TranslateKeyService translate;
  private final DurableListStore store;
  private final SimpleExecutor executor;

  public CaravanDataService(TranslateKeyService translate, DurableListStore store, SimpleExecutor executor) {
    this.translate = translate;
    this.store = store;
    this.executor = executor;
  }

  /** execute with the translation service and jump into the executor */
  private <T> void execute(String name, Key key, Callback<T> callback, Consumer<Long> action) {
    translate.lookup(key, new Callback<Long>() {
      @Override
      public void success(Long id) {
        executor.execute(new NamedRunnable(name) {
          @Override
          public void execute() throws Exception {
            action.accept(id);
          }
        });
      }

      @Override
      public void failure(ErrorCodeException ex) {
        callback.failure(ex);
      }
    });
  }

  @Override
  public void get(Key key, Callback<LocalDocumentChange> callback) {
    execute("get", key, callback, (id) -> {
      // need an implementation of ByteArrayStream that builds the final document
      // we also need to track what the appends are and keep useful information for the various computations
    });
  }

  @Override
  public void initialize(Key key, RemoteDocumentUpdate patch, Callback<Void> callback) {
    execute("initialize", key, callback, (id) -> {
      if (store.exists(id)) {
        callback.failure(new ErrorCodeException(ErrorCodes.UNIVERSAL_INITIALIZE_FAILURE));
        return;
      }
      // convert patch to byte[]
      byte[] bytes = null;
      if (store.append(id, bytes, () -> {
        callback.success(null);
      })) {
        callback.failure(new ErrorCodeException(-1));
      }
    });
  }

  @Override
  public void patch(Key key, RemoteDocumentUpdate[] patches, Callback<Void> callback) {
    execute("patch", key, callback, (id) -> {
      // convert patches to byte[]
      byte[] bytes = null;
      if (store.append(id, bytes, () -> {
        callback.success(null);
      })) {
        callback.failure(new ErrorCodeException(-1));
      }
    });
  }

  @Override
  public void compute(Key key, ComputeMethod method, int seq, Callback<LocalDocumentChange> callback) {
    execute("compute", key, callback, (id) -> {
      callback.failure(new ErrorCodeException(-1));
    });
  }

  @Override
  public void delete(Key key, Callback<Void> callback) {
    execute("delete", key, callback, (id) -> {
      if (store.delete(id, () -> {
        callback.success(null);
      })) {
        callback.success(null);
      }
    });
  }

  @Override
  public void compactAndSnapshot(Key key, int seq, String snapshot, int history, Callback<Integer> callback) {
    execute("snapshot", key, callback, (id) -> {
      // write a snapshot to the log
    });
  }
}
