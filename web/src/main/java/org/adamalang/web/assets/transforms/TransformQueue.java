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
package org.adamalang.web.assets.transforms;

import org.adamalang.ErrorCodes;
import org.adamalang.common.*;
import org.adamalang.common.cache.AsyncSharedLRUCache;
import org.adamalang.common.cache.SyncCacheLRU;
import org.adamalang.runtime.data.Key;
import org.adamalang.runtime.natives.NtAsset;
import org.adamalang.web.assets.AssetRequest;
import org.adamalang.web.assets.AssetStream;
import org.adamalang.web.assets.AssetSystem;
import org.adamalang.web.assets.transforms.capture.DiskCapture;
import org.adamalang.web.assets.transforms.capture.InflightAsset;
import org.adamalang.web.assets.transforms.capture.MemoryCapture;

import java.io.File;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/** A queue for executing transforms */
public class TransformQueue {
  private static final ExceptionLogger EXLOGGER = ExceptionLogger.FOR(TransformQueue.class);
  private final TimeSource time;
  private final File transformRoot;
  private final SimpleExecutor executorCache;
  private final SimpleExecutor executorTransform;
  private final SimpleExecutor executorDisk;
  private final AtomicBoolean alive;
  private final SyncCacheLRU<TransformTask, TransformAsset> cache;
  private final AsyncSharedLRUCache<TransformTask, TransformAsset> async;
  private final AssetSystem assets;

  public TransformQueue(TimeSource time,  File transformRoot, AssetSystem assets) {
    this.time = time;
    this.transformRoot = transformRoot;
    this.executorCache = SimpleExecutor.create("transforms-cache");
    this.executorTransform = SimpleExecutor.create("transforms-transform");
    this.executorDisk = SimpleExecutor.create("transforms-disk");
    this.alive = new AtomicBoolean(true);
    this.assets = assets;
    this.cache = new SyncCacheLRU<>(time, 10, 10000, 1024L * 1024L * 1024L, 30 * 60000, (key, item) -> {
      item.evict();
    });
    this.async = new AsyncSharedLRUCache<>(executorCache, cache, (task, cb) -> {
      executorTransform.execute(new NamedRunnable("transform") {
        @Override
        public void execute() throws Exception {
          task.execute(cb);
        }
      });
    });
    this.async.startSweeping(alive, 45000, 90000);
  }

  public class TransformTask {
    public final Key key;
    public final String instruction;
    public final Transform transform;
    public final NtAsset asset;
    public final String hash;

    public TransformTask(Key key, String instruction, Transform transform, NtAsset asset, String hash) {
      this.key = key;
      this.instruction = instruction;
      this.transform = transform;
      this.asset = asset;
      this.hash = hash;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      TransformTask that = (TransformTask) o;
      return Objects.equals(key, that.key) && Objects.equals(instruction, that.instruction) && Objects.equals(asset, that.asset) && Objects.equals(hash, that.hash);
    }

    @Override
    public int hashCode() {
      return Objects.hash(key, instruction, asset, hash);
    }

    public void execute(Callback<TransformAsset> callback) {
      AssetRequest request = new AssetRequest(key.space, key.key, asset.id);
      Callback<InflightAsset> thingToTransform = new Callback<InflightAsset>() {
        @Override
        public void success(InflightAsset inflight) {
          executorTransform.execute(new NamedRunnable("running-transform") {
            @Override
            public void execute() throws Exception {
              File output = new File(transformRoot, asset.id + "." + hash + ".result");
              try {
                InputStream input = inflight.open();
                try {
                  transform.execute(input, output);
                  callback.success(new TransformAsset(executorDisk, output, asset.contentType));
                } finally {
                  input.close();
                }
              } catch (Exception ex) {
                callback.failure(ErrorCodeException.detectOrWrap(ErrorCodes.ASSET_TRANSFORM_FAILED_TRANSFORM, ex, EXLOGGER));
              }
              inflight.finished();
            }
          });
        }

        @Override
        public void failure(ErrorCodeException ex) {
          callback.failure(ex);
        }
      };
      AssetStream streamToUse;
      if (asset.size < 1024 * 1024) {
        streamToUse = new MemoryCapture(thingToTransform);
      } else {
        streamToUse = new DiskCapture(executorDisk, asset, transformRoot, thingToTransform);
      }
      assets.request(request, streamToUse);
    }
  }

  public void process(Key key, String instruction, Transform transform, NtAsset asset, AssetStream response) {
    final String hash;
    {
      MessageDigest sha = Hashing.sha384();
      sha.update(instruction.getBytes());
      hash = Hashing.finishAndEncodeHex(sha);
    }

    TransformTask task = new TransformTask(key, instruction, transform, asset, hash);

    async.get(task, new Callback<TransformAsset>() {
      @Override
      public void success(TransformAsset result) {
        result.serve(response);
      }

      @Override
      public void failure(ErrorCodeException ex) {
        response.failure(ex.code);
      }
    });
  }

  public void shutdown() {
    this.alive.set(false);
    try {
      this.executorCache.shutdown().await(1000, TimeUnit.MILLISECONDS);
    } catch (Exception ex) {
    }
    try {
      this.executorTransform.shutdown().await(1000, TimeUnit.MILLISECONDS);
    } catch (Exception ex) {
    }
    try {
      this.executorDisk.shutdown().await(1000, TimeUnit.MILLISECONDS);
    } catch (Exception ex) {
    }
  }
}
