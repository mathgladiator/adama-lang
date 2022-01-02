package org.adamalang.grpc.client.routing;

import org.adamalang.grpc.client.contracts.SpaceTrackingEvents;
import org.junit.Assert;

import java.util.Set;
import java.util.TreeSet;

public class MockSpaceTrackingEvents implements SpaceTrackingEvents {
  StringBuilder sb;

  public MockSpaceTrackingEvents() {
    sb = new StringBuilder();
  }
  @Override
  public void gainInterestInSpace(String space) {
    sb.append("[GAIN:" + space + "]");
  }

  @Override
  public void shareTargetsFor(String space, Set<String> targets) {
    sb.append("[SHARE:" + space + "=");
    boolean first = true;
    for (String e : new TreeSet<>(targets)) {
      if (!first) {
        sb.append(",");
      }
      first = false;
      sb.append(e);
    }
    sb.append("]");
  }

  @Override
  public void lostInterestInSpace(String space) {
    sb.append("[LOST:" + space + "]");
  }

  public void assertHistory(String expected) {
    Assert.assertEquals(expected, sb.toString());
  }
}
