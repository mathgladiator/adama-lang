/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.caravan.index.heaps;

import io.netty.buffer.ByteBuf;
import org.adamalang.caravan.index.Heap;
import org.adamalang.caravan.index.Region;
import org.adamalang.caravan.index.Report;

/** a passthrough heap which ensures everything allocated from this heap is under a specific size */
public class LimitHeap implements Heap {

  private final Heap parent;
  private final int sizeLimit;

  public LimitHeap(Heap parent, int sizeLimit) {
    this.parent = parent;
    this.sizeLimit = sizeLimit;
  }

  @Override
  public void report(Report report) {
    parent.report(report);
  }

  @Override
  public long available() {
    return parent.available();
  }

  @Override
  public long max() {
    return parent.max();
  }

  @Override
  public Region ask(int size) {
    if (size > sizeLimit) {
      return null;
    }
    return parent.ask(size);
  }

  @Override
  public void free(Region region) {
    parent.free(region);
  }

  @Override
  public void snapshot(ByteBuf buf) {
    parent.snapshot(buf);
  }

  @Override
  public void load(ByteBuf buf) {
    parent.load(buf);
  }

  @Override
  public String toString() {
    return parent.toString();
  }
}
