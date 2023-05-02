/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
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
