/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.transforms.results;

import org.adamalang.runtime.natives.NtClient;

public class AuthenticatedUser {
  public final Source source;
  public final int id;
  public final NtClient who;

  public AuthenticatedUser(Source source, int id, NtClient who) {
    this.source = source;
    this.id = id;
    this.who = who;
  }

  public enum Source {
    Social,
    Adama,
    Authority,
  }
}
