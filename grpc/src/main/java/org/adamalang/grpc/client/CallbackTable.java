/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.grpc.client;

import org.adamalang.ErrorCodes;
import org.adamalang.grpc.client.contracts.AskAttachmentCallback;
import org.adamalang.grpc.client.contracts.Events;
import org.adamalang.grpc.client.contracts.SeqCallback;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicLong;

public class CallbackTable {
  private final HashMap<Long, Events> documents;
  private final HashMap<Long, SeqCallback> outstanding;
  private final HashMap<Long, AskAttachmentCallback> asks;
  private final AtomicLong nextId;

  public CallbackTable() {
    this.documents = new HashMap<>();
    this.outstanding = new HashMap<>();
    this.asks = new HashMap<>();
    this.nextId = new AtomicLong(1);
  }

  public long id() {
    return nextId.getAndIncrement();
  }

  public void associate(long id, Events document) {
    documents.put(id, document);
  }

  public void associate(long id, AskAttachmentCallback callback) {
    asks.put(id, callback);
  }

  public void associate(long id, SeqCallback callback) {
    outstanding.put(id, callback);
  }

  public Events documentsOf(long id) {
    return documents.get(id);
  }

  public void disconnectDocument(long id) {
    Events events = documents.remove(id);
    if (events != null) {
      events.disconnected();
    }
  }

  public void finishAsk(long id, boolean allowed) {
    AskAttachmentCallback callback = asks.remove(id);
    if (callback != null) {
      if (allowed) {
        callback.allow();
      } else {
        callback.reject();
      }
    }
  }

  public void finishSeq(long id, int seq) {
    SeqCallback callback = outstanding.remove(id);
    if (callback != null) {
      callback.success(seq);
    }
  }

  public void error(long id, int code) {
    Events events = documents.remove(id);
    if (events != null) {
      events.error(code);
      return;
    }
    SeqCallback callback = outstanding.remove(id);
    if (callback != null) {
      callback.error(code);
      return;
    }
    AskAttachmentCallback ask = asks.remove(id);
    if (ask != null) {
      ask.error(code);
      return;
    }
  }

  public void kill() {
    for (Events events : documents.values()) {
      events.disconnected();
    }
    documents.clear();
    for (AskAttachmentCallback callback : asks.values()) {
      callback.error(ErrorCodes.GRPC_DISCONNECT);
    }
    asks.clear();
    for (SeqCallback callback : outstanding.values()) {
      callback.error(ErrorCodes.GRPC_DISCONNECT);
    }
    outstanding.clear();
  }
}
