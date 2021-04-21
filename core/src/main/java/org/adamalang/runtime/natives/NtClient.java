/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * which is in the root directory of the repository. This file is part of the 'Adama'
 * project which is a programming language and document store for board games.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2021 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.runtime.natives;

/** represents who someone is */
public class NtClient implements Comparable<NtClient> {
  public static NtClient NO_ONE = new NtClient("?", "?");
  public final String agent;
  public final String authority;

  public NtClient(final String agent, final String authority) {
    this.agent = agent == null ? "?" : agent;
    this.authority = authority == null ? "?" : authority;
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
  public boolean equals(final Object o) {
    if (o instanceof NtClient) { return compareTo((NtClient) o) == 0; }
    return false;
  }

  @Override
  public int hashCode() {
    return agent.hashCode() * 31 + authority.hashCode();
  }

  @Override
  public String toString() {
    return "CLIENT<" + agent + "@" + authority + ">";
  }
}
