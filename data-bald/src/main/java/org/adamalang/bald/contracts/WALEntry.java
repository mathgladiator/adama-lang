package org.adamalang.bald.contracts;

import io.netty.buffer.ByteBuf;

public interface WALEntry {

  public void write(ByteBuf buf);
}
