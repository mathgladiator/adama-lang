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
package org.adamalang.validators;

import org.adamalang.ErrorCodes;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.translator.parser.token.Tables;

public class ValidateChannel {

  public static void validate(String channel) throws ErrorCodeException {
    char[] arr = channel.toCharArray();
    if (arr.length == 0) {
      throw new ErrorCodeException(ErrorCodes.API_CHANNEL_VALIDATION_FAILED_EMPTY);
    }
    if (!(0 <= arr[0] && arr[0] <= 255 && Tables.START_IDENTIFIER_SCANNER[arr[0]])) {
      throw new ErrorCodeException(ErrorCodes.API_CHANNEL_VALIDATION_BAD_START_CHARACTER);
    }
    for (int k = 1; k < arr.length; k++) {
      if (!(0 <= arr[k] && arr[k] <= 255 && Tables.START_IDENTIFIER_SCANNER[arr[k]])) {
        throw new ErrorCodeException(ErrorCodes.API_CHANNEL_VALIDATION_BAD_MIDDLE_CHARACTER);
      }
    }
  }
}
