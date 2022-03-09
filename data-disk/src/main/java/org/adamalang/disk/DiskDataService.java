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

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.disk.wal.WriteAheadMessage;
import org.adamalang.runtime.data.*;

import java.io.File;

/** the disk data service which aims for low latency commits */
public class DiskDataService implements DataService {
  private final DiskBase base;

  public DiskDataService(DiskBase base) {
    this.base = base;
  }

  @Override // GET
  public void get(Key key, Callback<LocalDocumentChange> callback) {
    DocumentMemoryLog memory = base.memory.get(key);
    if (memory != null) {
      if (memory.deleted()) {
        callback.failure(new ErrorCodeException(-1));
        return;
      }
      if (memory.hasSnapshot()) {
        // callback.success(new LocalDocumentChange(memory.current(), memory.outstanding()));
        return;
      }
    }
    /*
    File snapshot = base.fileFor(key, "SNAPSHOT");
    if (snapshot.exists()) {
      // TODO: load snapshot and get (seq, snapshot, history)
      File forward = base.fileFor(key, "FORWARD");
      if (forward.exists()) {
        // TODO: READ Changes from FORWARD, apply to SNAPSHOT
      }
      if (memory != null) {
        // memory.replay()
      }
      // EMIT SNAPSHOT + FORWARD[redo]
    } else {
      callback.failure(new ErrorCodeException(-1));
    }
    */


    // if the Integrator has a snapshot if exists in memory
    //    if the snapshot indicates a delete
    //      failure
    //    use the snapshot
    // else if it exists on disk
    //    pull a snapshot from disk
    // else
    //    failed not found
    // pull the sequencer of the snapshot
    // pull updates from the WAL
    // apply updates to the snapshot
  }

  @Override // WRITE
  public void initialize(Key key, RemoteDocumentUpdate patch, Callback<Void> callback) {
    DocumentMemoryLog memory = base.memory.get(key);
    if (memory != null) {
      if (!memory.deleted()) {
        callback.failure(new ErrorCodeException(-1));
        return;
      }
    } else {
      // TODL if File exists, then failure and return
    }

    WriteAheadMessage.Initialize initialize = new WriteAheadMessage.Initialize();
    initialize.space = key.space;
    initialize.key = key.key;
    initialize.initialize = new WriteAheadMessage.Change();
    initialize.initialize.copyFrom(patch);
    base.log.write(initialize, callback);
    initialize.apply(base.getOrCreate(key));
  }

  @Override // WRITE
  public void patch(Key key, RemoteDocumentUpdate[] patches, Callback<Void> callback) {
    WriteAheadMessage.Patch patch = new WriteAheadMessage.Patch();
    patch.space = key.space;
    patch.key = key.key;
    patch.changes = new WriteAheadMessage.Change[patches.length];
    for (int k = 0; k < patch.changes.length; k++) {
      patch.changes[k] = new WriteAheadMessage.Change();
      patch.changes[k].copyFrom(patches[k]);
    }
    base.log.write(patch, callback);
    patch.apply(base.getOrCreate(key));
  }

  @Override // READ
  public void compute(Key key, ComputeMethod method, int seq, Callback<LocalDocumentChange> callback) {
    if (method == ComputeMethod.HeadPatch) {
      callback.failure(new ErrorCodeException(-1));
    } else if (method == ComputeMethod.Rewind) {
      callback.failure(new ErrorCodeException(-1));
    } else {
      callback.failure(new ErrorCodeException(-1));
    }
  }

  @Override // WRITE
  public void delete(Key key, Callback<Void> callback) {
    WriteAheadMessage.Delete delete = new WriteAheadMessage.Delete();
    delete.space = key.space;
    delete.key = key.key;
    base.log.write(delete, callback);
    delete.apply(base.getOrCreate(key));
  }

  @Override // WRITE
  public void compactAndSnapshot(Key key, int seq, String snapshot, int history, Callback<Integer> callback) {
    WriteAheadMessage.Snapshot snap = new WriteAheadMessage.Snapshot();
    snap.space = key.space;
    snap.key = key.key;
    snap.seq = seq;
    snap.history = history;
    snap.document = snapshot;
    base.log.write(snap, new Callback<Void>() {
      @Override
      public void success(Void value) {
        callback.success(history);
      }

      @Override
      public void failure(ErrorCodeException ex) {
        callback.failure(ex);
      }
    });
    snap.apply(base.getOrCreate(key));
  }
}
