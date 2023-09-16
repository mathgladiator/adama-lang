/*
* Adama Platform and Language
* Copyright (C) 2021 - 2023 by Adama Platform Initiative, LLC
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as published
* by the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Affero General Public License for more details.
* 
* You should have received a copy of the GNU Affero General Public License
* along with this program.  If not, see <https://www.gnu.org/licenses/>.
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
    long m = 0;
    if (domain != null) {
      m += domain.length();
    }
    if (space != null) {
      m += space.length();
    }
    if (key != null) {
      m += key.length();
    }
    if (certificate != null) {
      m += certificate.length();
    }
    _measure = m;
  }

  @Override
  public long measure() {
    return _measure;
  }
}
