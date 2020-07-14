/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.natives;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/** represents who someone is */
public class NtClient implements Comparable<NtClient> {
  public static NtClient NO_ONE = new NtClient("?", "?");

  public static NtClient from(final JsonNode node) {
    if (node == null) { return NtClient.NO_ONE; }
    if (node.isObject()) {
      final var data = (ObjectNode) node;
      return new NtClient(data.get("agent").textValue(), data.get("authority").textValue());
    }
    return NtClient.NO_ONE;
  }

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

  public void dump(final ObjectNode node) {
    node.put("agent", agent);
    node.put("authority", authority);
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
