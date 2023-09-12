/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.caravan;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.adamalang.ErrorCodes;
import org.adamalang.caravan.contracts.ByteArrayStream;
import org.adamalang.caravan.contracts.Cloud;
import org.adamalang.caravan.data.DurableListStore;
import org.adamalang.caravan.events.*;
import org.adamalang.common.*;
import org.adamalang.runtime.contracts.DeleteTask;
import org.adamalang.runtime.data.*;
import org.adamalang.runtime.json.JsonStreamWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

public class CaravanDataService implements ArchivingDataService {
  private final Logger LOGGER = LoggerFactory.getLogger(CaravanDataService.class);
  private final CaravanMetrics metrics;
  private final Cloud cloud;
  private final DurableListStore store;
  private final SimpleExecutor executor;
  private final HashMap<Key, LocalCache> cache;

  public CaravanDataService(CaravanMetrics metrics, Cloud cloud, DurableListStore store, SimpleExecutor executor) {
    this.metrics = metrics;
    this.cloud = cloud;
    this.store = store;
    this.executor = executor;
    this.cache = new HashMap<>();
    scheduleReport(0);
  }

  private void scheduleReport(int offset) {
    executor.schedule(new NamedRunnable("disk-report") {
      @Override
      public void execute() throws Exception {
        store.report();
        scheduleReport((int) (30000 + 60000 * Math.random()));
      }
    }, offset);
  }

  @Override
  public void cleanUp(Key key, String archiveKey) {
    cloud.delete(key, archiveKey, Callback.DONT_CARE_VOID);
    File local = new File(new File(cloud.path(), key.space), archiveKey);
    if (local.exists()) {
      local.delete();
    }
  }

