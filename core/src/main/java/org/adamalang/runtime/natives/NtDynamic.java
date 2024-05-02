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
package org.adamalang.runtime.natives;

import org.adamalang.runtime.json.JsonStreamReader;

/** a native data type to hide and hold an entire json tree */
public class NtDynamic implements Comparable<NtDynamic>, NtToDynamic {
  public static final NtDynamic NULL = new NtDynamic("null");
  public final String json;
  private Object cached;

  public NtDynamic(String json) {
    this.json = json;
    this.cached = null;
  }

  public Object cached() {
    if (cached != null) {
      return cached;
    }
    cached = new JsonStreamReader(json).readJavaTree();
    return cached;
  }

  public NtJson to_json() {
    cached = null;
    return new NtJson(cached());
  }

  @Override
  public NtDynamic to_dynamic() {
    return this;
  }

  @Override
  public int compareTo(final NtDynamic other) {
    return json.compareTo(other.json);
  }

  @Override
  public int hashCode() {
    return json.hashCode();
  }

  @Override
  public boolean equals(final Object o) {
    if (o instanceof NtDynamic) {
      return ((NtDynamic) o).json.equals(json);
    }
    return false;
  }

  @Override
  public String toString() {
    return json;
  }

  public long memory() {
    return json.length() * 2L;
  }
}
