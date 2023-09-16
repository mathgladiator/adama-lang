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
package org.adamalang.caravan.index;

import io.netty.buffer.ByteBuf;

/** a very simple doubly-linked heap */
public interface Heap {
  /** how many bytes are available to allocate */
  long available();

  /** how many total bytes can this heap allocate */
  long max();

  /** ask the heap for a region of memory of the given size */
  Region ask(int size);

  /** free the given region */
  void free(Region region);

  /** take a snapshot of heap to the given byte buffer */
  void snapshot(ByteBuf buf);

  /** load the heap state from the given byte buffer */
  void load(ByteBuf buf);

  /** report on the heap */
  void report(Report report);
}
