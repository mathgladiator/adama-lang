/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.caravan.index;

/** a region in memory */
public class Region {
  public final long position;
  public final int size;

  public Region(long position, int size) {
    this.position = position;
    this.size = size;
  }

  @Override
  public String toString() {
    return "[" + position + "," + (position + size) + ")";
  }
}
