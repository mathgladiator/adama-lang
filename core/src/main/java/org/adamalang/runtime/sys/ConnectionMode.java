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
package org.adamalang.runtime.sys;

/** simplify the way documents are connected */
public enum ConnectionMode {
  // connect for read and write
  Full(true, true, 3),
  // connect for write only (another machine/thread may read)
  WriteOnly(false, true, 2),
  // connect for read only
  ReadOnly(true, false, 1);

  public final boolean read;
  public final boolean write;
  public final int asInt;

  private ConnectionMode(boolean r, boolean w, int asInt) {
    this.read = r;
    this.write = w;
    this.asInt = asInt;
  }

  public static ConnectionMode from(int val) {
    for (ConnectionMode cm : ConnectionMode.values()) {
      if (cm.asInt == val) {
        return cm;
      }
    }
    return ConnectionMode.Full;
  }
}
