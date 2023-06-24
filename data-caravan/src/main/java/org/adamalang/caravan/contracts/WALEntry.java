/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.caravan.contracts;

import io.netty.buffer.ByteBuf;

/** an entry within the write ahead log */
public interface WALEntry<T> {

  /** write a single buffer */
  void write(ByteBuf buf);
}
