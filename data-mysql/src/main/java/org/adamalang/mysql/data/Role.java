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

import org.adamalang.ErrorCodes;
import org.adamalang.common.ErrorCodeException;

import java.util.Locale;

public enum Role {
  None(0x00), Developer(0x01);

  public int role;

  Role(int role) {
    this.role = role;
  }

  public static Role from(int x) throws ErrorCodeException {
    for (Role r : Role.values()) {
      if (x == r.role) {
        return r;
      }
    }
    throw new ErrorCodeException(ErrorCodes.INVALID_ROLE);
  }

  public static Role from(String x) throws ErrorCodeException {
    switch (x.toLowerCase(Locale.ROOT).trim()) {
      case "developer":
        return Developer;
      case "none":
        return None;
    }
    throw new ErrorCodeException(ErrorCodes.INVALID_ROLE);
  }
}
