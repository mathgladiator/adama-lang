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

/** the backend data service provides a variety of algorithms to execute on the log */
public enum ComputeMethod {
  /** patch the local document to be up to date after the given sequencer */
  HeadPatch(1),
  /** rewind the document to the given sequencer */
  Rewind(2);

  public final int type;

  ComputeMethod(int type) {
    this.type = type;
  }

  public static ComputeMethod fromType(int type) {
    for (ComputeMethod method : ComputeMethod.values()) {
      if (method.type == type) {
        return method;
      }
    }
    return null;
  }
}
