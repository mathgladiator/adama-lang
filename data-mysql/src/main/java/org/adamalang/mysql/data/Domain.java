/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's Apache2); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
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
  public final String certificate;
  public final Date updated;
  public final long timestamp;

  public Domain(String domain, int owner, String space, String certificate, Date updated, long timestamp) {
    this.domain = domain;
    this.owner = owner;
    this.space = space;
    this.certificate = certificate;
    this.updated = updated;
    this.timestamp = timestamp;
  }
}
