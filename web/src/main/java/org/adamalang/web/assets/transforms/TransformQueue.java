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

import org.adamalang.common.*;
import org.adamalang.common.cache.AsyncSharedLRUCache;
import org.adamalang.common.cache.SyncCacheLRU;
import org.adamalang.runtime.data.Key;
import org.adamalang.runtime.natives.NtAsset;
import org.adamalang.web.assets.AssetRequest;
import org.adamalang.web.assets.AssetStream;
import org.adamalang.web.assets.AssetSystem;
import org.adamalang.web.assets.cache.CachedAsset;

import java.io.File;
import java.security.MessageDigest;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/** A queue for executing transforms */
public class TransformQueue {
  private final TimeSource time;
  private final File transformRoot;
  private final SimpleExecutor executorCache;
  private final SimpleExecutor executorTransform;
  private final AtomicBoolean alive;
  private final SyncCacheLRU<TransformTask, TransformAsset> cache;
  private final AsyncSharedLRUCache<TransformTask, TransformAsset> async;
  private final AssetSystem assets;

  public TransformQueue(TimeSource time,  File transformRoot, AssetSystem assets) {
    this.time = time;
    this.transformRoot = transformRoot;
    this.executorCache = SimpleExecutor.create("transforms-cache");
    this.executorTransform = SimpleExecutor.create("transforms-transform");
    this.alive = new AtomicBoolean(true);
    this.assets = assets;
    this.cache = new SyncCacheLRU<>(time, 10, 2000, 128 * 1024 * 1024L, 30 * 60000, (key, item) -> {
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

    public TransformTask(Key key, String instruction, Transform transform, NtAsset asset) {
      this.key = key;
      this.instruction = instruction;
      this.transform = transform;
      this.asset = asset;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      TransformTask that = (TransformTask) o;
      return Objects.equals(key, that.key) && Objects.equals(instruction, that.instruction) && Objects.equals(asset, that.asset);
    }

    @Override
    public int hashCode() {
      return Objects.hash(key, instruction, asset);
    }

    public void execute(Callback<TransformAsset> callback) {
      AssetRequest request = new AssetRequest(key.space, key.key, asset.id);
      // assets.request(request, );

    }
  }

  public void process(Key key, String instruction, Transform transform, NtAsset asset, AssetStream response) {
    final String hash;
    {
      MessageDigest sha = Hashing.sha384();
      sha.update(instruction.getBytes());
      hash = Hashing.finishAndEncodeHex(sha);
    }

    TransformTask task = new TransformTask(key, instruction, transform, asset);

    async.get(task, new Callback<TransformAsset>() {
      @Override
      public void success(TransformAsset asset) {

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
  }
}
