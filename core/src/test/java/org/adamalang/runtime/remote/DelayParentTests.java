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
package org.adamalang.runtime.remote;

import org.adamalang.runtime.contracts.DelayParent;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;

public class DelayParentTests {
  @Test
  public void flow_a() {
    AtomicBoolean success = new AtomicBoolean(false);
    DelayParent dp = new DelayParent();
    dp.__raiseDirty();
    dp.bind(() -> success.set(true));
    dp.__cost(100);
    dp.__settle(null);
    Assert.assertTrue(success.get());
    Assert.assertTrue(dp.__isAlive());
  }

  @Test
  public void flow_b() {
    AtomicBoolean success = new AtomicBoolean(false);
    DelayParent dp = new DelayParent();
    dp.bind(() -> success.set(true));
    dp.__cost(100);
    dp.__settle(null);
    dp.__raiseDirty();
    Assert.assertTrue(success.get());
    Assert.assertTrue(dp.__isAlive());
  }
}
