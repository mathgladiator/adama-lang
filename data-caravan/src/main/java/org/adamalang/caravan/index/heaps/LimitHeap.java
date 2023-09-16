/*
* Adama Platform and Language
* Copyright (C) 2021 - 2023 by Adama Platform Initiative, LLC
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as published
* by the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Affero General Public License for more details.
* 
* You should have received a copy of the GNU Affero General Public License
* along with this program.  If not, see <https://www.gnu.org/licenses/>.
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
