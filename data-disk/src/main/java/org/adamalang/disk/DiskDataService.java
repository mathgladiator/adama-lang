/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.disk;

import org.adamalang.ErrorCodes;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.NamedRunnable;
import org.adamalang.disk.wal.WriteAheadMessage;
import org.adamalang.runtime.data.*;

import java.io.FileNotFoundException;
import java.io.IOException;

/** the disk data service which aims for low latency commits */
public class DiskDataService implements DataService {
  private final DiskBase base;
  private final WriteAheadLog log;

  public DiskDataService(DiskBase base, WriteAheadLog log) {
    this.base = base;
    this.log = log;
  }

  @Override // GET
  public void get(Key key, Callback<LocalDocumentChange> callbackRaw) {
    Callback<LocalDocumentChange> callback = this.base.metrics.disk_data_get.wrap(callbackRaw);
    base.executor.execute(new NamedRunnable("dds-get") {
      @Override
      public void execute() throws Exception {
        DocumentMemoryLog memory = base.getOrCreate(key);
        if (memory.get_IsDeleted()) {
          callback.failure(new ErrorCodeException(-1));
          return;
        }
        try {
          callback.success(memory.get_Load());
        } catch (IOException ioe) {
          if (ioe instanceof FileNotFoundException) {
            callback.failure(new ErrorCodeException(ErrorCodes.UNIVERSAL_LOOKUP_FAILED));
          } else {
            callback.failure(new ErrorCodeException(-1, ioe));
          }
          base.remove(key);
        }
      }
    });
  }

  @Override // WRITE
  public void initialize(Key key, RemoteDocumentUpdate patch, Callback<Void> callbackRaw) {
    Callback<Void> callback = this.base.metrics.disk_data_initialize.wrap(callbackRaw);
    base.executor.execute(new NamedRunnable("dds-initialize") {
      @Override
      public void execute() throws Exception {
        DocumentMemoryLog memory = base.memory.get(key);
        if (memory.canInitialize()) {
          callback.failure(new ErrorCodeException(ErrorCodes.UNIVERSAL_INITIALIZE_FAILURE));
          base.remove(key);
          return;
        }

        WriteAheadMessage.Initialize initialize = new WriteAheadMessage.Initialize();
        initialize.space = key.space;
        initialize.key = key.key;
        initialize.initialize = new WriteAheadMessage.Change();
        initialize.initialize.copyFrom(patch);

        log.write(initialize, new ApplyMessageCallback<>(memory, initialize, callback));
      }
    });
  }

  @Override // WRITE
  public void patch(Key key, RemoteDocumentUpdate[] patches, Callback<Void> callbackRaw) {
    Callback<Void> callback = this.base.metrics.disk_data_patch.wrap(callbackRaw);
    base.executor.execute(new NamedRunnable("dds-patch") {
      @Override
      public void execute() throws Exception {
        DocumentMemoryLog memory = base.getOrCreate(key);
        if (!memory.ensureLoaded(callback)) {
          base.memory.remove(key);
          return;
        }

        if (!memory.canPatch(patches[0].seqBegin)) {
          callback.failure(new ErrorCodeException(-1));
          return;
        }
        WriteAheadMessage.Patch patch = new WriteAheadMessage.Patch();
        patch.space = key.space;
        patch.key = key.key;
        patch.changes = new WriteAheadMessage.Change[patches.length];
        for (int k = 0; k < patch.changes.length; k++) {
          patch.changes[k] = new WriteAheadMessage.Change();
          patch.changes[k].copyFrom(patches[k]);
        }
        log.write(patch, new ApplyMessageCallback<>(memory, patch, callback));
      }
    });
  }

  @Override // READ
  public void compute(Key key, ComputeMethod method, int seq, Callback<LocalDocumentChange> callbackRaw) {
    Callback<LocalDocumentChange> callback = this.base.metrics.disk_data_compute.wrap(callbackRaw);
    base.executor.execute(new NamedRunnable("dds-compute", method.toString()) {
      @Override
      public void execute() throws Exception {
        if (method == ComputeMethod.HeadPatch) {
          callback.failure(new ErrorCodeException(-1));
        } else if (method == ComputeMethod.Rewind) {
          callback.failure(new ErrorCodeException(-1));
        } else {
          callback.failure(new ErrorCodeException(-1));
        }
      }
    });
  }

  @Override // WRITE
  public void delete(Key key, Callback<Void> callbackRaw) {
    Callback<Void> callback = this.base.metrics.disk_data_delete.wrap(callbackRaw);
    base.executor.execute(new NamedRunnable("dds-delete") {
      @Override
      public void execute() throws Exception {
        DocumentMemoryLog memory = base.memory.get(key);
        WriteAheadMessage.Delete delete = new WriteAheadMessage.Delete();
        delete.space = key.space;
        delete.key = key.key;

        log.write(delete, new ApplyMessageCallback<>(memory, delete, callback));
      }
    });
  }

  @Override // WRITE
  public void compactAndSnapshot(Key key, int seq, String snapshot, int history, Callback<Integer> callbackRaw) {
    Callback<Integer> callback = this.base.metrics.disk_data_snapshot.wrap(callbackRaw);
    base.executor.execute(new NamedRunnable("dds-snapshot") {
      @Override
      public void execute() throws Exception {
        DocumentMemoryLog memory = base.getOrCreate(key);
        if (!memory.ensureLoaded(callback)) {
          base.memory.remove(key);
          return;
        }

        WriteAheadMessage.Snapshot snap = new WriteAheadMessage.Snapshot();
        snap.space = key.space;
        snap.key = key.key;
        snap.seq = seq;
        snap.history = history;
        snap.document = snapshot;

        Callback<Void> adaptedCallback = new Callback<Void>() {
          @Override
          public void success(Void value) {
            callback.success(history);
          }

          @Override
          public void failure(ErrorCodeException ex) {
            callback.failure(ex);
          }
        };

        log.write(snap, new ApplyMessageCallback<>(memory, snap, adaptedCallback));
      }
    });
  }
}