  @Override
  public void restore(Key key, String archiveKey, Callback<Void> callback) {
    // ask the cloud to ensure the archive key has been downloaded
    cloud.restore(key, archiveKey, new Callback<>() {
      @Override
      public void success(File archiveFile) {
        // load the writes from the backup
        final ArrayList<byte[]> writes;
        try {
          writes = RestoreLoader.load(archiveFile);
        } catch (Exception ex) {
          callback.failure(new ErrorCodeException(ErrorCodes.CARAVAN_CANT_RESTORE_EXCEPTION, ex));
          return;
        }

        // jump into the exector for the cache
        execute("restore", key, false, callback, (cached) -> {
          if (cached == null) {
            // the cache does not exist, so let's attempt to create
            LocalCache newBuilderToCache = new LocalCache() {
              @Override
              public void finished() { // Note: runs in the thread calling execute since store.read is sync
                // aftering reading into the cache, let's merge what we have restored
                LocalCache builder = this;
                mergeRestore(key, builder, writes, new Callback<Void>() {
                  @Override
                  public void success(Void value) { // note; this callback runs in the executor
                    addToCacheIfDoesntExistReturnCorrect(key, builder);
                    callback.success(null);
                  }

                  @Override
                  public void failure(ErrorCodeException ex) {
                    callback.failure(ex);
                  }
                });
              }
            };
            try {
              store.read(key, newBuilderToCache);
            } catch (Exception ex) {
              LOGGER.error("failed-restore", ex);
              callback.failure(new ErrorCodeException(ErrorCodes.CARAVAN_CANT_RESTORE_CANT_READ, ex));
              return;
            }
          } else {
            mergeRestore(key, cached, writes, callback);
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
  public void backup(Key key, Callback<BackupResult> callback) {
    String archiveKey = ProtectedUUID.generate() + "-" + System.currentTimeMillis();
    execute("backup", key, true, callback, (cached) -> {
      int seq = cached.seq();
      AtomicLong deltaBytesSum = new AtomicLong(0);
      AssetByteAccountant assetByteAccountant = new AssetByteAccountant();
      File root = new File(cloud.path(), key.space);
      if (!root.exists()) {
        root.mkdir();
      }
      File tempOutput = new File(root, archiveKey + ".temp");
      File finalOutput = new File(root, archiveKey);
      try {
        DataOutputStream output = new DataOutputStream(new FileOutputStream(tempOutput));
        store.read(key, new ByteArrayStream() {
          @Override
          public void next(int appendIndex, byte[] value, int seq, long assetBytes) throws Exception {
            deltaBytesSum.addAndGet(value.length);
            assetByteAccountant.account(value, assetBytes);
            output.writeBoolean(true);
            output.writeInt(value.length);
            output.write(value);
          }

          @Override
          public void finished() throws Exception {
            output.writeBoolean(false);
          }
        });
        output.flush();
        output.close();
        Files.move(tempOutput.toPath(), finalOutput.toPath(), StandardCopyOption.ATOMIC_MOVE);
        if (assetByteAccountant.hasThereBeenDataloss()) {
          LOGGER.error("detected data loss during a backup:" + key.space + "/" + key.key + "->" + archiveKey);
          metrics.caravan_datalog_loss.up();
        }
      } catch (Exception ioex) {
        LOGGER.error("failed-backup", ioex);
        callback.failure(new ErrorCodeException(ErrorCodes.CARAVAN_CANT_BACKUP_EXCEPTION, ioex));
        return;
      }
      cloud.backup(key, finalOutput, new Callback<Void>() {
        @Override
        public void success(Void value) {
          callback.success(new BackupResult(archiveKey, seq, deltaBytesSum.get(), assetByteAccountant.getBytes()));
        }

        @Override
        public void failure(ErrorCodeException ex) {
          callback.failure(ex);
        }
      });
    });
  }

  private LocalCache addToCacheIfDoesntExistReturnCorrect(Key key, LocalCache created) {
    LocalCache prior = cache.putIfAbsent(key, created);
    if (prior != null) {
      metrics.caravan_waste.run();
      return prior;
    } else {
      return created;
    }
  }

  public void diagnostics(Callback<String> callback) {
    executor.execute(new NamedRunnable("diagnostics") {
      @Override
      public void execute() throws Exception {
        JsonStreamWriter writer = new JsonStreamWriter();
        writer.beginObject();
        {
          writer.writeObjectFieldIntro("cache");
          writer.beginObject();
          for (Map.Entry<Key, LocalCache> entry : cache.entrySet()) {
            writer.writeObjectFieldIntro(entry.getKey().space + "/" + entry.getKey().key);
            writer.beginObject();
            writer.writeObjectFieldIntro("seq");
            writer.writeInteger(entry.getValue().seq());
            writer.endObject();
          }
          writer.endObject();
        }
        {
          writer.writeObjectFieldIntro("working-set");
          writer.beginObject();
          writer.writeObjectFieldIntro("index");
          writer.beginArray();
          for (long id : store.listIndex()) {
            writer.writeLong(id);
          }
          writer.endArray();
          writer.writeObjectFieldIntro("keys");
          writer.beginObject();
          for (Map.Entry<Key, Integer> et : store.map().entrySet()) {
            writer.writeObjectFieldIntro(et.getKey().space + "/" + et.getKey().key);
            writer.writeInteger(et.getValue());
          }
          writer.endObject();
          writer.endObject();
        }
        writer.endObject();
        callback.success(writer.toString());
      }
    });
  }


  /** execute with the translation service and jump into the executor */
  private <T> void execute(String name, Key key, boolean load, Callback<T> callback, Consumer<LocalCache> action) {
    executor.execute(new NamedRunnable(name) {
      @Override
      public void execute() throws Exception {
        LocalCache cached = cache.get(key);
        if (load && cached == null) {
          load(key, new Callback<>() {
            @Override
            public void success(LocalCache cached) {
              executor.execute(new NamedRunnable(name, "load") {
                @Override
                public void execute() throws Exception {
                  action.accept(addToCacheIfDoesntExistReturnCorrect(key, cached));
                }
              });
            }

            @Override
            public void failure(ErrorCodeException ex) {
              LOGGER.error("caravan-failure-load:" + ex.code + "/msg=" + ex.getMessage());
              callback.failure(ex);
            }
          });
        } else {
          action.accept(cached);
        }
      }
    });
  }

  private void load(Key key, Callback<LocalCache> callback) {
    try {
      LocalCache builder = new LocalCache() {
        @Override
        public void finished() {
          LocalCache builder = this;
          LocalDocumentChange result = builder.build();
          if (result == null) {
            callback.failure(new ErrorCodeException(ErrorCodes.UNIVERSAL_LOOKUP_FAILED, "failed:" + key.space + "/" + key.key));
            return;
          }
          executor.execute(new NamedRunnable("load-jump-callback") {
            @Override
            public void execute() throws Exception {
              callback.success(builder);
            }
          });
        }
      };
      store.read(key, builder);
    } catch (Exception ex) {
      LOGGER.error("failed-load-" + key.space + "/" + key.key, ex);
      callback.failure(new ErrorCodeException(ErrorCodes.CARAVAN_LOAD_FAILURE_EXCEPTION, ex));
    }
  }

  private void mergeRestore(Key key, LocalCache cached, ArrayList<byte[]> writes, Callback<Void> callback) {
    ArrayList<byte[]> filtered = cached.filter(writes);
    if (filtered.size() == 0) {
      callback.success(null);
      return;
    }
    RestoreWalker walker = new RestoreWalker();
    for (byte[] toAppend : filtered) {
      EventCodec.route(Unpooled.wrappedBuffer(toAppend), walker);
    }
    if (store.append(key, filtered, walker.seq, walker.assetBytes, () -> {
      executor.execute(new NamedRunnable("restore-write-map") {
        @Override
        public void execute() throws Exception {
          for (byte[] write : filtered) {
            EventCodec.route(Unpooled.wrappedBuffer(write), cached);
          }
          callback.success(null);
        }
      });
    }) == null) {
      callback.failure(new ErrorCodeException(ErrorCodes.CARAVAN_CANT_MERGE_RESTORE_OUT_OF_SPACE));
    }
  }

  public void dumpLog(Key key, Stream<String> stream) {
    executor.execute(new NamedRunnable("dump-log") {
      @Override
      public void execute() throws Exception {
        store.read(key, new ByteArrayStream() {
          @Override
          public void next(int appendIndex, byte[] value, int seq, long assetBytes) throws Exception {
            EventCodec.route(Unpooled.wrappedBuffer(value), new EventCodec.HandlerEvent() {
              @Override
              public void handle(Events.Snapshot payload) {
                stream.next("[" + (seq == payload.seq ? seq : (seq + "/" + payload.seq)  + "] SNAPSHOT:" + payload.document + " (history=" + payload.history + ", assets=" + payload.assetBytes + ")"));
              }

              @Override
              public void handle(Events.Batch payload) {
                if (payload.changes.length > 1) {
                  stream.next("BATCH:" + payload.changes.length);
                }
                for (Events.Change change : payload.changes) {
                  handle(change);
                }
              }

              @Override
              public void handle(Events.Change payload) {
                stream.next("[" + (seq + ":" + payload.seq_begin + "-->" + payload.seq_end)  + "] REQUEST:"+ payload.request + " ; REDO:" + payload.redo + " ; UNDO=" + payload.undo + " (active=" + payload.active + ")");
              }
            });
          }

          @Override
          public void finished() throws Exception {
            stream.complete();
          }
        });
      }
    });
  }

  @Override
  public void get(Key key, Callback<LocalDocumentChange> callback) {
    execute("get", key, false, callback, (cached) -> {
      if (cached != null) {
        callback.success(cached.build());
        return;
      }
      load(key, new Callback<>() {
        @Override
        public void success(LocalCache builder) {
          callback.success(addToCacheIfDoesntExistReturnCorrect(key, builder).build());
        }

        @Override
        public void failure(ErrorCodeException ex) {
          callback.failure(ex);
        }
      });
    });
  }

  @Override
  public void initialize(Key key, RemoteDocumentUpdate patch, Callback<Void> callback) {
    Events.Change change = new Events.Change();
    change.copyFrom(patch);
    ByteBuf buf = Unpooled.buffer();
    EventCodec.write(buf, change);

    execute("initialize", key, false, callback, (cached) -> {
      if (cached != null || store.exists(key)) {
        callback.failure(new ErrorCodeException(ErrorCodes.UNIVERSAL_INITIALIZE_FAILURE));
        return;
      }
      LocalCache builder = new LocalCache() {
        @Override
        public void finished() {

        }
      };
      builder.handle(change);
      if (store.append(key, ByteArrayHelper.convert(buf), patch.seqEnd, patch.assetBytes, () -> {
        executor.execute(new NamedRunnable("commit-cache") {
          @Override
          public void execute() throws Exception {
            addToCacheIfDoesntExistReturnCorrect(key, builder);
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
    final Events.Batch batch = new Events.Batch();
    final byte[] write;
    final long assetBytes;
    {
      batch.changes = new Events.Change[patches.length];
      long assetBytesSum = 0;
      for (int k = 0; k < patches.length; k++) {
        batch.changes[k] = new Events.Change();
        batch.changes[k].copyFrom(patches[k]);
        assetBytesSum += patches[k].assetBytes;
      }
      assetBytes = assetBytesSum;
      ByteBuf buf = Unpooled.buffer();
      EventCodec.write(buf, batch);
      write = ByteArrayHelper.convert(buf);
    }
    execute("patch", key, true, callback, (cached) -> {
      if (!cached.check(patches[0].seqBegin)) {
        metrics.caravan_seq_off.run();
        callback.failure(new ErrorCodeException(ErrorCodes.UNIVERSAL_PATCH_FAILURE_HEAD_SEQ_OFF));
        return;
      }
      if (store.append(key, write, patches[patches.length - 1].seqEnd, assetBytes, () -> {
        executor.execute(new NamedRunnable("patch-commit") {
          @Override
          public void execute() throws Exception {
            cached.handle(batch);
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
    execute("compute", key, true, callback, (cached) -> {
      if (method == ComputeMethod.HeadPatch) {
        String result = cached.computeHeadPatch(seq);
        if (result != null) {
          callback.success(new LocalDocumentChange(result, 1, cached.seq()));
        } else {
          callback.failure(new ErrorCodeException(ErrorCodes.CARAVAN_COMPUTE_HEADPATCH_SEQ_NOT_FOUND));
        }
      } else if (method == ComputeMethod.Rewind) {
        String result = cached.computeRewind(seq);
        if (result != null) {
          callback.success(new LocalDocumentChange(result, 1, cached.seq()));
        } else {
          callback.failure(new ErrorCodeException(ErrorCodes.CARAVAN_COMPUTE_REWIND_SEQ_NOT_FOUND));
        }
      } else {
        callback.failure(new ErrorCodeException(ErrorCodes.CARAVAN_COMPUTE_METHOD_NOT_FOUND));
      }
    });
  }

  @Override
  public void delete(Key key, DeleteTask task, Callback<Void> callback) {
    execute("delete", key, false, callback, (cached) -> {
      store.delete(key, () -> {
      });
      cache.remove(key);
      task.executeAfterMark(callback);
    });
  }

  @Override
  public void snapshot(Key key, DocumentSnapshot snapshot, Callback<Integer> callback) {
    if (snapshot.history <= 0) {
      callback.failure(new ErrorCodeException(ErrorCodes.CARAVAN_UNABLE_TO_COMPACT_NON_POSITIVE_HISTORY));
      return;
    }
    Events.Snapshot snap = new Events.Snapshot();
    snap.seq = snapshot.seq;
    snap.document = snapshot.json;
    snap.history = snapshot.history;
    snap.assetBytes = snapshot.assetBytes;
    ByteBuf buf = Unpooled.buffer();
    EventCodec.write(buf, snap);
    byte[] bytes = ByteArrayHelper.convert(buf);

    execute("snapshot", key, true, callback, (cached) -> {
      Integer size = store.append(key, bytes, snapshot.seq, snapshot.assetBytes, () -> {
      });
      if (size == null) {
        callback.failure(new ErrorCodeException(ErrorCodes.CARAVAN_OUT_OF_SPACE_SNAPSHOT));
      } else {
        cached.handle(snap);
        int toPreserve = Math.max(snapshot.history, cached.getMinimumHistoryToPreserve());
        store.trim(key, toPreserve, () -> {
          callback.success(0);// huh, this is interesting
        });
      }
    });
  }

  @Override
  public void shed(Key key) {
    close(key, Callback.DONT_CARE_VOID);
  }

  @Override
  public void close(Key key, Callback<Void> callback) {
    execute("close", key, false, callback, (cached) -> {
      if (cached != null) {
        cache.remove(key);
      }
      callback.success(null);
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

  public CountDownLatch shutdown() {
    CountDownLatch latch = new CountDownLatch(1);
    executor.execute(new NamedRunnable("flush") {
      @Override
      public void execute() throws Exception {
        store.flush(true);
        store.shutdown();
        latch.countDown();
      }
    });
    return latch;
  }
}
