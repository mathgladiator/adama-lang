/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.runtime.sys;

import org.adamalang.common.Callback;
import org.adamalang.common.NamedRunnable;
import org.adamalang.runtime.json.PrivateView;
import org.adamalang.runtime.natives.NtAsset;
import org.adamalang.runtime.natives.NtClient;

/**
 * Represents a stream for the consumer to interact with the document. This simplifies the
 * interaction model such that consumers don't need to think about how threading happens.
 */
public class CoreStream {
  private final NtClient who;
  private final PredictiveInventory inventory;
  private final DurableLivingDocument document;
  private final PrivateView view;

  public CoreStream(
      NtClient who,
      PredictiveInventory inventory,
      DurableLivingDocument document,
      PrivateView view) {
    this.who = who;
    this.inventory = inventory;
    this.document = document;
    this.view = view;
  }

  /** send a message to the document */
  public void send(String channel, String marker, String message, Callback<Integer> callback) {
    document.base.executor.execute(
        new NamedRunnable("core-stream-send") {
          @Override
          public void execute() throws Exception {
            inventory.message();
            document.send(who, marker, channel, message, callback);
          }
        });
  }

  public void canAttach(Callback<Boolean> callback) {
    document.base.executor.execute(
        new NamedRunnable("core-stream-can-attach") {
          @Override
          public void execute() throws Exception {
            inventory.message();
            callback.success(document.canAttach(who));
          }
        });
  }

  public void attach(NtAsset asset, Callback<Integer> callback) {
    document.base.executor.execute(
        new NamedRunnable("core-stream-attach") {
          @Override
          public void execute() throws Exception {
            inventory.message();
            document.attach(who, asset, callback);
          }
        });
  }

  /** disconnect this stream from the document */
  public void disconnect() {
    document.base.executor.execute(
        new NamedRunnable("core-stream-disconnect") {
          @Override
          public void execute() throws Exception {
            // disconnect this view
            view.kill();
            // clean up and keep things tidy
            if (document.garbageCollectPrivateViewsFor(who) == 0) {
              // falling edge disconnects the person
              document.disconnect(who, Callback.DONT_CARE_INTEGER);
            } else {
              document.invalidate(Callback.DONT_CARE_INTEGER);
            }
            // tell the client
            view.perspective.disconnect();
          }
        });
  }
}
