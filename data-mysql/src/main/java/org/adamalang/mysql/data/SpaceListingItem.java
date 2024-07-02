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
package org.adamalang.mysql.data;

public class SpaceListingItem {
  public final String name;
  public final String callerRole;
  public final String created;
  public final boolean enabled;
  public final long storageBytes;

  public SpaceListingItem(String name, String callerRole, String created, boolean enabled, long storageBytes) {
    this.name = name;
    this.callerRole = callerRole;
    this.created = created;
    this.enabled = enabled;
    this.storageBytes = storageBytes;
  }
}
