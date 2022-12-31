/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.net.codec;

import org.adamalang.common.codec.FieldOrder;
import org.adamalang.common.codec.Flow;
import org.adamalang.common.codec.TypeId;
import org.adamalang.runtime.data.RemoteDocumentUpdate;
import org.adamalang.runtime.data.UpdateType;
import org.adamalang.runtime.natives.NtPrincipal;

/** messages from client to server */
public class ClientMessage {

  @TypeId(24321)
  @Flow("Server")
  public static class PingRequest {
  }

  @TypeId(12523)
  @Flow("Server")
  public static class CreateRequest {
    @FieldOrder(1)
    public String space;
    @FieldOrder(2)
    public String key;
    @FieldOrder(3)
    public String arg;
    @FieldOrder(4)
    public String entropy;
    @FieldOrder(5)
    public String agent;
    @FieldOrder(6)
    public String authority;
    @FieldOrder(7)
    public String origin;
    @FieldOrder(8)
    public String ip;
  }

  @TypeId(12525)
  @Flow("Server")
  public static class DeleteRequest {
    @FieldOrder(1)
    public String space;
    @FieldOrder(2)
    public String key;
    @FieldOrder(3)
    public String agent;
    @FieldOrder(4)
    public String authority;
    @FieldOrder(5)
    public String origin;
    @FieldOrder(6)
    public String ip;
  }

  @TypeId(6735)
  @Flow("Server")
  public static class ReflectRequest {
    @FieldOrder(1)
    public String space;

    @FieldOrder(2)
    public String key;
  }

  @TypeId(8921)
  @Flow("Server")
  public static class ScanDeployment {
    @FieldOrder(1)
    public String space;
  }

  @TypeId(1243)
  @Flow("Server")
  public static class MeteringBegin {
  }

  @TypeId(1245)
  @Flow("Server")
  public static class MeteringDeleteBatch {
    @FieldOrder(1)
    public String id;
  }

  @TypeId(12345)
  @Flow("Server")
  public static class StreamConnect {
    @FieldOrder(1)
    public String space;
    @FieldOrder(2)
    public String key;
    @FieldOrder(3)
    public String agent;
    @FieldOrder(4)
    public String authority;
    @FieldOrder(5)
    public String viewerState;
    @FieldOrder(6)
    public String origin;
    @FieldOrder(7)
    public String ip;
    @FieldOrder(8)
    public String assetKey;
  }

  @TypeId(13345)
  @Flow("Server")
  public static class StreamSend {
    @FieldOrder(1)
    public int op;
    @FieldOrder(2)
    public String channel;
    @FieldOrder(3)
    public String marker;
    @FieldOrder(4)
    public String message;
  }

  @TypeId(14345)
  @Flow("Server")
  public static class StreamUpdate {
    @FieldOrder(1)
    public String viewerState;
  }

  @TypeId(13335)
  @Flow("Server")
  public static class StreamDisconnect {
  }

  @TypeId(15345)
  @Flow("Server")
  public static class StreamAskAttachmentRequest {
    @FieldOrder(1)
    public int op;
  }

  @TypeId(16345)
  @Flow("Server")
  public static class StreamAttach {
    @FieldOrder(1)
    public int op;

    @FieldOrder(2)
    public String id;

    @FieldOrder(3)
    public String filename;

    @FieldOrder(4)
    public String contentType;

    @FieldOrder(5)
    public long size;

    @FieldOrder(6)
    public String md5;

    @FieldOrder(7)
    public String sha384;
  }

  @TypeId(1919)
  @Flow("Server")
  public static class RequestHeat {
  }

  @TypeId(7231)
  @Flow("Server")
  public static class RequestInventoryHeartbeat {
  }

  @TypeId(9001)
  @Flow("Server")
  public static class ProxyGet {
    @FieldOrder(1)
    public String space;

    @FieldOrder(2)
    public String key;
  }

  @TypeId(9003)
  @Flow("PatchItem")
  public static class RemoteDocumentUpdateItem {
    @FieldOrder(1)
    public String agent;
    @FieldOrder(2)
    public String authority;
    @FieldOrder(3)
    public int seq_begin;
    @FieldOrder(4)
    public int seq_end;
    @FieldOrder(5)
    public String request;
    @FieldOrder(6)
    public String redo;
    @FieldOrder(7)
    public String undo;
    @FieldOrder(8)
    public boolean active;
    @FieldOrder(9)
    public int delay;
    @FieldOrder(10)
    public long dAssetBytes;

    public void copyFrom(RemoteDocumentUpdate update) {
      this.seq_begin = update.seqBegin;
      this.seq_end = update.seqEnd;
      if (update.who != null) {
        this.agent = update.who.agent;
        this.authority = update.who.authority;
      }
      this.request = update.request;
      this.redo = update.redo;
      this.undo = update.undo;
      this.active = update.requiresFutureInvalidation;
      this.delay = update.whenToInvalidateMilliseconds;
      this.dAssetBytes = update.assetBytes;
    }

