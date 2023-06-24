/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.common.net;

import io.netty.buffer.ByteBuf;

/** contract for sending/getting bytes; this requires some reading as there are caveats */
public interface ByteStream {
  /** request some bytes from the remote side. This is a hint for application level flow control. */
  void request(int bytes);

  /** create is required to be called to create the buffer that you provide next */
  ByteBuf create(int bestGuessForSize);

  /** send the buffer created by create(). This oddity exists precisey because we need create to prepend some information before you fill in the body */
  void next(ByteBuf buf);

  /** the stream is complete */
  void completed();

  /** the stream produced an error */
  void error(int errorCode);
}
