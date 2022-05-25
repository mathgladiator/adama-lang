/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.caravan.index.heaps;

import io.netty.buffer.ByteBuf;
import org.adamalang.caravan.index.Heap;
import org.adamalang.caravan.index.Region;

public class SplitHeat implements Heap {
  private final Heap left;
  private final int leftSizeMustBeUnder;
  private final long rightOffset;
  private final Heap right;

  public SplitHeat(Heap left, int leftSizeMustBeUnder, long rightOffset, Heap right) {
    this.left = left;
    this.leftSizeMustBeUnder = leftSizeMustBeUnder;
    this.rightOffset = rightOffset;
    this.right = right;
  }

  @Override
  public Region ask(int size) {
    Region result;
    if (size < leftSizeMustBeUnder) {
      result = left.ask(size);
      if (result != null) {
        return result;
      }
    }
    Region r = right.ask(size);
    if (r != null) {
      return new Region(r.position + rightOffset, r.size);
    }
    return null;
  }

  @Override
  public long available() {
    return left.available() + right.available();
  }

  @Override
  public void free(Region region) {
    if (region.position < rightOffset) {
      left.free(region);
    } else {
      right.free(new Region(region.position - rightOffset, region.size));
    }
  }

  @Override
  public void snapshot(ByteBuf buf) {
    left.snapshot(buf);
    right.snapshot(buf);
  }

  @Override
  public void load(ByteBuf buf) {
    left.load(buf);
    right.load(buf);
  }

  @Override
  public String toString() {
    return "Split{" + "left=" + left + ",+offset" + rightOffset + ":right="  + right + '}';
  }
}
