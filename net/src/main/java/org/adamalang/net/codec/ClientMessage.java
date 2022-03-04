/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.net.codec;

import org.adamalang.common.codec.FieldOrder;
import org.adamalang.common.codec.Flow;
import org.adamalang.common.codec.TypeId;

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
  public static class ScanDeploymentRequest {
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
  }

  @TypeId(13345)
  @Flow("Server")
  public static class StreamSend {
    @FieldOrder(1)
    public String channel;
    @FieldOrder(2)
    public String marker;
    @FieldOrder(3)
    public String message;
  }

  @TypeId(14345)
  @Flow("Server")
  public static class StreamUpdate {
    @FieldOrder(1)
    public String viewerState;
  }

  @TypeId(15345)
  @Flow("Server")
  public static class StreamAskAttachmentRequest {
  }

  @TypeId(16345)
  @Flow("Server")
  public static class StreamAttach {
    @FieldOrder(1)
    public String id;

    @FieldOrder(2)
    public String filename;

    @FieldOrder(3)
    public String contentType;

    @FieldOrder(4)
    public long size;

    @FieldOrder(5)
    public String md5;

    @FieldOrder(6)
    public String sha384;
  }

  @TypeId(17345)
  @Flow("Server")
  public static class StreamDisconnect {
  }

  @TypeId(1919)
  @Flow("Server")
  public static class RequestHeat {
  }

  @TypeId(7231)
  @Flow("Server")
  public static class RequestInventoryHeartbeat {
  }
}
