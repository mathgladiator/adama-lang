/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
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
