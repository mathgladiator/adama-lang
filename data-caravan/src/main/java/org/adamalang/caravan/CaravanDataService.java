/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.caravan;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.adamalang.ErrorCodes;
import org.adamalang.caravan.contracts.ByteArrayStream;
import org.adamalang.caravan.contracts.Cloud;
import org.adamalang.caravan.data.DurableListStore;
import org.adamalang.caravan.events.EventCodec;
import org.adamalang.caravan.events.Events;
import org.adamalang.caravan.events.LocalCache;
import org.adamalang.common.*;
import org.adamalang.runtime.data.*;

import java.io.*;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.CountDownLatch;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class CaravanDataService implements ArchivingDataService {
  private final Cloud cloud;
  private final FinderService finder;
  private final DurableListStore store;
  private final SimpleExecutor executor;
  private final HashMap<Long, LocalCache> cache;

  public CaravanDataService(Cloud cloud, FinderService finder, DurableListStore store, SimpleExecutor executor) {
    this.cloud = cloud;
    this.finder = finder;
    this.store = store;
    this.executor = executor;
    this.cache = new HashMap();
  }

  @Override
  public void backup(Key key, Callback<String> callback) {
    String archiveKey = ProtectedUUID.generate() + "-" + System.currentTimeMillis();
    execute("backup", key, true, callback, (id, cached) -> {
      File tempOutput = new File(cloud.path(), archiveKey + ".temp");
      File finalOutput = new File(cloud.path(), archiveKey + ".archive");
      try {
        DataOutputStream output = new DataOutputStream(new FileOutputStream(tempOutput));
        store.read(id, new ByteArrayStream() {
          @Override
          public void next(int appendIndex, byte[] value) throws Exception {
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
      } catch (Exception ioex) {
        callback.failure(new ErrorCodeException(ErrorCodes.CARAVAN_CANT_BACKUP_EXCEPTION, ioex));
        return;
      }
      cloud.backup(finalOutput, new Callback<Void>() {
        @Override
        public void success(Void value) {
          callback.success(archiveKey);
        }

        @Override
        public void failure(ErrorCodeException ex) {
          callback.failure(ex);
        }
      });
    });
  }

  private void mergeRestore(long id, LocalCache cached, ArrayList<byte[]> writes, Callback<Void> callback) {
    // TODO: merge the data into

  }

  @Override
  public void restore(Key key, String archiveKey, Callback<Void> callback) {
    cloud.restore(archiveKey, new Callback<File>() {
      @Override
      public void success(File archiveFile) {

        // load the writes from the backup
        ArrayList<byte[]> writes = new ArrayList<>();
        try {
          try (DataInputStream input = new DataInputStream(new FileInputStream(archiveFile))) {
            while(input.readBoolean()) {
              byte[] bytes = new byte[input.readInt()];
              input.readFully(bytes);
              writes.add(bytes);
            }
          }
        } catch (Exception ex) {
          callback.failure(new ErrorCodeException(ErrorCodes.CARAVAN_CANT_RESTORE_EXCEPTION, ex));
          return;
        }

        execute("restore", key, false, callback, (id, cached) -> {
          if (cached == null) {
            cached = new LocalCache() {
              @Override
              public void finished() {
                LocalCache builder = this;
                // Step (4) merge the data into the file
                executor.execute(new NamedRunnable("restore-write-map") {
                  @Override
                  public void execute() throws Exception {
                    cache.put(id, builder);
                    mergeRestore(id, builder, writes, callback);
                  }
                });
              }
            };
            // Step 3(a): attempt to read data from disk
            try {
              store.read(id, cached);
            } catch (Exception ex) {
              callback.failure(new ErrorCodeException(ErrorCodes.CARAVAN_CANT_RESTORE_CANT_READ, ex));
              return;
            }
          } else {
            mergeRestore(id, cached, writes, callback);
          }
        });
      }

      @Override
      public void failure(ErrorCodeException ex) {
        callback.failure(ex);
      }
    });
  }

  /** execute with the translation service and jump into the executor */
  private <T> void execute(String name, Key key, boolean load, Callback<T> callback, BiConsumer<Long, LocalCache> action) {
    finder.find(key, new Callback<FinderService.Result>() {
      @Override
      public void success(FinderService.Result result) {
        long id = result.id;
        executor.execute(new NamedRunnable(name) {
          @Override
          public void execute() throws Exception {
            LocalCache cached = cache.get(id);
            if (load && cached == null) {
              load(id, new Callback<>() {
                @Override
                public void success(LocalCache cached) {
                  executor.execute(new NamedRunnable(name, "load") {
                    @Override
                    public void execute() throws Exception {
                      cache.put(id,cached);
                      action.accept(id,cached);
                    }
                  });
                }

                @Override
                public void failure(ErrorCodeException ex) {
                  callback.failure(ex);
                }
              });
            } else {
              action.accept(id, cached);
            }
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

  private void load(long id, Callback<LocalCache> callback) {
    try {
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
              callback.success(builder);
            }
          });
        }
      };
      store.read(id, builder);
    } catch (Exception ex) {
      callback.failure(new ErrorCodeException(ErrorCodes.CARAVAN_LOAD_FAILURE_EXCEPTION));
    }
  }

  @Override
  public void get(Key key, Callback<LocalDocumentChange> callback) {
    execute("get", key, false, callback, (id, cached) -> {
      if (cached != null) {
        callback.success(cached.build());
        return;
      }
      load(id, new Callback<LocalCache>() {
        @Override
        public void success(LocalCache builder) {
          cache.put(id, builder);
          callback.success(builder.build());
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

    execute("initialize", key, false, callback, (id, cached) -> {
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
    execute("patch", key, true, callback, (id, cached) -> {
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
    execute("compute", key, true, callback, (id, cached) -> {
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
    execute("delete", key, false, callback, (id, cached) -> {
      store.delete(id, () -> {});
      callback.success(null);
      cache.remove(id);
    });
  }

  @Override
  public void snapshot(Key key, int seq, String snapshot, int history, Callback<Integer> callback) {
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

    execute("snapshot", key, true, callback, (id, cached) -> {
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

  @Override
  public void close(Key key, Callback<Void> callback) {
    execute("close", key, false, callback, (id, cached) -> {
      if (cached != null) {
        cache.remove(id);
      }
      callback.success(null);
    });
  }
}
