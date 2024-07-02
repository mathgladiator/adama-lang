/*
* Adama Platform and Language
* Copyright (C) 2021 - 2024 by Adama Platform Engineering, LLC
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
package org.adamalang.auth;

import org.adamalang.web.io.ConnectionContext;

import java.util.Objects;

/** bundled together an identity along with a context */
public class AuthRequest implements Comparable<AuthRequest> {
  public final String identity;
  public final ConnectionContext context;

  public AuthRequest(String identity, ConnectionContext context) {
    this.identity = identity;
    this.context = context;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    AuthRequest that = (AuthRequest) o;
    return Objects.equals(identity, that.identity) && Objects.equals(context, that.context);
  }

  @Override
  public int hashCode() {
    return Objects.hash(identity, context);
  }

  @Override
  public int compareTo(AuthRequest o) {
    return identity.compareTo(o.identity);
  }
}
