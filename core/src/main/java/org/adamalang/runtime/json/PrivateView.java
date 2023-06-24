/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.runtime.json;

import org.adamalang.runtime.contracts.Perspective;
import org.adamalang.runtime.delta.secure.AssetIdEncoder;
import org.adamalang.runtime.natives.NtPrincipal;

/** a private view of the document where private lives; code is generated to use this */
public abstract class PrivateView {
  private static final String DEFAULT_FUTURES = "\"outstanding\":[],\"blockers\":[]";
  public final Perspective perspective;
  public final NtPrincipal who;
  public final AssetIdEncoder assetIdEncoder;
  public final int viewId;
  private boolean alive;
  private PrivateView usurper;
  private String lastWrittenFutures;

  /** construct the view based on the person (who) and the connection (perspective) */
  public PrivateView(final int viewId, final NtPrincipal who, final Perspective perspective, final AssetIdEncoder assetIdEncoder) {
    this.viewId = viewId;
    this.alive = true;
    this.who = who;
    this.assetIdEncoder = assetIdEncoder;
    this.perspective = perspective;
    this.lastWrittenFutures = DEFAULT_FUTURES;
  }

  /**
   * a new private view was created on a different document which is usurping the existing document.
   * Since private views leak outside the document, this creates a proxy link for the client to kill
   * both views.
   */
  public void usurp(PrivateView usurper) {
    this.usurper = usurper;
  }

  /** codegen: seed the state held by the view */
  public abstract void ingest(JsonStreamReader reader);

  /** codegen: dump the data held by the view */
  public abstract void dumpViewer(JsonStreamWriter writer);

  /** send the user a delivery update */
  public void deliver(final String delivery) {
    perspective.data(delivery);
  }

  /** is the view still alive and interesting to the user */
  public synchronized boolean isAlive() {
    return alive;
  }

  /** get the memory of the view */
  public abstract long memory();

  /** dedupe excessive outstanding and blockers sharing */
  public boolean futures(String futures) {
    String futuresToTest = futures;
    if (futuresToTest.equals(DEFAULT_FUTURES)) { // save some memory
      futuresToTest = DEFAULT_FUTURES;
    }
    if (futuresToTest == lastWrittenFutures || lastWrittenFutures.equals(futures)) {
      return false;
    } else {
      lastWrittenFutures = futures;
      return true;
    }
  }

  /** the client is no longer interested */
  public synchronized void kill() {
    if (usurper != null) {
      // this has been usurped, so kill the usurper as well
      usurper.kill();
    }
    alive = false;
  }

  /** codegen: the data inside the view has been updated, stream it out to the writer */
  public abstract void update(JsonStreamWriter writer);
}
