/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.runtime.sys;

import org.adamalang.runtime.natives.NtPrincipal;

/** wrap common data around a request for policies to exploit */
public class CoreRequestContext {
  public final NtPrincipal who;
  public final String origin;
  public final String ip;
  public final String key;

  public CoreRequestContext(NtPrincipal who, String origin, String ip, String key) {
    this.who = who;
    this.origin = origin;
    this.ip = ip;
    this.key = key;
  }
}
