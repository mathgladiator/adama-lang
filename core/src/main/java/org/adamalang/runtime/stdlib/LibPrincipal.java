/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
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
  public static boolean isOverlord(final NtPrincipal principal) {
    return "overlord".equals(principal.authority);
  }

  @Extension
  public static boolean isAdamaHost(final NtPrincipal principal) {
    return "region".equals(principal.authority);
  }

  @Extension
  public static boolean fromAuthority(final NtPrincipal principal, String authority) {
    return authority.equals(principal.authority);
  }

  @Extension
  public static String agent(final NtPrincipal principal) {
    return principal.agent;
  }

  @Extension
  public static String authority(final NtPrincipal principal) {
    return principal.authority;
  }
}
