/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.runtime.natives;

/** represents who someone is */
public class NtClient implements Comparable<NtClient> {
  public static NtClient NO_ONE = new NtClient("?", "?");
  public final String agent;
  public final String authority;
  private final int cachedHash;

  public NtClient(final String agent, final String authority) {
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
    if (o instanceof NtClient) {
      NtClient other = (NtClient) o;
      return other == this ? true : (other.cachedHash == cachedHash ? compareTo((NtClient) o) == 0 : false);
    }
    return false;
  }

  @Override
  public int compareTo(final NtClient other) {
    var result = authority.compareTo(other.authority);
    if (result == 0) {
      result = agent.compareTo(other.agent);
    }
    return result;
  }

  @Override
  public String toString() {
    return "CLIENT<" + agent + "@" + authority + ">";
  }

  public long memory() {
    return (agent.length() + authority.length()) * 2;
  }
}
