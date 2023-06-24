/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.caravan.contracts;

/** for reading a list of byte[] */
public interface ByteArrayStream {

  // a new append was discovered
  void next(int appendIndex, byte[] value, int seq, long assetBytes) throws Exception;

  // no more appends were found
  void finished() throws Exception;
}
