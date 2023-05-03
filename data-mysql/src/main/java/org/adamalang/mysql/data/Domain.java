/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.mysql.data;

import java.sql.Date;

/** a domain mapped to a space */
public class Domain {
  public final String domain;
  public final int owner;
  public final String space;
  public final String key;
  public final String certificate;
  public final Date updated;
  public final long timestamp;

  public Domain(String domain, int owner, String space, String key, String certificate, Date updated, long timestamp) {
    this.domain = domain;
    this.owner = owner;
    this.space = space;
    this.key = key;
    this.certificate = certificate;
    this.updated = updated;
    this.timestamp = timestamp;
  }
}
