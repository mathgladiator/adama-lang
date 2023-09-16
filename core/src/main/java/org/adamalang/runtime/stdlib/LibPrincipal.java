/*
* Adama Platform and Language
* Copyright (C) 2021 - 2023 by Adama Platform Initiative, LLC
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as published
* by the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Affero General Public License for more details.
* 
* You should have received a copy of the GNU Affero General Public License
* along with this program.  If not, see <https://www.gnu.org/licenses/>.
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
