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

import org.adamalang.ErrorCodes;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.TimeSource;
import org.adamalang.runtime.contracts.AutoMorphicAccumulator;
import org.adamalang.runtime.contracts.DeleteTask;
import org.adamalang.runtime.json.JsonAlgebra;
import org.adamalang.runtime.natives.NtPrincipal;

import java.util.*;
import java.util.concurrent.Executor;

/**
 * provides a canonical "in-memory" service for backing Adama. Beyond providing a simple way to
 * benchmark the stack above Adama, this should be a super fast version.
 */
public class InMemoryDataService implements DataService {

  private final HashMap<Key, InMemoryDocument> datum;
  private final TimeSource time;
  private final Executor executor;

  public InMemoryDataService(Executor executor, TimeSource time) {
    this.datum = new HashMap<>();
    this.executor = executor;
    this.time = time;
  }

  @Override
  public void get(Key key, Callback<LocalDocumentChange> callback) {
    executor.execute(() -> {
      InMemoryDocument document = datum.get(key);
      int reads = 0;
      if (document == null) {
        callback.failure(new ErrorCodeException(ErrorCodes.UNIVERSAL_LOOKUP_FAILED));
        return;
      }
      AutoMorphicAccumulator<String> merge = JsonAlgebra.mergeAccumulator();
      for (RemoteDocumentUpdate update : document.updates) {
        merge.next(update.redo);
        reads++;
      }
      callback.success(new LocalDocumentChange(merge.finish(), reads, document.seq));
    });
  }

  @Override
  public void initialize(Key key, RemoteDocumentUpdate patch, Callback<Void> callback) {
    executor.execute(() -> {
      if (datum.containsKey(key)) {
        callback.failure(new ErrorCodeException(ErrorCodes.UNIVERSAL_INITIALIZE_FAILURE));
        return;
      }
      InMemoryDocument document = new InMemoryDocument();
      document.seq = patch.seqEnd;
      document.updates.add(patch);
      datum.put(key, document);
      callback.success(null);
    });
  }

  @Override
  public void patch(Key key, RemoteDocumentUpdate[] patches, Callback<Void> callback) {
    executor.execute(() -> {
      InMemoryDocument document = datum.get(key);
      if (document == null) {
        callback.failure(new ErrorCodeException(ErrorCodes.INMEMORY_DATA_PATCH_CANT_FIND_DOCUMENT));
        return;
      }
      if (patches[0].seqBegin != document.seq + 1) {
        callback.failure(new ErrorCodeException(ErrorCodes.UNIVERSAL_PATCH_FAILURE_HEAD_SEQ_OFF));
        return;
      }
      document.seq = patches[patches.length - 1].seqEnd;
      Collections.addAll(document.updates, patches);
      if (patches[patches.length - 1].requiresFutureInvalidation) {
        document.active = true;
        document.timeToWake = patches[patches.length - 1].whenToInvalidateMilliseconds + time.nowMilliseconds();
      } else {
        document.active = false;
        document.timeToWake = 0L;
      }
      callback.success(null);
    });
  }

  @Override
  public void compute(Key key, ComputeMethod method, int seq, Callback<LocalDocumentChange> callback) {
    executor.execute(() -> {
      InMemoryDocument document = datum.get(key);
      if (document == null) {
        callback.failure(new ErrorCodeException(ErrorCodes.INMEMORY_DATA_COMPUTE_CANT_FIND_DOCUMENT));
        return;
      }
      if (method == ComputeMethod.HeadPatch) {
        AutoMorphicAccumulator<String> redo = JsonAlgebra.mergeAccumulator();
        int reads = 0;
        // get items in order
        for (RemoteDocumentUpdate update : document.updates) {
          if (update.seqBegin > seq) {
            redo.next(update.redo);
            reads++;
          }
        }
        if (redo.empty()) {
          callback.failure(new ErrorCodeException(ErrorCodes.INMEMORY_DATA_COMPUTE_PATCH_NOTHING_TODO));
          return;
        }
        callback.success(new LocalDocumentChange(redo.finish(), reads, document.seq));
        return;
      }
      if (method == ComputeMethod.Rewind) {
        Stack<RemoteDocumentUpdate> toUndo = new Stack<>();
        int reads = 0;
        // get items in order
        for (RemoteDocumentUpdate update : document.updates) {
          if (update.seqBegin >= seq) {
            toUndo.push(update);
            reads++;
          }
        }
        // walk them backwards to build appropriate undo
        AutoMorphicAccumulator<String> undo = JsonAlgebra.mergeAccumulator();
        while (!toUndo.empty()) {
          undo.next(toUndo.pop().undo);
        }
        if (undo.empty()) {
          callback.failure(new ErrorCodeException(ErrorCodes.INMEMORY_DATA_COMPUTE_REWIND_NOTHING_TODO));
          return;
        }
        callback.success(new LocalDocumentChange(undo.finish(), reads, document.seq));
        return;
      }

      callback.failure(new ErrorCodeException(ErrorCodes.INMEMORY_DATA_COMPUTE_INVALID_METHOD));
    });
  }

