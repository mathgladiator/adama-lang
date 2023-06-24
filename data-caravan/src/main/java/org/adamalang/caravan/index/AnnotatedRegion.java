/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.caravan.index;

/** a region of memory with an annotations (i.e. seq) */
public class AnnotatedRegion extends Region {
  public final int seq;
  public final long assetBytes;

  public AnnotatedRegion(long position, int size, int seq, long assetBytes) {
    super(position, size);
    this.seq = seq;
    this.assetBytes = assetBytes;
  }


  @Override
  public String toString() {
    return "[" + position + "," + (position + size) + "=" + seq + (assetBytes > 0 ? (", " + assetBytes) : "") + "]";
  }
}
