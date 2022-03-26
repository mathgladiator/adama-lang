package org.adamalang.caravan;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.adamalang.ErrorCodes;
import org.adamalang.caravan.contracts.TranslateKeyService;
import org.adamalang.caravan.data.DurableListStore;
import org.adamalang.caravan.events.EventCodec;
import org.adamalang.caravan.events.Events;
import org.adamalang.caravan.events.LocalCache;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.NamedRunnable;
import org.adamalang.common.SimpleExecutor;
import org.adamalang.runtime.data.*;

import java.util.HashMap;
import java.util.concurrent.CountDownLatch;
import java.util.function.BiConsumer;

public class CaravanDataService implements DataService {
  private final TranslateKeyService translate;
  private final DurableListStore store;
  private final SimpleExecutor executor;
  private final HashMap<Long, LocalCache> cache;

  public CaravanDataService(TranslateKeyService translate, DurableListStore store, SimpleExecutor executor) {
    this.translate = translate;
    this.store = store;
    this.executor = executor;
    this.cache = new HashMap();
  }

  /** execute with the translation service and jump into the executor */
  private <T> void execute(String name, Key key, Callback<T> callback, BiConsumer<Long, LocalCache> action) {
    translate.lookup(key, new Callback<Long>() {
      @Override
      public void success(Long id) {
        executor.execute(new NamedRunnable(name) {
          @Override
          public void execute() throws Exception {
            action.accept(id, cache.get(id));
          }
        });
      }

      @Override
      public void failure(ErrorCodeException ex) {
        callback.failure(ex);
      }
    });
  }

  private static byte[] convert(ByteBuf buf) {
    byte[] memory = new byte[buf.writerIndex()];
    while (buf.isReadable()) {
      buf.readBytes(memory, buf.readerIndex(), memory.length - buf.readerIndex());
    }
    return memory;
  }

  public void close(Key key, Callback<Void> callback) {
    execute("get", key, callback, (id, cached) -> {
      if (cached != null) {
        cache.remove(id);
      }
      callback.success(null);
    });
  }

  @Override
  public void get(Key key, Callback<LocalDocumentChange> callback) {
    execute("get", key, callback, (id, cached) -> {
      if (cached != null) {
        callback.success(cached.build());
        return;
      }
      LocalCache builder = new LocalCache() {
        @Override
        public void finished() {
          LocalCache builder = this;
          LocalDocumentChange result = builder.build();
          if (result == null) {
            callback.failure(new ErrorCodeException(ErrorCodes.UNIVERSAL_LOOKUP_FAILED));
            return;
          }
          executor.execute(new NamedRunnable("commit-cache") {
            @Override
            public void execute() throws Exception {
              cache.put(id, builder);
              callback.success(result);
            }
          });
        }
      };
      store.read(id, builder);
    });
  }

  @Override
  public void initialize(Key key, RemoteDocumentUpdate patch, Callback<Void> callback) {
    Events.Change change = new Events.Change();
    change.copyFrom(patch);
    ByteBuf buf = Unpooled.buffer();
    EventCodec.write(buf, change);

    execute("initialize", key, callback, (id, cached) -> {
      if (cached != null || store.exists(id)) {
        callback.failure(new ErrorCodeException(ErrorCodes.UNIVERSAL_INITIALIZE_FAILURE));
        return;
      }
      LocalCache builder = new LocalCache() {
        @Override
        public void finished() {

        }
      };
      builder.handle(change);
      builder.bump();
      if (store.append(id, convert(buf), () -> {
        executor.execute(new NamedRunnable("commit-cache") {
          @Override
          public void execute() throws Exception {
            cache.put(id, builder);
            callback.success(null);
          }
        });
      }) == null) {
        callback.failure(new ErrorCodeException(ErrorCodes.CARAVAN_OUT_OF_SPACE_INITIALIZE));
      }
    });
  }

