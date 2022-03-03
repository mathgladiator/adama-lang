package org.adamalang.common.net;

/** handler to create a stream */
public interface Handler {

  /** create a stream */
  public ByteStream create(ByteStream upstream);
}
