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
import org.adamalang.runtime.data.RemoteDocumentUpdate;
import org.adamalang.runtime.data.UpdateType;
import org.adamalang.runtime.natives.NtPrincipal;

/** messages from client to server */
public class ClientMessage {

  @TypeId(24321)
  @Flow("Server")
  public static class PingRequest {
  }

  @TypeId(9001)
  @Flow("Server")
  public static class FindRequest {
    @FieldOrder(1)
    public String space;
    @FieldOrder(2)
    public String key;
  }

  @TypeId(1017)
  @Flow("Server")
  public static class ProbeCommandRequest {
    @FieldOrder(1)
    public String command;
    @FieldOrder(2)
    public String[] args;
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

  @TypeId(12945)
  @Flow("Server")
  public static class StreamPassword {
    @FieldOrder(1)
    public int op;
    @FieldOrder(2)
    public String password;
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

  @TypeId(12347)
  @Flow("Server")
  public static class ReplicaConnect {
    @FieldOrder(1)
    public String space;
    @FieldOrder(2)
    public String key;
  }

  @TypeId(13337)
  @Flow("Server")
  public static class ReplicaDisconnect {
  }

  @TypeId(2124)
  @Flow("Server")
  public static class Authorize {
    @FieldOrder(1)
    public String space;
    @FieldOrder(2)
    public String key;
    @FieldOrder(3)
    public String username;
    @FieldOrder(4)
    public String password;
    @FieldOrder(5)
    public String ip;
    @FieldOrder(6)
    public String origin;
  }
}
