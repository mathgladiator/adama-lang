/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.net.codec;

import org.adamalang.common.codec.FieldOrder;
import org.adamalang.common.codec.Flow;
import org.adamalang.common.codec.TypeId;
import org.adamalang.runtime.data.LocalDocumentChange;

/** messages from server to client */
public class ServerMessage {
  @TypeId(24322)
  @Flow("Ping")
  public static class PingResponse {
  }

  @TypeId(1018)
  @Flow("Probe")
  public static class ProbeCommandResponse {
    @FieldOrder(1)
    public String json;
    @FieldOrder(2)
    public String[] errors;
  }

  @TypeId(12524)
  @Flow("Creation")
  public static class CreateResponse {
  }

  @TypeId(12526)
  @Flow("Deletion")
  public static class DeleteResponse {
  }

  @TypeId(6736)
  @Flow("Reflection")
  public static class ReflectResponse {
    @FieldOrder(1)
    public String schema;
  }

  @TypeId(8922)
  @Flow("Deployment")
  public static class ScanDeploymentResponse {
  }

  @TypeId(1246)
  @Flow("Metering")
  public static class MeteringBatchFound {
    @FieldOrder(1)
    public String id;
    @FieldOrder(2)
    public String batch;
  }

  @TypeId(1248)
  @Flow("Metering")
  public static class MeteringBatchRemoved {
  }

  @TypeId(12546)
  @Flow("Document")
  public static class StreamStatus {
    @FieldOrder(1)
    public int code;
  }

  @TypeId(10546)
  @Flow("Document")
  public static class StreamData {
    @FieldOrder(1)
    public String delta;
  }

  @TypeId(19546)
  @Flow("Document")
  public static class StreamError {
    @FieldOrder(1)
    public int op;
    @FieldOrder(2)
    public int code;
  }

  @TypeId(15546)
  @Flow("Document")
  public static class StreamAskAttachmentResponse {
    @FieldOrder(1)
    public int op;
    @FieldOrder(2)
    public boolean allowed;
  }

  @TypeId(1632)
  @Flow("Document")
  public static class StreamSeqResponse {
    @FieldOrder(1)
    public int op;
    @FieldOrder(2)
    public int seq;
  }

  @TypeId(5122)
  @Flow("Info")
  public static class HeatPayload {
    @FieldOrder(1)
    public double cpu;
    @FieldOrder(2)
    public double mem;
  }

  @TypeId(7232)
  @Flow("Info")
  public static class InventoryHeartbeat {
    @FieldOrder(1)
    public String[] spaces;
  }

  @TypeId(9002)
  @Flow("ProxyVoidResponse")
  public static class ProxyVoidResponse {
  }

  @TypeId(9004)
  @Flow("ProxyIntResponse")
  public static class ProxyIntResponse {
    @FieldOrder(1)
    public int value;
  }

  @TypeId(9006)
  @Flow("ProxyLocalDataChange")
  public static class ProxyLocalDataChange {
    @FieldOrder(1)
    public String patch;

    @FieldOrder(2)
    public int reads;

    @FieldOrder(3)
    public int seq;

    public static ProxyLocalDataChange copyFrom(LocalDocumentChange change) {
      ProxyLocalDataChange proxy = new ProxyLocalDataChange();
      proxy.patch = change.patch;
      proxy.reads = change.reads;
      proxy.seq = change.seq;
      return proxy;
    }

    public LocalDocumentChange toLocalDocumentChange() {
      return new LocalDocumentChange(patch, reads, seq);
    }
  }

  @TypeId(1721)
  @Flow("Web")
  public static class WebResponseNet { // search commonWebHandle in Handler.java AND commonWebReturn in InstanceClient.java
    @FieldOrder(1)
    public String contentType;
    @FieldOrder(2)
    public String body;
    @FieldOrder(3)
    public String assetId;
    @FieldOrder(4)
    public String assetName;
    @FieldOrder(5)
    public long assetSize;
    @FieldOrder(6)
    public String assetMD5;
    @FieldOrder(7)
    public String assetSHA384;
    @FieldOrder(8)
    public boolean cors;
    @FieldOrder(9)
    public int cache_ttl_seconds;
    @FieldOrder(10)
    public String asset_transform;
  }

  @TypeId(2123)
  @Flow("Auth")
  public static class AuthResponse {
    @FieldOrder(1)
    public String agent;
  }

  @TypeId(2001)
  @Flow("Query")
  public static class QueryResult {
    @FieldOrder(1)
    public String result;
  }

  @TypeId(1783)
  @Flow("Direct")
  public static class DirectSendResponse {
    @FieldOrder(1)
    public int seq;
  }

  @TypeId(10548)
  @Flow("Replica")
  public static class ReplicaData {
    @FieldOrder(1)
    public boolean reset;
    @FieldOrder(2)
    public String change;
  }
}
