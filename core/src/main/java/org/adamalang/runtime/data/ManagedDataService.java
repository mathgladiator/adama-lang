/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.runtime.data;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.NamedRunnable;
import org.adamalang.runtime.data.managed.Action;
import org.adamalang.runtime.data.managed.Base;

/** a managed data source will convert and archiving data source into a dataservice such that the local state is managed by a finder. This lets data be uploaded/downloaded as needed. */
public class ManagedDataService implements DataService {
  private final Base base;

  public ManagedDataService(Base base) {
    this.base = base;
  }

  @Override
  public void get(Key key, Callback<LocalDocumentChange> callback) {
    base.on(key, (machine) -> {
      machine.read(new Action(() -> {
        base.data.get(key, callback);
      }, callback));
    });
  }

  @Override
  public void initialize(Key key, RemoteDocumentUpdate patch, Callback<Void> callback) {
    base.finder.bind(key, base.target, new Callback<Void>() {
      @Override
      public void success(Void value) {
        base.on(key, (machine) -> {
          machine.write(new Action(() -> {
            base.data.initialize(key, patch, callback);
          }, callback));
        });
      }

      @Override
      public void failure(ErrorCodeException ex) {
        callback.failure(ex);
      }
    });
  }

  @Override
  public void patch(Key key, RemoteDocumentUpdate[] patches, Callback<Void> callback) {
    base.on(key, (machine) -> {
      machine.write(new Action(() -> {
        base.data.patch(key, patches, callback);
      }, callback));
    });
  }

  @Override
  public void compute(Key key, ComputeMethod method, int seq, Callback<LocalDocumentChange> callback) {
    base.on(key, (machine) -> {
      machine.read(new Action(() -> {
        base.data.compute(key, method, seq, callback);
      }, callback));
    });
  }

  @Override
  public void delete(Key key, Callback<Void> callback) {
    base.finder.delete(key, base.target, new Callback<Void>() {
      @Override
      public void success(Void value) {
        deleteLocal(key, callback);
      }

      @Override
      public void failure(ErrorCodeException ex) {
        /* There are two core failure modes.

           (I) It failed and the mapping is still valid. In this case, failing the request is appropriate and the user is free to retry.

           (II) It actually was deleted and the network failed to return. In this case, we have a problem of leaking data.

           This is OK if and only IF the finder and local storage communicate via ids which is the case for caravan.
        */
        callback.failure(ex);
      }
    });
  }

  private void deleteLocal(Key key, Callback<Void> callback) {
    base.on(key, (machine) -> {
      machine.delete();
      base.data.delete(key, new Callback<Void>() {
        @Override
        public void success(Void value) {
          base.executor.execute(new NamedRunnable("managed-delete") {
            @Override
            public void execute() throws Exception {
              machine.close();
              base.documents.remove(key);
              callback.success(null);
            }
          });
        }

        @Override
        public void failure(ErrorCodeException ex) {
          callback.failure(ex);
        }
      });
    });
  }

  @Override
  public void snapshot(Key key, DocumentSnapshot snapshot, Callback<Integer> callback) {
    base.on(key, (machine) -> {
      machine.write(new Action(() -> {
        base.data.snapshot(key, snapshot, callback);
      }, callback));
    });
  }

  @Override
  public void close(Key key, Callback<Void> callback) {
    base.on(key, (machine) -> {
      machine.close();
      callback.success(null);
    });
  }
}
