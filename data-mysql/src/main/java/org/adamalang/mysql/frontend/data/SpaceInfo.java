/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.mysql.frontend.data;

import java.util.Set;

public class SpaceInfo {
  public final int id;
  public final int owner;
  public final String billing;
  public final Set<Integer> developers;
  public final int balance;

  public SpaceInfo(int id, int owner, String billing, Set<Integer> developers, int balance) {
    this.id = id;
    this.owner = owner;
    this.billing = billing;
    this.developers = developers;
    this.balance = balance;
  }
}
