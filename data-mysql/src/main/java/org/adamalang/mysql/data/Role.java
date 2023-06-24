/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
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
