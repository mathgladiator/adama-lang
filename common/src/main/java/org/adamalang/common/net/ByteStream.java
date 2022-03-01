package org.adamalang.common.net;

import io.netty.buffer.ByteBuf;

/** contract for sending/getting bytes; this requires some reading as there are caveats */
public interface ByteStream {
  /** request some bytes from the remote side. This is a hint for application level flow control. */
  public void request(int bytes);

  /** create is required to be called to create the buffer that you provide next */
  public ByteBuf create(int bestGuessForSize);

  /** send the buffer created by create(). This oddity exists precisey because we need create to prepend some information before you fill in the body */
  public void next(ByteBuf buf);

  /** the stream is complete */
  public void completed();

  /** the stream produced an error */
  public void error(int errorCode);
}