  @Override
  public void delete(Key key, DeleteTask task, Callback<Void> callback) {
    executor.execute(() -> {
      InMemoryDocument document = datum.remove(key);
      if (document == null) {
        callback.failure(new ErrorCodeException(ErrorCodes.INMEMORY_DATA_DELETE_CANT_FIND_DOCUMENT));
      } else {
        task.executeAfterMark(callback);
      }
    });
  }

  @Override
  public void snapshot(Key key, DocumentSnapshot snapshot, Callback<Integer> callback) {
    executor.execute(() -> {
      InMemoryDocument document = datum.get(key);
      if (document != null) {
        callback.success(document.compact(snapshot.history));
      } else {
        callback.failure(new ErrorCodeException(ErrorCodes.INMEMORY_DATA_COMPACT_CANT_FIND_DOCUMENT));
      }
    });
  }

  @Override
  public void shed(Key key) {
    close(key, Callback.DONT_CARE_VOID);
  }

  @Override
  public void inventory(Callback<Set<Key>> callback) {
    executor.execute(() -> callback.success(new TreeSet<>(datum.keySet())));
  }

  @Override
  public void close(Key key, Callback<Void> callback) {
    executor.execute(() -> {
      InMemoryDocument document = datum.remove(key);
      callback.success(null);
    });
  }

  @Override
  public void recover(Key key, DocumentRestore restore, Callback<Void> callback) {
    executor.execute(() -> {
      InMemoryDocument document = datum.get(key);
      if (document == null) {
        callback.failure(new ErrorCodeException(ErrorCodes.INMEMORY_DATA_RESTORE_CANT_FIND_DOCUMENT));
        return;
      }
      document.updates.clear();
      document.seq = restore.seq;
      document.updates.add(new RemoteDocumentUpdate(restore.seq, restore.seq, restore.who, "{\"restore\":" + restore.seq + "}", restore.document, "{}", true, 0, 0, UpdateType.Restore));
      callback.success(null);
    });
  }

  private static class InMemoryDocument {
    private final ArrayList<RemoteDocumentUpdate> updates;
    private boolean active;
    private long timeToWake;
    private int seq;

    public InMemoryDocument() {
      this.updates = new ArrayList<>();
      this.active = false;
      this.timeToWake = 0;
      this.seq = 0;
    }

    public int compact(int history) {
      int toCompact = updates.size() - history;
      if (toCompact > 1) {
        AutoMorphicAccumulator<String> mergeRedo = JsonAlgebra.mergeAccumulator();
        AutoMorphicAccumulator<String> mergeUndo = JsonAlgebra.mergeAccumulator();
        Stack<String> undo = new Stack<>();
        long assetBytes = 0;
        for (int k = 0; k < toCompact; k++) {
          RemoteDocumentUpdate update = updates.remove(0);
          assetBytes += update.assetBytes;
          mergeRedo.next(update.redo);
          undo.push(update.undo);
        }
        while (!undo.empty()) {
          mergeUndo.next(undo.pop());
        }
        RemoteDocumentUpdate newHead = new RemoteDocumentUpdate(0, 0, NtPrincipal.NO_ONE, "{}", mergeRedo.finish(), mergeUndo.finish(), false, 0, assetBytes, UpdateType.CompactedResult);
        updates.add(0, newHead);
        return toCompact - 1;
      }
      return 0;
    }
  }
}
