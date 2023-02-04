package org.adamalang.web.assets.cache;

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
import java.util.concurrent.atomic.AtomicLong;

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

  public FileCacheAsset(long localId, File cacheRoot, NtAsset asset, SimpleExecutor executor) throws ErrorCodeException {
    this.asset = asset;
    this.executor = executor;
    this.done = false;
    this.written = 0L;
    this.streams = new ArrayList<>();
    try {
      String name = "asset." + localId + "." + asset.id + ".cache";
      this.filename = new File(cacheRoot, name);
      this.file = new RandomAccessFile(filename, "rwd");
    } catch (Exception ex) {
      throw ErrorCodeException.detectOrWrap(-123, ex, EXLOGGER);
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

  @Override
  public AssetStream attachWhileInExecutor(AssetStream attach) {
    attach.headers(asset.size, asset.contentType);
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
        public void headers(long length, String contentType) {
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
                failure(-123);
                return;
              }

              // if this is the last chunk, then set the done flag and clean things up
              if (last) {
                done = true;
                streams.clear();
              }
            }
          });
        }

        @Override
        public void failure(int code) {
          executor.execute(new NamedRunnable("mc-failure") {
            @Override
            public void execute() throws Exception {
              for (AssetStream existing : streams) {
                existing.failure(code);
              }
              streams.clear();
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
    try {
      file.close();
    } catch (Exception ex) {

    }
  }

  @Override
  public long measure() {
    return asset.size;
  }
}
