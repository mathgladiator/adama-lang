/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
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
