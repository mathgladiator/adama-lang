/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.runtime.sys.web;

import org.adamalang.runtime.natives.NtPrincipal;

public class WebContext {
  public final NtPrincipal who;
  public final String origin;
  public final String ip;

  public WebContext(NtPrincipal who, String origin, String ip) {
    this.who = who;
    this.origin = origin;
    this.ip = ip;
  }
}