    public RemoteDocumentUpdate toRemoteDocumentUpdate() {
      return new RemoteDocumentUpdate(seq_begin, seq_end, agent != null ? new NtPrincipal(agent, authority) : null, request, redo, undo, active, delay, dAssetBytes, UpdateType.Internal);
    }
  }

  @TypeId(9005)
  @Flow("Server")
  public static class ProxyInitialize {
    @FieldOrder(1)
    public String space;

    @FieldOrder(2)
    public String key;

    @FieldOrder(3)
    public RemoteDocumentUpdateItem initial;

  }

  @TypeId(9007)
  @Flow("Server")
  public static class ProxyPatch {
    @FieldOrder(1)
    public String space;

    @FieldOrder(2)
    public String key;

    @FieldOrder(3)
    public RemoteDocumentUpdateItem[] patches;
  }

  @TypeId(9009)
  @Flow("Server")
  public static class ProxyCompute {
    @FieldOrder(1)
    public String space;

    @FieldOrder(2)
    public String key;

    @FieldOrder(3)
    public int method;

    @FieldOrder(4)
    public int seq;
  }

  @TypeId(9011)
  @Flow("Server")
  public static class ProxyDelete {
    @FieldOrder(1)
    public String space;

    @FieldOrder(2)
    public String key;
  }

  @TypeId(9013)
  @Flow("Server")
  public static class ProxySnapshot {
    @FieldOrder(1)
    public String space;
    @FieldOrder(2)
    public String key;
    @FieldOrder(3)
    public int seq;
    @FieldOrder(4)
    public int history;
    @FieldOrder(5)
    public String document;
    @FieldOrder(6)
    public long assetBytes;
  }

  @TypeId(9015)
  @Flow("Server")
  public static class ProxyClose {
    @FieldOrder(1)
    public String space;
    @FieldOrder(2)
    public String key;
  }

  @TypeId(1722)
  @Flow("WebGetHeader")
  public static class Header {
    @FieldOrder(1)
    public String key;
    @FieldOrder(2)
    public String value;
  }

  @TypeId(1721)
  @Flow("Server")
  public static class WebGet {
    @FieldOrder(1)
    public String space;
    @FieldOrder(2)
    public String key;
    @FieldOrder(3)
    public String agent;
    @FieldOrder(4)
    public String authority;
    @FieldOrder(5)
    public String uri;
    @FieldOrder(6)
    public Header[] headers;
    @FieldOrder(7)
    public String parametersJson;
    @FieldOrder(8)
    public String origin;
    @FieldOrder(9)
    public String ip;
  }

  @TypeId(1723)
  @Flow("Server")
  public static class WebPut {
    @FieldOrder(1)
    public String space;
    @FieldOrder(2)
    public String key;
    @FieldOrder(3)
    public String agent;
    @FieldOrder(4)
    public String authority;
    @FieldOrder(5)
    public String uri;
    @FieldOrder(6)
    public Header[] headers;
    @FieldOrder(7)
    public String parametersJson;
    @FieldOrder(8)
    public String bodyJson;
    @FieldOrder(9)
    public String origin;
    @FieldOrder(10)
    public String ip;
  }

  @TypeId(1725)
  @Flow("Server")
  public static class WebOptions {
    @FieldOrder(1)
    public String space;
    @FieldOrder(2)
    public String key;
    @FieldOrder(3)
    public String agent;
    @FieldOrder(4)
    public String authority;
    @FieldOrder(5)
    public String uri;
    @FieldOrder(6)
    public Header[] headers;
    @FieldOrder(7)
    public String parametersJson;
    @FieldOrder(8)
    public String origin;
    @FieldOrder(9)
    public String ip;
  }

  @TypeId(1727)
  @Flow("Server")
  public static class WebDelete {
    @FieldOrder(1)
    public String space;
    @FieldOrder(2)
    public String key;
    @FieldOrder(3)
    public String agent;
    @FieldOrder(4)
    public String authority;
    @FieldOrder(5)
    public String uri;
    @FieldOrder(6)
    public Header[] headers;
    @FieldOrder(7)
    public String parametersJson;
    @FieldOrder(8)
    public String origin;
    @FieldOrder(9)
    public String ip;
  }

  @TypeId(1999)
  @Flow("Server")
  public static class ExecuteQuery {
    @FieldOrder(1)
    public Header[] headers;
  }

  @TypeId(17345)
  @Flow("Server")
  public static class DirectSend {
    @FieldOrder(1)
    public String space;
    @FieldOrder(2)
    public String key;
    @FieldOrder(3)
    public String agent;
    @FieldOrder(4)
    public String authority;
    @FieldOrder(5)
    public String origin;
    @FieldOrder(6)
    public String ip;
    @FieldOrder(7)
    public String marker;
    @FieldOrder(8)
    public String channel;
    @FieldOrder(9)
    public String message;
  }
}
