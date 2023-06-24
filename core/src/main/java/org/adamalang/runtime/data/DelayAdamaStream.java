/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.runtime.data;

import org.adamalang.ErrorCodes;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.NamedRunnable;
import org.adamalang.common.SimpleExecutor;
import org.adamalang.common.metrics.ItemActionMonitor;
import org.adamalang.common.queue.ItemAction;
import org.adamalang.common.queue.ItemQueue;
import org.adamalang.runtime.contracts.AdamaStream;

import java.util.function.Consumer;

/** implements a AdamaStream which is delayed against an executor such that it can change */
public class DelayAdamaStream implements AdamaStream {
  private final SimpleExecutor executor;
  private final ItemQueue<AdamaStream> queue;
  private final ItemActionMonitor monitor;

  public DelayAdamaStream(SimpleExecutor executor, ItemActionMonitor monitor) {
    this.executor = executor;
    this.queue = new ItemQueue<>(executor, 16, 2500);
    this.monitor = monitor;
  }

  public void ready(AdamaStream stream) {
    executor.execute(new NamedRunnable("adama-stream-delay-ready") {
      @Override
      public void execute() throws Exception {
        queue.ready(stream);
      }
    });
  }

  public void unready() {
    executor.execute(new NamedRunnable("adama-stream-delay-unready") {
      @Override
      public void execute() throws Exception {
        queue.unready();
      }
    });
  }

  @Override
  public void update(String newViewerState) {
    buffer((stream) -> stream.update(newViewerState), Callback.DONT_CARE_VOID);
  }

  public void buffer(Consumer<AdamaStream> consumer, Callback<?> callback) {
    ItemActionMonitor.ItemActionMonitorInstance instance = monitor.start();
    executor.execute(new NamedRunnable("adama-stream-delay") {
      @Override
      public void execute() throws Exception {
        queue.add(new ItemAction<AdamaStream>(ErrorCodes.CORE_DELAY_ADAMA_STREAM_TIMEOUT, ErrorCodes.CORE_DELAY_ADAMA_STREAM_REJECTED, instance) {
          @Override
          protected void executeNow(AdamaStream stream) {
            consumer.accept(stream);
          }

          @Override
          protected void failure(int code) {
            callback.failure(new ErrorCodeException(code));
          }
        });
      }
    });
  }

  @Override
  public void send(String channel, String marker, String message, Callback<Integer> callback) {
    buffer((stream) -> stream.send(channel, marker, message, callback), callback);
  }

  @Override
  public void password(String password, Callback<Integer> callback) {
    buffer((stream) -> stream.password(password, callback), callback);
  }

  @Override
  public void canAttach(Callback<Boolean> callback) {
    buffer((stream) -> stream.canAttach(callback), callback);
  }

  @Override
  public void attach(String id, String name, String contentType, long size, String md5, String sha384, Callback<Integer> callback) {
    buffer((stream) -> stream.attach(id, name, contentType, size, md5, sha384, callback), callback);
  }

  @Override
  public void close() {
    buffer((stream) -> stream.close(), Callback.DONT_CARE_VOID);
  }
}
