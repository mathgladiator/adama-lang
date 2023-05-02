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
