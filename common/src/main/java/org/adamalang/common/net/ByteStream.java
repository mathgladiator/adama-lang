/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
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
