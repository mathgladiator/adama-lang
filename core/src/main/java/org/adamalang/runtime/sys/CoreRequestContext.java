/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.runtime.sys;

import org.adamalang.runtime.natives.NtClient;

/** wrap common data around a request for policies to exploit */
public class CoreRequestContext {
  public final NtClient who;
  public final String origin;
  public final String ip;
  public final String key;

  public CoreRequestContext(NtClient who, String origin, String ip, String key) {
    this.who = who;
    this.origin = origin;
    this.ip = ip;
    this.key = key;
  }
}
