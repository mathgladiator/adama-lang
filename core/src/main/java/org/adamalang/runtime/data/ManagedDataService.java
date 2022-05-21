package org.adamalang.runtime.data;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.NamedRunnable;
import org.adamalang.common.SimpleExecutor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class ManagedDataService implements DataService {
  private final FinderService finder;
  private final ArchivingDataService data;
  private final String region;
  private final String target;
  private final HashMap<Key, DocumentStateMachine> documents;
  private final SimpleExecutor executor;

  private static enum DocumentState {
    Ready,
    RestoringArchiveLoad,
    RestoringArchiveDelete,
  }

  private static class DocumentStateMachine {
    private DocumentState state;
    private ArrayList<Runnable> readyQueue;

    private DocumentStateMachine(DocumentState state) {
      this.state = state;
      this.readyQueue = null;
    }
  }

  public ManagedDataService(FinderService finder, ArchivingDataService data, String region, String target) {
    this.finder = finder;
    this.data = data;
    this.region = region;
    this.target = target;
    this.documents = new HashMap<>();
    this.executor = SimpleExecutor.create("managed-data-service");
  }

  @Override
  public void initialize(Key key, RemoteDocumentUpdate patch, Callback<Void> callback) {
    finder.create(key, region, target, new Callback<Void>() {
      @Override
      public void success(Void value) {
        executor.execute(new NamedRunnable("mds-initialize") {
          @Override
          public void execute() throws Exception {
            data.initialize(key, patch, callback);
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
  public void close(Key key, Callback<Void> callback) {

  }

  public void executeOnReady(Key key, boolean mutate, Runnable runnable) {

    /**
     *     executor.execute(new NamedRunnable("mds-get") {
     *       @Override
     *       public void execute() throws Exception {
     *         DocumentStateMachine dsm = documents.get(key);
     *         if (dsm == null) {
     *           finder.find(key, new Callback<FinderService.Result>() {
     *             @Override
     *             public void success(FinderService.Result findResult) {
     *               executor.execute(new NamedRunnable("mds-get-back-in") {
     *                 @Override
     *                 public void execute() throws Exception {
     *                   if (documents.get(key) == null) {
     *                     switch (findResult.location) {
     *                       case Archive:
     *
     *                         return;
     *                       case Machine:
     *                         if (findResult.value.equals(target)) {
     *                           documents.put(key, DocumentStateMachine.Ready);
     *                           data.get(key, callback);
     *                         } else {
     *                           callback.failure(new ErrorCodeException(-1));
     *                         }
     *                         return;
     *                     }
     *                   } else {
     *                     // multiple gets/inits to the same key, slower finder service
     *                   }
     *                 }
     *               });
     *             }
     *
     *             @Override
     *             public void failure(ErrorCodeException ex) {
     *               callback.failure(ex);
     *             }
     *           });
     *         } else {
     *           switch (dsm) {
     *             case Ready:
     *               data.get(key, callback);
     *               return;
     *
     *           }
     *         }
     *       }
     *     });
     */
  }

  @Override
  public void get(Key key, Callback<LocalDocumentChange> callback) {
    executeOnReady(key, false, () -> {
      data.get(key, callback);
    });
  }

  @Override
  public void patch(Key key, RemoteDocumentUpdate[] patches, Callback<Void> callback) {
    executeOnReady(key, true, () -> {
      data.patch(key, patches, callback);
    });
  }

  @Override
  public void compute(Key key, ComputeMethod method, int seq, Callback<LocalDocumentChange> callback) {
    executeOnReady(key, false, () -> {
      data.compute(key, method, seq, callback);
    });
  }

  @Override
  public void delete(Key key, Callback<Void> callback) {
    data.delete(key, Callback.DONT_CARE_VOID);
    finder.delete(key, target, callback);
  }

  @Override
  public void snapshot(Key key, int seq, String snapshot, int history, Callback<Integer> callback) {
    executeOnReady(key, true, () -> {
      data.snapshot(key, seq, snapshot, history, callback);
    });
  }
}