  @Override
  public void patch(Key key, RemoteDocumentUpdate[] patches, Callback<Void> callback) {
    Events.Batch batch = new Events.Batch();
    batch.changes = new Events.Change[patches.length];
    for (int k = 0; k < patches.length; k++) {
      batch.changes[k] = new Events.Change();
      batch.changes[k].copyFrom(patches[k]);
    }
    ByteBuf buf = Unpooled.buffer();
    EventCodec.write(buf, batch);
    byte[] write = convert(buf);
    execute("patch", key, callback, (id, cached) -> {
      if (cached == null) {
        callback.failure(new ErrorCodeException(ErrorCodes.CARAVAN_KEY_NOT_LOADED_PATCH));
        return;
      }
      if (!cached.check(patches[0].seqBegin)) {
        callback.failure(new ErrorCodeException(ErrorCodes.UNIVERSAL_PATCH_FAILURE_HEAD_SEQ_OFF));
        return;
      }
      if (store.append(id, write, () -> {
        executor.execute(new NamedRunnable("patch-commit") {
          @Override
          public void execute() throws Exception {
            cached.handle(batch);
            cached.bump();
            callback.success(null);
          }
        });
      }) == null) {
        callback.failure(new ErrorCodeException(ErrorCodes.CARAVAN_OUT_OF_SPACE_PATCH));
      }
    });
  }

  @Override
  public void compute(Key key, ComputeMethod method, int seq, Callback<LocalDocumentChange> callback) {
    execute("compute", key, callback, (id, cached) -> {
      if (cached == null) {
        callback.failure(new ErrorCodeException(ErrorCodes.CARAVAN_KEY_NOT_LOADED_COMPUTE));
        return;
      }
      if (method == ComputeMethod.HeadPatch) {
        String result = cached.computeHeadPatch(seq);
        if (result != null) {
          callback.success(new LocalDocumentChange(result, 1));
        } else {
          callback.failure(new ErrorCodeException(ErrorCodes.CARAVAN_COMPUTE_HEADPATCH_SEQ_NOT_FOUND));
        }
      } else if (method == ComputeMethod.Rewind) {
        String result = cached.computeRewind(seq);
        if (result != null) {
          callback.success(new LocalDocumentChange(result, 1));
        } else {
          callback.failure(new ErrorCodeException(ErrorCodes.CARAVAN_COMPUTE_REWIND_SEQ_NOT_FOUND));
        }
      } else {
        callback.failure(new ErrorCodeException(ErrorCodes.CARAVAN_COMPUTE_METHOD_NOT_FOUND));
      }
    });
  }

  @Override
  public void delete(Key key, Callback<Void> callback) {
    execute("delete", key, callback, (id, cached) -> {
      store.delete(id, () -> {});
      callback.success(null);
      cache.remove(id);
    });
  }

  @Override
  public void compactAndSnapshot(Key key, int seq, String snapshot, int history, Callback<Integer> callback) {
    if (history <= 0) {
      callback.failure(new ErrorCodeException(ErrorCodes.CARAVAN_UNABLE_TO_COMPACT_NON_POSITIVE_HISTORY));
      return;
    }
    Events.Snapshot snap = new Events.Snapshot();
    snap.seq = seq;
    snap.document = snapshot;
    snap.history = history;
    ByteBuf buf = Unpooled.buffer();
    EventCodec.write(buf, snap);
    byte[] bytes = convert(buf);

    execute("snapshot", key, callback, (id, cached) -> {
      if (cached == null) {
        callback.failure(new ErrorCodeException(ErrorCodes.CARAVAN_KEY_NOT_LOADED_SNAPSHOT));
        return;
      }
      Integer size = store.append(id, bytes, () -> {
        callback.success(0);// huh, this is interesting
      });
      if (size == null) {
        callback.failure(new ErrorCodeException(ErrorCodes.CARAVAN_OUT_OF_SPACE_SNAPSHOT));
      } else {
        cached.handle(snap);
        cached.bump();
        int toTrim = Math.min(size - 1, cached.reset()) - history;
        if (toTrim > 0) {
          store.trim(id, toTrim, () -> {});
        }
      }
    });
  }

  public CountDownLatch flush(boolean force) {
    CountDownLatch latch = new CountDownLatch(1);
    executor.execute(new NamedRunnable("flush") {
      @Override
      public void execute() throws Exception {
        store.flush(force);
        latch.countDown();
      }
    });
    return latch;
  }
}
