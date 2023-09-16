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
package org.adamalang.runtime.data;

/** the type of a document location (on a machine or in the cloud) */
public enum LocationType {
  // a single machine
  Machine(2),

  // an archive
  Archive(4);

  public final int type;

  LocationType(int type) {
    this.type = type;
  }

  public static LocationType fromType(int type) {
    for (LocationType location : LocationType.values()) {
      if (location.type == type) {
        return location;
      }
    }
    return null;
  }
}
