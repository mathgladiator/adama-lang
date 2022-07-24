/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.validators;

import org.adamalang.ErrorCodes;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.Validators;

import java.util.HashSet;

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
    return set;
  }

  public static void validate(String space) throws ErrorCodeException {
    if (INAPPROPRIATE_SPACE_NAMES.contains(space) || space.startsWith(".") || space.contains("..") || space.length() < 3) {
      throw new ErrorCodeException(ErrorCodes.API_INVALID_SPACE_INAPPROPRIATE_NAME);
    }
    if (space.length() == 0) {
      throw new ErrorCodeException(ErrorCodes.API_INVALID_SPACE_EMPTY);
    }
    if (!Validators.simple(space, 127)) {
      throw new ErrorCodeException(ErrorCodes.API_INVALID_SPACE_NOT_SIMPLE);
    }
  }
}
