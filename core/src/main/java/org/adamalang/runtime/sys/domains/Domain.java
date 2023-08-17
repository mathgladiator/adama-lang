/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.runtime.sys.domains;

import org.adamalang.common.cache.Measurable;

import java.sql.Date;

/** a domain mapped to a space */
public class Domain implements Measurable {
  public final String domain;
  public final int owner;
  public final String space;
  public final String key;
  public final boolean routeKey;
  public final String certificate;
  public final Date updated;
  public final long timestamp;

  private final long _measure;

  public Domain(String domain, int owner, String space, String key, boolean routeKey, String certificate, Date updated, long timestamp) {
    this.domain = domain;
    this.owner = owner;
    this.space = space;
    this.routeKey = routeKey;
    this.key = key;
    this.certificate = certificate;
    this.updated = updated;
    this.timestamp = timestamp;
    _measure = domain.length() + space.length() + key.length() + certificate.length() + 64;;
  }

  @Override
  public long measure() {
    return _measure;
  }
}
