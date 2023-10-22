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
package org.adamalang.runtime.reactives.tables;

import org.adamalang.runtime.mocks.MockRxParent;
import org.junit.Assert;
import org.junit.Test;

public class TablePubSubTests {
  @Test
  public void flow() {
    MockRxParent par = new MockRxParent();
    TablePubSub pubsub = new TablePubSub(par);
    MockTableSubscription one = new MockTableSubscription();
    MockTableSubscription two = new MockTableSubscription();
    pubsub.subscribe(one);
    pubsub.subscribe(two);
    pubsub.gc();
    pubsub.primary(123);
    pubsub.index(13, 69);
    one.alive = false;
    pubsub.gc();
    pubsub.primary(123);
    pubsub.index( 13, 69);
    Assert.assertTrue(pubsub.alive());
    par.alive = false;
    Assert.assertFalse(pubsub.alive());
    Assert.assertEquals(2, one.publishes.size());
    Assert.assertEquals(4, two.publishes.size());
    Assert.assertEquals("PKEY:123", one.publishes.get(0));
    Assert.assertEquals("IDX:13=69", one.publishes.get(1));
    Assert.assertEquals("PKEY:123", two.publishes.get(0));
    Assert.assertEquals("IDX:13=69", two.publishes.get(1));
    Assert.assertEquals("PKEY:123", two.publishes.get(2));
    Assert.assertEquals("IDX:13=69", two.publishes.get(3));
  }
}
