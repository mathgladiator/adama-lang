package org.adamalang.net.codec;

import org.adamalang.common.codec.FieldOrder;
import org.adamalang.common.codec.Flow;
import org.adamalang.common.codec.TypeId;

/** messages from server to client */
public class ServerMessage {
  @TypeId(24322)
  @Flow("Ping")
  public static class PingResponse {
  }

  @TypeId(12524)
  @Flow("Creation")
  public static class CreateResponse {
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
  @Flow("Metering")
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
    public int code;
  }

  @TypeId(15546)
  @Flow("Document")
  public static class StreamAskAttachmentResponse {
    @FieldOrder(1)
    public boolean allowed;
  }

  @TypeId(1632)
  @Flow("Document")
  public static class StreamSeqResponse {
    @FieldOrder(1)
    public int seq;
  }

  @TypeId(5122)
  @Flow("Heat")
  public static class HeatPayload {
    @FieldOrder(1)
    double cpu;
    @FieldOrder(2)
    double mem;
  }

  @TypeId(7232)
  @Flow("Inventory")
  public static class InventoryHeartbeat {
    @FieldOrder(1)
    String[] spaces;
  }
}
