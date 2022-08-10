package org.adamalang.common.capacity;

import org.junit.Assert;
import org.junit.Test;

public class BinaryEventOrGateTests {
  @Test
  public void flow() {
    StringBuilder sb = new StringBuilder();
    BinaryEventOrGate gate = new BinaryEventOrGate() {
      @Override
      public void start() {
        sb.append("START");
      }

      @Override
      public void stop() {
        sb.append("STOP");
      }
    };
    gate.a(true);
    gate.b(true);
    gate.a(false);
    gate.b(false);
    gate.a(true);
    gate.b(true);
    gate.a(false);
    gate.b(false);
    Assert.assertEquals("STARTSTOPSTARTSTOP", sb.toString());
  }
}
