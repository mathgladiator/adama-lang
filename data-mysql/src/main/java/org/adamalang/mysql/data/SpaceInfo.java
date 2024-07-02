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

import java.util.Set;

/** information about a space */
public class SpaceInfo {
  public final int id;
  public final int owner;
  public final Set<Integer> developers;
  public final boolean enabled;
  public final long storageBytes;
  public final String policy;

  public SpaceInfo(int id, int owner, Set<Integer> developers, boolean enabled, long storageBytes, String policy) {
    this.id = id;
    this.owner = owner;
    this.developers = developers;
    this.enabled = enabled;
    this.storageBytes = storageBytes;
    this.policy = policy == null ? "{}" : policy;
  }
}
