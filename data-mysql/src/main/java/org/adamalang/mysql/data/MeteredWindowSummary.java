/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.mysql.data;

/** the resources metered within an hour and their associated user cost */
public class MeteredWindowSummary {
  public String resources;
  public int pennies;
  public long storageBytes;
  public UnbilledResources changeUnbilled;

  public MeteredWindowSummary(String resources, int pennies, long storageBytes, UnbilledResources changeUnbilled) {
    this.resources = resources;
    this.pennies = pennies;
    this.storageBytes = storageBytes;
    this.changeUnbilled = changeUnbilled;
  }
}
