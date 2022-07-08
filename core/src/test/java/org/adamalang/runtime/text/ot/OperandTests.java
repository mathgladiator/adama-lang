/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.runtime.text.ot;

import org.junit.Assert;
import org.junit.Test;

public class OperandTests {
  @Test
  public void flow() {
    Operand flow = new Raw("/* adama */");
    flow = Operand.apply(flow, "[{\"clientID\":\"dzg02a\",\"changes\":[11,[0,\"x\"]]}]");
    Assert.assertEquals("/* adama */x", flow.get());
    flow = Operand.apply(flow, "[{\"clientID\":\"dzg02a\",\"changes\":[[0,\"z\"],12]}]");
    Assert.assertEquals("z/* adama */x", flow.get());
    flow = Operand.apply(flow, "[{\"clientID\":\"dzg02a\",\"changes\":[9,[0,\" adama\"],4]}]");
    Assert.assertEquals("z/* adama adama */x", flow.get());
    flow = Operand.apply(flow, "[{\"clientID\":\"dzg02a\",\"changes\":[4,[11],4]}]");
    Assert.assertEquals("z/*  */x", flow.get());
  }
}
