package org.adamalang.caravan.index;

import io.netty.buffer.ByteBuf;

/** a very simple doubly-linked heap */
public interface Heap {
  Region ask(int size);

  void free(Region region);

  void snapshot(ByteBuf buf);

  void load(ByteBuf buf);
}
