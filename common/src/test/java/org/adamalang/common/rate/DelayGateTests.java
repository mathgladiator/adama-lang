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
package org.adamalang.common.rate;

import org.adamalang.common.gossip.MockTime;
import org.junit.Assert;
import org.junit.Test;

public class DelayGateTests {
  @Test
  public void coverage() {
    MockTime time = new MockTime();
    time.currentTime = 10000;
    DelayGate gate = new DelayGate(time, 1000);
    Assert.assertTrue(gate.test());
    Assert.assertFalse(gate.test());
    time.currentTime += 500;
    Assert.assertFalse(gate.test());
    time.currentTime += 499;
    Assert.assertFalse(gate.test());
    time.currentTime += 1;
    Assert.assertTrue(gate.test());
    time.currentTime += 999;
    Assert.assertFalse(gate.test());
    time.currentTime += 1;
    Assert.assertTrue(gate.test());
  }
}
