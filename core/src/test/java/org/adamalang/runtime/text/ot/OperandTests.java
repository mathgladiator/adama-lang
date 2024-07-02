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
