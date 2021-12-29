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
import org.adamalang.common.TimeSource;
import org.adamalang.runtime.contracts.*;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.runtime.json.JsonAlgebra;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.Executor;

/** provides a canonical "in-memory" service for backing Adama. Beyond providing a simple way to benchmark the stack above Adama, this should be a super fast version. */
public class InMemoryDataService implements DataService {

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
    }

    private final HashMap<Key, InMemoryDocument> datum;
    private final TimeSource time;
    private final Executor executor;

    public InMemoryDataService(Executor executor, TimeSource time) {
        this.datum = new HashMap<>();
        this.executor = executor;
        this.time = time;
    }

    @Override
    public void scan(ActiveKeyStream stream) {
        executor.execute(() -> {
            for (Map.Entry<Key, InMemoryDocument> entry : datum.entrySet()) {
                if (entry.getValue().active) {
                    long timeToWake = time.nowMilliseconds() - entry.getValue().timeToWake;
                    if (timeToWake < 0) {
                        timeToWake = 0;
                    }
                    stream.schedule(entry.getKey(), timeToWake);
                }
            }
            stream.finish();
        });
    }

    @Override
    public void get(Key key, Callback<LocalDocumentChange> callback) {
        executor.execute(() -> {
            InMemoryDocument document = datum.get(key);
            if (document == null) {
                callback.failure(new ErrorCodeException(ErrorCodes.INMEMORY_DATA_GET_CANT_FIND_DOCUMENT));
                return;
            }
            AutoMorphicAccumulator<String> merge = JsonAlgebra.mergeAccumulator();
            for (RemoteDocumentUpdate update : document.updates) {
                merge.next(update.redo);
            }
            callback.success(new LocalDocumentChange(merge.finish()));
        });
    }

    @Override
    public void initialize(Key key, RemoteDocumentUpdate patch, Callback<Void> callback) {
        executor.execute(() -> {
            if (datum.containsKey(key)) {
                callback.failure(new ErrorCodeException(ErrorCodes.INMEMORY_DATA_INITIALIZED_UNABLE_ALREADY_EXISTS));
                return;
            }
            InMemoryDocument document = new InMemoryDocument();
            document.seq = patch.seq;
            document.updates.add(patch);
            datum.put(key, document);
            callback.success(null);
        });
    }

    @Override
    public void patch(Key key, RemoteDocumentUpdate patch, Callback<Void> callback) {
        executor.execute(() -> {
            InMemoryDocument document = datum.get(key);
            if (document == null) {
                callback.failure(new ErrorCodeException(ErrorCodes.INMEMORY_DATA_PATCH_CANT_FIND_DOCUMENT));
                return;
            }
            if (patch.seq != document.seq + 1) {
                callback.failure(new ErrorCodeException(ErrorCodes.UNIVERSAL_PATCH_FAILURE_HEAD_SEQ_OFF));
                return;
            }
            document.seq = patch.seq;
            document.updates.add(patch);
            if (patch.requiresFutureInvalidation) {
                document.active = true;
                document.timeToWake = patch.whenToInvalidateMilliseconds + time.nowMilliseconds();
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
            if (method == ComputeMethod.Patch) {
                AutoMorphicAccumulator<String> redo = JsonAlgebra.mergeAccumulator();
                // get items in order
                for (RemoteDocumentUpdate update : document.updates) {
                    if (update.seq > seq) {
                        redo.next(update.redo);
                    }
                }
                if (redo.empty()) {
                    callback.failure(new ErrorCodeException(ErrorCodes.INMEMORY_DATA_COMPUTE_PATCH_NOTHING_TODO));
                    return;
                }
                callback.success(new LocalDocumentChange(redo.finish()));
                return;
            }
            if (method == ComputeMethod.Rewind) {
                Stack<RemoteDocumentUpdate> toUndo = new Stack<>();
                // get items in order
                for (RemoteDocumentUpdate update : document.updates) {
                    if (update.seq >= seq) {
                        toUndo.push(update);
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
                callback.success(new LocalDocumentChange(undo.finish()));
                return;
            }
            if (method == ComputeMethod.Unsend) {
                RemoteDocumentUpdate start = null;
                ArrayList<RemoteDocumentUpdate> redos = new ArrayList<>();
                // get items in order
                for (RemoteDocumentUpdate update : document.updates) {
                    if (update.seq == seq) {
                        start = update;
                    } else if (update.seq > seq) {
                        redos.add(update);
                    }
                }

                if (start == null) {
                    callback.failure(new ErrorCodeException(ErrorCodes.INMEMORY_DATA_COMPUTE_UNSEND_FAILED_TO_FIND));
                    return;
                }

                AutoMorphicAccumulator<String> unsend = JsonAlgebra.rollUndoForwardAccumulator(start.undo);
                for (RemoteDocumentUpdate redo : redos) {
                    unsend.next(redo.redo);
                }
                callback.success(new LocalDocumentChange(unsend.finish()));
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
}
