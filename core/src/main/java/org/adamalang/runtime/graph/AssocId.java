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
package org.adamalang.runtime.graph;

import java.util.Objects;

/** an association identifier (i.e. tuple of id and an assoc) */
class AssocId {
  public final int id;
  public final short assoc;

  public AssocId(int id, short assoc) {
    this.id = id;
    this.assoc = assoc;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    AssocId assocId = (AssocId) o;
    return id == assocId.id && assoc == assocId.assoc;
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, assoc);
  }
}
