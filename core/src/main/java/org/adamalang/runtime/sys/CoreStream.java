/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
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
  private final PrivateView view;

  public CoreStream(CoreRequestContext context, CoreMetrics metrics, PredictiveInventory inventory, DurableLivingDocument document, PrivateView view) {
    this.context = context;
    this.metrics = metrics;
    this.inventory = inventory;
    this.document = document;
    this.view = view;
    inventory.message();
    inventory.connect();
    metrics.inflight_streams.up();
  }

  @Override
  public void update(String newViewerState) {
    updateView(new JsonStreamReader(newViewerState));
  }

  /** update the viewer state */
  public void updateView(JsonStreamReader patch) {
    document.base.executor.execute(new NamedRunnable("core-stream-send") {
      @Override
      public void execute() throws Exception {
        inventory.message();
        view.ingest(patch);
        document.invalidate(Callback.DONT_CARE_INTEGER);
        // TODO: for efficiency sake, we can recompute just the view. However, it does require no patch being inflight to not leak data.
      }
    });
  }

  /** send a message to the document */
  @Override
  public void send(String channel, String marker, String message, Callback<Integer> callback) {
    document.base.executor.execute(new NamedRunnable("core-stream-send") {
      @Override
      public void execute() throws Exception {
        inventory.message();
        document.send(context, marker, channel, message, callback);
      }
    });
  }

  @Override
  public void canAttach(Callback<Boolean> callback) {
    document.base.executor.execute(new NamedRunnable("core-stream-can-attach") {
      @Override
      public void execute() throws Exception {
        inventory.message();
        try {
          callback.success(document.canAttach(context.who));
        } catch (Exception ex) {
          callback.failure(ErrorCodeException.detectOrWrap(ErrorCodes.CORE_STREAM_CAN_ATTACH_UNKNOWN_EXCEPTION, ex, LOGGER));
        }
      }
    });
  }

  @Override
  public void attach(String id, String name, String contentType, long size, String md5, String sha384, Callback<Integer> callback) {
    attach(new NtAsset(id, name, contentType, size, md5, sha384), callback);
  }

  @Override
  public void close() {
    disconnect();
  }

  public void attach(NtAsset asset, Callback<Integer> callback) {
    document.base.executor.execute(new NamedRunnable("core-stream-attach") {
      @Override
      public void execute() throws Exception {
        inventory.message();
        document.attach(context.who, asset, callback);
      }
    });
  }

  /** disconnect this stream from the document */
  public void disconnect() {
    metrics.inflight_streams.down();
    document.base.executor.execute(new NamedRunnable("core-stream-disconnect") {
      @Override
      public void execute() throws Exception {
        // documents that is silent
        // account for the disconnect message
        inventory.message();
        // disconnect this view
        view.kill();
        // clean up and keep things tidy
        if (document.garbageCollectPrivateViewsFor(context.who) == 0) {
          // falling edge disconnects the person
          document.disconnect(context.who, Callback.DONT_CARE_INTEGER);
        } else {
          document.invalidate(Callback.DONT_CARE_INTEGER);
        }
        // tell the client
        view.perspective.disconnect();
      }
    });
  }
}
