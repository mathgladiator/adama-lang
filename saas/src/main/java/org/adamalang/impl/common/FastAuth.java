/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.impl.common;

import org.adamalang.common.Callback;
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.transforms.results.AuthenticatedUser;
import org.adamalang.web.io.ConnectionContext;

/** the ultra fast and internal auth aspects (shared between global and region) */
public class FastAuth {
  public static boolean process(String identity, Callback<AuthenticatedUser> callback, ConnectionContext context) {
    if (identity.startsWith("anonymous:")) {
      String agent = identity.substring("anonymous:".length());
      callback.success(new AuthenticatedUser(-1, new NtPrincipal(agent, "anonymous"), context));
      return true;
    }
    return false;
  }
}
