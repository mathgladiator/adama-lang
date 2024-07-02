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
