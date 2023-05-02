/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's Apache2); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
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
