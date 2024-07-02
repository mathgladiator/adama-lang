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
package org.adamalang.web.assets.cache;

import org.adamalang.ErrorCodes;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.ExceptionLogger;
import org.adamalang.common.NamedRunnable;
import org.adamalang.common.SimpleExecutor;
import org.adamalang.runtime.natives.NtAsset;
import org.adamalang.web.assets.AssetStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;

public class FileCacheAsset implements CachedAsset {
  private static final Logger LOG = LoggerFactory.getLogger(FileCacheAsset.class);
  private static final ExceptionLogger EXLOGGER = ExceptionLogger.FOR(LOG);
  private final NtAsset asset;
  private final SimpleExecutor executor;
  private ArrayList<AssetStream> streams;
  private final File filename;
  private final RandomAccessFile file;
  private long written;
  private boolean done;
  private boolean kill;
  private Integer failed;

  public FileCacheAsset(long localId, File cacheRoot, NtAsset asset, SimpleExecutor executor) throws ErrorCodeException {
    this.asset = asset;
    this.executor = executor;
    this.done = false;
    this.written = 0L;
    this.kill = false;
    this.failed = null;
    this.streams = new ArrayList<>();
    try {
      String name = "asset." + localId + "." + asset.id + ".cache";
      this.filename = new File(cacheRoot, name);
      this.file = new RandomAccessFile(filename, "rwd");
    } catch (Exception ex) {
      throw ErrorCodeException.detectOrWrap(ErrorCodes.CACHE_ASSET_FILE_FAILED_CREATE, ex, EXLOGGER);
    }
  }

  @Override
  public SimpleExecutor executor() {
    return executor;
  }

  private void pumpCurrent(AssetStream attach) {
    try {
      file.seek(0L);
      int rd;
      byte[] chunk = new byte[8192];
      long at = 0;
      while ((rd = file.read(chunk)) >= 0) {
        at += rd;
        byte[] copy = Arrays.copyOfRange(chunk, 0, rd);
        attach.body( copy, 0, copy.length, at == asset.size);
      }
    } catch (Exception ex) {
      attach.failure(ErrorCodeException.detectOrWrap(-123, ex, EXLOGGER).code);
    }
  }

  private void killWhileInExecutor() {
    try {
      file.close();
      filename.delete();
      if (failed == null) {
        failed = ErrorCodes.CACHE_ASSET_FILE_CLOSED_PRIOR_ATTACH;
      }
    } catch (Exception ignoreCacheLeak) {
      LOG.error("cache-leak", ignoreCacheLeak);
    }
  }

  @Override
  public AssetStream attachWhileInExecutor(AssetStream attach) {
    if (failed != null) {
      attach.failure(failed);
      return null;
    }
    attach.headers(asset.size, asset.contentType, asset.md5);
    if (done) { // the cache item has been fed, so simply replay what was captured
      pumpCurrent(attach);
      return null;
    }
    // otherwise, we need to wait for data...
    if (streams.size() == 0) {
      // this is the first attach call which is special; return the asset stream to pump both attach and late-joiners
      streams.add(attach);
      return new AssetStream() {
        @Override
        public void headers(long length, String contentType, String md5) {
          // the headers were already transmitted
        }

        @Override
        public void body(byte[] chunk, int offset, int length, boolean last) {
          byte[] clone = Arrays.copyOfRange(chunk, offset, length); // TODO: this signature of byte[] chunk is... bad

          // forward the body to all connected streams
          executor.execute(new NamedRunnable("mc-body") {
            @Override
            public void execute() throws Exception {
              // replicate the write to all attached streams
              for (AssetStream existing : streams) {
                existing.body(clone, 0, length, last);
              }

              try {
                file.seek(written);
                file.write(clone, 0, length);
                file.getFD().sync();
                written += length;
              } catch (Exception ex) {
                failure(ErrorCodes.CACHE_ASSET_FILE_FAILED_WRITE);
                return;
              }

              // if this is the last chunk, then set the done flag and clean things up
              if (last) {
                done = true;
                streams.clear();
                if (kill) {
                  killWhileInExecutor();
                }
              }
            }
          });
        }

        @Override
        public void failure(int code) {
          executor.execute(new NamedRunnable("mc-failure") {
            @Override
            public void execute() throws Exception {
              failed = code;
              for (AssetStream existing : streams) {
                existing.failure(code);
              }
              streams.clear();
              killWhileInExecutor();
            }
          });
        }
      };
    } else {
      pumpCurrent(attach);
      streams.add(attach);
      return null;
    }
  }

  @Override
  public void evict() {
    executor.execute(new NamedRunnable("mc-evict") {
      @Override
      public void execute() throws Exception {
        if (done) {
          killWhileInExecutor();
        } else {
          kill = true;
        }
      }
    });
  }

  @Override
  public long measure() {
    return asset.size;
  }
}
