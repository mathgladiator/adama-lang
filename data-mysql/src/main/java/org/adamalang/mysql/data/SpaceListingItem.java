/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.mysql.data;

public class SpaceListingItem {
  public final String name;
  public final String callerRole;
  public final String created;
  public final boolean enabled;
  public final long storageBytes;

  public SpaceListingItem(String name, String callerRole, String created, boolean enabled, long storageBytes) {
    this.name = name;
    this.callerRole = callerRole;
    this.created = created;
    this.enabled = enabled;
    this.storageBytes = storageBytes;
  }
}
