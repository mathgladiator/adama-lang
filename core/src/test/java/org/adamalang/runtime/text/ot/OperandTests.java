/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.runtime.text.ot;

import org.junit.Assert;
import org.junit.Test;

public class OperandTests {
  @Test
  public void flow() {
    Operand flow = new Raw("/* adama */");
    flow = Operand.apply(flow, "{\"clientID\":\"dzg02a\",\"changes\":[11,[0,\"x\"]]}");
    Assert.assertEquals("/* adama */x", flow.get());
    flow = Operand.apply(flow, "{\"clientID\":\"dzg02a\",\"changes\":[[0,\"z\"],12]}");
    Assert.assertEquals("z/* adama */x", flow.get());
    flow = Operand.apply(flow, "{\"clientID\":\"dzg02a\",\"changes\":[9,[0,\" adama\"],4]}");
    Assert.assertEquals("z/* adama adama */x", flow.get());
    flow = Operand.apply(flow, "{\"clientID\":\"dzg02a\",\"changes\":[4,[11],4]}");
    Assert.assertEquals("z/*  */x", flow.get());
    flow = Operand.apply(flow, "null");
    Assert.assertEquals("z/*  */x", flow.get());
  }

  @Test
  public void flow2() {
    Operand flow = new Raw("hello world");
    flow = Operand.apply(flow, "{\"clientID\":\"9ajsif\",\"changes\":[6,[0,\"X\",\"Y\",\"Z\"],5]}");
    Assert.assertEquals("hello X\nY\nZworld", flow.get());
    Assert.assertEquals(16, flow.length());
    flow = Operand.apply(flow, "{\"clientID\":\"9ajsif\",\"changes\":[7,[0,\"\",\"\"],9]}");
    Assert.assertEquals("hello X\n\nY\nZworld", flow.get());
    Assert.assertEquals(17, flow.length());
    flow = Operand.apply(flow, "{\"clientID\":\"9ajsif\",\"changes\":[14,[0,\"\",\" \"],3]}");
    Assert.assertEquals("hello X\n\nY\nZwo\n rld", flow.get());
    Assert.assertEquals(19, flow.length());
  }
}
