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
package org.adamalang.runtime.natives;

/** represents who someone is */
public class NtPrincipal implements Comparable<NtPrincipal> {
  public static NtPrincipal NO_ONE = new NtPrincipal("?", "?");
  public final String agent;
  public final String authority;
  private final int cachedHash;

  public NtPrincipal(final String agent, final String authority) {
    this.agent = agent == null ? "?" : agent;
    this.authority = authority == null ? "?" : authority;
    this.cachedHash = agent.hashCode() * 31 + authority.hashCode();
  }

  @Override
  public int hashCode() {
    return cachedHash;
  }

  @Override
  public boolean equals(final Object o) {
    if (o instanceof NtPrincipal) {
      NtPrincipal other = (NtPrincipal) o;
      return other == this || (other.cachedHash == cachedHash && compareTo((NtPrincipal) o) == 0);
    }
    return false;
  }

  @Override
  public int compareTo(final NtPrincipal other) {
    var result = authority.compareTo(other.authority);
    if (result == 0) {
      result = agent.compareTo(other.agent);
    }
    return result;
  }

  @Override
  public String toString() {
    return "NtPrincipal<" + agent + "@" + authority + ">";
  }

  public long memory() {
    return (agent.length() + authority.length()) * 2L;
  }
}
