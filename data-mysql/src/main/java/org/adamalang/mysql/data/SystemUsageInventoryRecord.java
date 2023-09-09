/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.mysql.data;

/** a record of things in the system that have an hourly cost */
public class SystemUsageInventoryRecord {
  public final int domains;
  public final int authorities;

  public SystemUsageInventoryRecord(int domains, int authorities) {
    this.domains = domains;
    this.authorities = authorities;
  }
}
