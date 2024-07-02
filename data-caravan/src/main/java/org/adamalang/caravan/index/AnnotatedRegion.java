/*
* Adama Platform and Language
* Copyright (C) 2021 - 2024 by Adama Platform Engineering, LLC
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
