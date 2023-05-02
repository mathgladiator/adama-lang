/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's Apache2); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.caravan.data;

import org.adamalang.caravan.index.Region;

import java.io.IOException;

/** simple interface for reading and writing bytes from a storage device (synchronous) */
public interface Storage {

  /** how much is allocated */
  long size();

  /** write some bytes to the given region */
  void write(Region region, byte[] mem);

  /** read a byte array from the given region */
  byte[] read(Region region);

  /** flush all writes to disk */
  void flush() throws IOException;

  /** close the storage */
  void close() throws IOException;
}
