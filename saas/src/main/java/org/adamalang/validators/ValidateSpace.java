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
package org.adamalang.validators;

import org.adamalang.ErrorCodes;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.Validators;

import java.util.HashSet;
import java.util.Locale;

public class ValidateSpace {
  private static HashSet<String> INAPPROPRIATE_SPACE_NAMES = buildInappropriateSpaceNames();

  private static HashSet<String> buildInappropriateSpaceNames() {
    HashSet<String> set = new HashSet<>();
    set.add("CSS");
    set.add("Portal");
    set.add("actuator");
    set.add("api");
    set.add("cgi-bin");
    set.add("docs");
    set.add("ecp");
    set.add("owa");
    set.add("scripts");
    set.add("vendor");
    set.add("portal");
    set.add("remote");
    set.add("d");
    set.add("s");
    set.add("telescope");
    set.add("idx_config");
    set.add("console");
    set.add("mgmt");
    set.add("wp-admin");
    return set;
  }

  public static void validate(String space) throws ErrorCodeException {
    if (space.length() == 0) {
      throw new ErrorCodeException(ErrorCodes.API_INVALID_SPACE_EMPTY);
    }
    if (INAPPROPRIATE_SPACE_NAMES.contains(space) || space.length() < 3) {
      throw new ErrorCodeException(ErrorCodes.API_INVALID_SPACE_INAPPROPRIATE_NAME);
    }
    if (!Validators.simple(space, 127)) {
      throw new ErrorCodeException(ErrorCodes.API_INVALID_SPACE_NOT_SIMPLE);
    }
    if (space.contains(".") || space.contains("_") || space.contains("--")) {
      throw new ErrorCodeException(ErrorCodes.API_INVALID_SPACE_HAS_INVALID_CHARACTER);
    }
  }
}
