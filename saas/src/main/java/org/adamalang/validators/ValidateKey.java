/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.validators;

import org.adamalang.ErrorCodes;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.Validators;

public class ValidateKey {
  public static void validate(String key) throws ErrorCodeException {
    if (key.length() == 0) {
      throw new ErrorCodeException(ErrorCodes.API_INVALID_KEY_EMPTY);
    }
    if (!Validators.simple(key, 511)) {
      throw new ErrorCodeException(ErrorCodes.API_INVALID_KEY_NOT_SIMPLE);
    }
  }
}
