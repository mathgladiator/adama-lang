package org.adamalang.caravan.contracts;

import io.netty.buffer.ByteBuf;

/** an entry within the write ahead log */
public interface WALEntry<T> {

  /** write a single buffer */
  public void write(ByteBuf buf);
}
