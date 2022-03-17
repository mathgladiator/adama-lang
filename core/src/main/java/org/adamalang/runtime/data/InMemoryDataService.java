/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.runtime.data;

import org.adamalang.ErrorCodes;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.TimeSource;
import org.adamalang.runtime.contracts.AutoMorphicAccumulator;
import org.adamalang.runtime.json.JsonAlgebra;
import org.adamalang.runtime.natives.NtClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;
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
      callback.success(new LocalDocumentChange(merge.finish(), reads));
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
      for (RemoteDocumentUpdate patch : patches) {
        document.updates.add(patch);
      }
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
        callback.success(new LocalDocumentChange(redo.finish(), reads));
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
        callback.success(new LocalDocumentChange(undo.finish(), reads));
        return;
      }

      callback.failure(new ErrorCodeException(ErrorCodes.INMEMORY_DATA_COMPUTE_INVALID_METHOD));
    });
  }

  @Override
  public void delete(Key key, Callback<Void> callback) {
    executor.execute(() -> {
      InMemoryDocument document = datum.remove(key);
      if (document == null) {
        callback.failure(new ErrorCodeException(ErrorCodes.INMEMORY_DATA_DELETE_CANT_FIND_DOCUMENT));
        return;
      } else {
        callback.success(null);
      }
    });
  }

  @Override
  public void compactAndSnapshot(Key key, int seq, String snapshot, int history, Callback<Integer> callback) {
    executor.execute(() -> {
      InMemoryDocument document = datum.get(key);
      if (document != null) {
        callback.success(document.compact(history));
      } else {
        callback.failure(new ErrorCodeException(ErrorCodes.INMEMORY_DATA_COMPACT_CANT_FIND_DOCUMENT));
      }
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
        RemoteDocumentUpdate newHead = new RemoteDocumentUpdate(0, 0, NtClient.NO_ONE, "{}", mergeRedo.finish(), mergeUndo.finish(), false, 0, assetBytes, UpdateType.CompactedResult);
        updates.add(0, newHead);
        return toCompact - 1;
      }
      return 0;
    }
  }
}
