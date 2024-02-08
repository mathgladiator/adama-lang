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
package org.adamalang.runtime.sys;

import org.adamalang.ErrorCodes;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.ExceptionLogger;
import org.adamalang.common.NamedRunnable;
import org.adamalang.runtime.contracts.AdamaStream;
import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.json.PrivateView;
import org.adamalang.runtime.natives.NtAsset;

/**
 * Represents a stream for the consumer to interact with the document. This simplifies the
 * interaction model such that consumers don't need to think about how threading happens.
 */
public class CoreStream implements AdamaStream {
  private static final ExceptionLogger LOGGER = ExceptionLogger.FOR(CoreStream.class);
  private final CoreRequestContext context;
  private final CoreMetrics metrics;
  private final PredictiveInventory inventory;
  private final DurableLivingDocument document;
  private final StreamHandle handle;

  public CoreStream(CoreRequestContext context, CoreMetrics metrics, PredictiveInventory inventory, DurableLivingDocument document, StreamHandle handle) {
    this.context = context;
    this.metrics = metrics;
    this.inventory = inventory;
    this.document = document;
    this.handle = handle;
    inventory.message();
    inventory.connect();
    metrics.inflight_streams.up();
  }

  @Override
  public void update(String newViewerState) {
    JsonStreamReader patch = new JsonStreamReader(newViewerState);
    document.base.executor.execute(new NamedRunnable("core-stream-send") {
      @Override
      public void execute() throws Exception {
        inventory.message();
        handle.ingestViewUpdate(patch);
        if (document.document().__hasInflightAsyncWork()) {
          // this is, at core, fundamentally expensive
          document.invalidate(Callback.DONT_CARE_INTEGER);
        } else {
          handle.triggerRefresh();
        }
      }
    });
  }

  /** send a message to the document */
  @Override
  public void send(String channel, String marker, String message, Callback<Integer> callback) {
    if (!document.base.shield.canSendMessageExisting.get()) {
      callback.failure(new ErrorCodeException(ErrorCodes.SHIELD_REJECT_SEND_MESSAGE));
      return;
    }

    document.base.executor.execute(new NamedRunnable("core-stream-send") {
      @Override
      public void execute() throws Exception {
        inventory.message();
        document.send(context, handle.getViewId(), marker, channel, message, callback);
      }
    });
  }

  @Override
  public void password(String password, Callback<Integer> callback) {
    document.base.executor.execute(new NamedRunnable("core-stream-password") {
      @Override
      public void execute() throws Exception {
        inventory.message();
        document.setPassword(context, password, callback);
      }
    });
  }

  @Override
  public void canAttach(Callback<Boolean> callback) {
    if (!document.base.shield.canSendMessageExisting.get()) {
      callback.failure(new ErrorCodeException(ErrorCodes.SHIELD_REJECT_SEND_MESSAGE));
      return;
    }
    document.base.executor.execute(new NamedRunnable("core-stream-can-attach") {
      @Override
      public void execute() throws Exception {
        inventory.message();
        try {
          callback.success(document.canAttach(context));
        } catch (Exception ex) {
          callback.failure(ErrorCodeException.detectOrWrap(ErrorCodes.CORE_STREAM_CAN_ATTACH_UNKNOWN_EXCEPTION, ex, LOGGER));
        }
      }
    });
  }

  @Override
  public void attach(String id, String name, String contentType, long size, String md5, String sha384, Callback<Integer> callback) {
    NtAsset asset = new NtAsset(id, name, contentType, size, md5, sha384);
    document.base.executor.execute(new NamedRunnable("core-stream-attach") {
      @Override
      public void execute() throws Exception {
        inventory.message();
        document.attach(context, asset, callback);
      }
    });
  }

  @Override
  public void close() {
    metrics.inflight_streams.down();
    document.base.executor.execute(new NamedRunnable("core-stream-disconnect") {
      @Override
      public void execute() throws Exception {
        // documents that is silent
        // account for the disconnect message
        inventory.message();
        // disconnect this view
        handle.kill();
        // clean up and keep things tidy
        if (document.garbageCollectPrivateViewsFor(context.who) == 0) {
          // falling edge disconnects the person
          document.disconnect(context, Callback.DONT_CARE_INTEGER);
        } else {
          document.invalidate(Callback.DONT_CARE_INTEGER);
        }
        // tell the client
        handle.disconnect();
      }
    });
  }
}
