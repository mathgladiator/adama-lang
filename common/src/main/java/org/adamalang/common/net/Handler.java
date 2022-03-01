package org.adamalang.common.net;

public interface Handler {

  public ByteStream create(ByteStream upstream);
}
