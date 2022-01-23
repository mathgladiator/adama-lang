package org.adamalang.validators;

import org.adamalang.ErrorCodes;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.Validators;

public class ValidateSpace {
  public static void validate(String space) throws ErrorCodeException {
    if (space.length() == 0) {
      throw new ErrorCodeException(ErrorCodes.API_INVALID_SPACE_EMPTY);
    }
    if (!Validators.simple(space, 127)) {
      throw new ErrorCodeException(ErrorCodes.API_INVALID_SPACE_NOT_SIMPLE);
    }
  }
}
