/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.runtime.stdlib;

import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.translator.reflect.Extension;

/** Helpful functions around NtPrincipal */
public class LibPrincipal {
  @Extension
  public static boolean isAdamaDeveloper(final NtPrincipal principal) {
    return "adama".equals(principal.authority);
  }

  @Extension
  public static boolean isAnonymous(final NtPrincipal principal) {
    return "anonymous".equals(principal.authority);
  }

  @Extension
  public static boolean fromAuthority(final NtPrincipal principal, String authority) {
    return authority.equals(principal.authority);
  }
}
