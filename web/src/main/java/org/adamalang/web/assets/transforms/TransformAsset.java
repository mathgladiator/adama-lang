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
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.ExceptionLogger;
import org.adamalang.common.NamedRunnable;
import org.adamalang.common.SimpleExecutor;
import org.adamalang.common.cache.Measurable;
import org.adamalang.web.assets.AssetStream;

import java.io.File;
import java.io.FileInputStream;

public class TransformAsset implements Measurable {
  private static final ExceptionLogger EXLOGGER = ExceptionLogger.FOR(TransformAsset.class);
  private final SimpleExecutor executor;
  private final File cache;
  private byte[] serveFromMemory;
  private final String contentType;
  private final long size;

  public TransformAsset(SimpleExecutor executor, File cache, String contentType) {
    this.executor = executor;
    this.cache = cache;
    this.serveFromMemory = null;
    this.contentType = contentType;
    this.size = cache.length();
  }

  public void serve(AssetStream response) {
    executor.execute(new NamedRunnable("serve-transform-asset") {
      @Override
      public void execute() throws Exception {
        try {
          response.headers(size, contentType, null);
          FileInputStream input = new FileInputStream(cache);
          try {
            byte[] chunk = new byte[64 * 1024];
            long written = 0;
            int rd;
            while ((rd = input.read(chunk)) >= 0) {
              written += rd;
              response.body(chunk, 0, rd, written >= size);
              chunk = new byte[64 * 1024];
            }
          } finally {
            input.close();
          }
        } catch (Exception ex) {
          response.failure(ErrorCodeException.detectOrWrap(ErrorCodes.CACHE_ASSET_SERVE_FAILURE, ex, EXLOGGER).code);
        }
      }
    });
  }

  public void evict() {
    executor.schedule(new NamedRunnable("evict") {
      @Override
      public void execute() throws Exception {
        cache.delete();
      }
    }, 1000);
  }

  @Override
  public long measure() {
    return size;
  }
}
