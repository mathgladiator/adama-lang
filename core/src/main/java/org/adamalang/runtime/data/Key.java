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
package org.adamalang.runtime.data;

import java.util.Objects;

/** A document is identified by a key */
public class Key implements Comparable<Key> {
  public final String space;
  public final String key;
  private final int cachedHashCode;

  public Key(String space, String key) {
    this.space = space;
    this.key = key;
    this.cachedHashCode = Math.abs(Objects.hash(space, key));
  }

  @Override
  public int hashCode() {
    return cachedHashCode;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Key key1 = (Key) o;
    return Objects.equals(space, key1.space) && Objects.equals(key, key1.key);
  }

  @Override
  public int compareTo(Key o) {
    if (this == o) return 0;
    int delta = space.compareTo(o.space);
    if (delta == 0) {
      delta = key.compareTo(o.key);
    }
    return delta;
  }
}
