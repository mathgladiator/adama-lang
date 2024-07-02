/*
* Adama Platform and Language
* Copyright (C) 2021 - 2024 by Adama Platform Engineering, LLC
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
package org.adamalang.runtime.deploy;

import org.adamalang.common.Callback;
import org.adamalang.common.SimpleExecutor;
import org.adamalang.common.TimeSource;
import org.adamalang.common.cache.AsyncSharedLRUCache;
import org.adamalang.common.cache.SyncCacheLRU;

import java.util.concurrent.atomic.AtomicBoolean;

/** a herd protection mechanism for the compiler */
public class CachedAsyncByteCodeCache implements AsyncByteCodeCache {
  private final SyncCacheLRU<ByteCodeKey, CachedByteCode> storage;
  private final AsyncSharedLRUCache<ByteCodeKey, CachedByteCode> cache;

  public CachedAsyncByteCodeCache(TimeSource timeSource, int maxByteCodes, long maxAge, SimpleExecutor executor, AsyncByteCodeCache byteCodeCache) {
    this.storage = new SyncCacheLRU<>(timeSource, 0, maxByteCodes, 1024 * 1024L * maxByteCodes, maxAge, SyncCacheLRU.MAKE_NO_OP());
    this.cache = new AsyncSharedLRUCache<>(executor, storage, (key, cb) -> {
      byteCodeCache.fetchOrCompile(key.spaceName, key.className, key.javaSource, key.reflection, cb);
    });
  }

  public void startSweeping(AtomicBoolean alive, int periodMinimumMs, int periodMaximumMs) {
    this.cache.startSweeping(alive, periodMinimumMs, periodMaximumMs);
  }

  @Override
  public void fetchOrCompile(String spaceName, String className, String javaSource, String reflection, Callback<CachedByteCode> callback) {
    this.cache.get(new ByteCodeKey(spaceName, className, javaSource, reflection), callback);
  }
}
