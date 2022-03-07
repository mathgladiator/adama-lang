package org.adamalang.disk.wal;

import org.adamalang.common.codec.FieldOrder;
import org.adamalang.common.codec.TypeId;

public interface WriteAheadMessage {

  @TypeId(0x05)
  public static class Initialize implements WriteAheadMessage {
    @FieldOrder(1)
    public String space;
    @FieldOrder(2)
    public String key;
  }

  @TypeId(0x10)
  public static class Compact implements WriteAheadMessage {
    @FieldOrder(1)
    public String space;
    @FieldOrder(2)
    public String key;
    @FieldOrder(3)
    public int history;
  }

  @TypeId(0x15)
  public static class Change implements WriteAheadMessage {
    @FieldOrder(1)
    public int seq_begin;
    @FieldOrder(2)
    public int seq_end;
    @FieldOrder(3)
    public String request;
    @FieldOrder(4)
    public String redo;
    @FieldOrder(5)
    public String undo;
  }

  @TypeId(0x20)
  public static class Patch implements WriteAheadMessage {
    @FieldOrder(1)
    public String space;
    @FieldOrder(2)
    public String key;
  }

}
