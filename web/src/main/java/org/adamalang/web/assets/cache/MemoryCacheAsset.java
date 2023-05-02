/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.web.assets.cache;

import org.adamalang.common.NamedRunnable;
import org.adamalang.common.SimpleExecutor;
import org.adamalang.runtime.natives.NtAsset;
import org.adamalang.web.assets.AssetStream;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;

/** A cache that is 100% in-memory */
public class MemoryCacheAsset implements CachedAsset {
  private final NtAsset asset;
  private final SimpleExecutor executor;
  private ByteArrayOutputStream memory;
  private boolean done;
  private ArrayList<AssetStream> streams;
  private Integer failed;

  public MemoryCacheAsset(NtAsset asset, SimpleExecutor executor) {
    this.asset = asset;
    this.executor = executor;
    this.memory = new ByteArrayOutputStream();
    this.done = false;
    this.streams = new ArrayList<>();
    this.failed = null;
  }

  @Override
  public SimpleExecutor executor() {
    return executor;
  }

  @Override
  public void evict() {
    // no-op
  }

  @Override
  public AssetStream attachWhileInExecutor(AssetStream attach) {
    if (failed != null) {
      attach.failure(failed);
      return null;
    }
    attach.headers(asset.size, asset.contentType);
    if (done) { // the cache item has been fed, so simply replay what was captured
      byte[] body = memory.toByteArray();
      attach.body(body, 0, body.length, done);
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
              // record the chunk to memory
              memory.write(clone, 0, length);
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
              failed = code;
              for (AssetStream existing : streams) {
                existing.failure(code);
              }
              streams.clear();
            }
          });
        }
      };
    } else {
      // another stream has started pumping data, so we simply need to replay what has already been record
      byte[] body = memory.toByteArray();
      attach.body(body, 0, body.length, false);
      streams.add(attach);
      return null;
    }
  }

  @Override
  public long measure() {
    return asset.size;
  }
}
