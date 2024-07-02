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
package org.adamalang.runtime.deploy;

import java.util.Objects;

/** a simple key for the required inputs for byte code compiler */
public class ByteCodeKey implements Comparable<ByteCodeKey> {
  public final String spaceName;
  public final String className;
  public final String javaSource;
  public final String reflection;

  public ByteCodeKey(final String spaceName, final String className, final String javaSource, String reflection) {
    this.spaceName = spaceName;
    this.className = className;
    this.javaSource = javaSource;
    this.reflection = reflection;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ByteCodeKey that = (ByteCodeKey) o;
    return Objects.equals(spaceName, that.spaceName) && Objects.equals(className, that.className) && Objects.equals(javaSource, that.javaSource) && Objects.equals(reflection, that.reflection);
  }

  @Override
  public int hashCode() {
    return Objects.hash(spaceName, className, javaSource, reflection);
  }

  @Override
  public int compareTo(ByteCodeKey o) {
    int delta = spaceName.compareTo(o.spaceName);
    if (delta == 0) {
      delta = className.compareTo(o.className);
    }
    if (delta == 0) {
      delta = javaSource.compareTo(o.javaSource);
    }
    if (delta == 0) {
      delta = reflection.compareTo(o.reflection);
    }
    return delta;
  }
}
