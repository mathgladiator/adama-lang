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

public class ValidateDomain {
  public static void validate(String domain) throws ErrorCodeException  {
    if (domain.contains("/") || domain.contains("\\")) {
      throw new ErrorCodeException(ErrorCodes.API_DOMAIN_SHOULDNT_CONTAIN_SLASH);
    }
    if (domain.contains(":")) {
      throw new ErrorCodeException(ErrorCodes.API_DOMAIN_SHOULDNT_CONTAIN_COLON);
    }
  }
}
