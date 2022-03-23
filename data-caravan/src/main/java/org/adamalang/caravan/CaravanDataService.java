package org.adamalang.caravan;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.adamalang.ErrorCodes;
import org.adamalang.caravan.contracts.TranslateKeyService;
import org.adamalang.caravan.data.DurableListStore;
import org.adamalang.caravan.events.EventCodec;
import org.adamalang.caravan.events.Events;
import org.adamalang.caravan.events.LocalCacheBuilder;
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
      LocalCacheBuilder builder = new LocalCacheBuilder();
      store.read(id, builder);
      callback.success(builder.build());
    });
  }

  private static byte[] convert(ByteBuf buf) {
    byte[] memory = new byte[buf.writerIndex()];
    while (buf.isReadable()) {
      buf.readBytes(memory, buf.readerIndex(), memory.length - buf.readerIndex());
    }
    return memory;
  }

  @Override
  public void initialize(Key key, RemoteDocumentUpdate patch, Callback<Void> callback) {
    execute("initialize", key, callback, (id) -> {
      if (store.exists(id)) {
        callback.failure(new ErrorCodeException(ErrorCodes.UNIVERSAL_INITIALIZE_FAILURE));
        return;
      }
      Events.Change change = new Events.Change();
      change.copyFrom(patch);
      ByteBuf buf = Unpooled.buffer();
      EventCodec.write(buf, change);
      if (!store.append(id, convert(buf), () -> {
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
      Events.Batch batch = new Events.Batch();
      batch.changes = new Events.Change[patches.length];
      for (int k = 0; k < patches.length; k++) {
        batch.changes[k] = new Events.Change();
        batch.changes[k].copyFrom(patches[k]);
      }
      ByteBuf buf = Unpooled.buffer();
      EventCodec.write(buf, batch);
      if (!store.append(id, convert(buf), () -> {
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
      if (!store.delete(id, () -> {
        callback.success(null);
      })) {
        // failed to delete because it didn't exist, we let it slide to success
        callback.success(null);
      }
    });
  }

  @Override
  public void compactAndSnapshot(Key key, int seq, String snapshot, int history, Callback<Integer> callback) {
    execute("snapshot", key, callback, (id) -> {
      Events.Snapshot snap = new Events.Snapshot();
      snap.seq = seq;
      snap.document = snapshot;
      snap.history = history;
      ByteBuf buf = Unpooled.buffer();
      EventCodec.write(buf, snap);
      if (!store.append(id, convert(buf), () -> {
        callback.success(0);// huh, this is interesting
      })) {
        callback.failure(new ErrorCodeException(-1));
      }
    });
  }

  public void flush() {
    executor.execute(new NamedRunnable("flush") {
      @Override
      public void execute() throws Exception {
        store.flush(false);
      }
    });
  }
}
