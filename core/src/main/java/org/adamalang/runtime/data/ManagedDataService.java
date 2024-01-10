/*
* Adama Platform and Language
* Copyright (C) 2021 - 2023 by Adama Platform Initiative, LLC
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as published
* by the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Affero General Public License for more details.
* 
* You should have received a copy of the GNU Affero General Public License
* along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
package org.adamalang.runtime.data;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.NamedRunnable;
import org.adamalang.runtime.contracts.DeleteTask;
import org.adamalang.runtime.data.managed.Action;
import org.adamalang.runtime.data.managed.Base;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.TreeSet;

/** a managed data source will convert and archiving data source into a dataservice such that the local state is managed by a finder. This lets data be uploaded/downloaded as needed. */
public class ManagedDataService implements DataService {
  private final Logger LOG = LoggerFactory.getLogger(ManagedDataService.class);
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
    base.finder.bind(key, new Callback<>() {
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
  public void delete(Key key, DeleteTask task, Callback<Void> callback) {
    base.finder.markDelete(key, new Callback<Void>() {
      @Override
      public void success(Void value) {
        task.executeAfterMark(new Callback<Void>() {
          @Override
          public void success(Void value) {
            deleteLocal(key, new Callback<>() {
              @Override
              public void success(Void value) {
                base.finder.commitDelete(key, callback);
              }

              @Override
              public void failure(ErrorCodeException ex) {
                callback.failure(ex);
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
      public void failure(ErrorCodeException ex) {
        LOG.error("delete-from-finder-failed:", ex);
        base.on(key, machine -> machine.close());
        callback.failure(ex);
      }
    });
  }

  private void deleteLocal(Key key, Callback<Void> callback) {
    base.on(key, (machine) -> {
      machine.delete();
      base.data.delete(key, DeleteTask.TRIVIAL, new Callback<Void>() {
        @Override
        public void success(Void value) {
          base.executor.execute(new NamedRunnable("managed-delete") {
            @Override
            public void execute() throws Exception {
              base.delete.deleteAllAssets(key, callback);
            }
          });
        }

        @Override
        public void failure(ErrorCodeException ex) {
          LOG.error("delete-from-storage-failed:", ex);
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
  public void shed(Key key) {
    base.on(key, machine -> {
      machine.shed();
    });
  }

  @Override
  public void inventory(Callback<Set<Key>> callback) {
    base.executor.execute(new NamedRunnable("inventory") {
      @Override
      public void execute() throws Exception {
        callback.success(new TreeSet<>(base.documents.keySet()));
      }
    });
  }

  @Override
  public void recover(Key key, DocumentRestore restore, Callback<Void> callback) {
    base.on(key, machine -> {
      machine.write(new Action(() -> {
        base.data.recover(key, restore, callback);
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
