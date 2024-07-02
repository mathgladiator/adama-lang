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
package org.adamalang.runtime.reactives.maps;

import org.adamalang.runtime.mocks.MockRxParent;
import org.junit.Assert;
import org.junit.Test;

public class MapPubSubTests {
  @Test
  public void flow() {
    MockRxParent par = new MockRxParent();
    MapPubSub<String> pubsub = new MapPubSub<>(par);
    MockMapSubscription one = new MockMapSubscription();
    MockMapSubscription two = new MockMapSubscription();
    pubsub.subscribe(one);
    pubsub.subscribe(two);
    pubsub.changed("X");
    pubsub.changed("X");
    pubsub.changed("X");
    pubsub.changed("X");
    pubsub.changed("X");
    pubsub.gc();
    pubsub.changed("Y");
    two.alive = false;
    Assert.assertEquals(2, pubsub.count());
    pubsub.gc();
    Assert.assertEquals(1, pubsub.count());
    pubsub.changed("Z");
    one.alive = false;
    pubsub.gc();
    Assert.assertEquals(0, pubsub.count());
    Assert.assertEquals(3, one.publishes.size());
    Assert.assertEquals(2, two.publishes.size());
    Assert.assertEquals("CHANGE:X", one.publishes.get(0));
    Assert.assertEquals("CHANGE:Y", one.publishes.get(1));
    Assert.assertEquals("CHANGE:Z", one.publishes.get(2));
    Assert.assertEquals("CHANGE:X", two.publishes.get(0));
    Assert.assertEquals("CHANGE:Y", two.publishes.get(1));
    Assert.assertTrue(pubsub.alive());
    par.alive = false;
    Assert.assertFalse(pubsub.alive());
    pubsub.__memory();
  }

  @Test
  public void repeats_settle() {
    MockRxParent par = new MockRxParent();
    MapPubSub<String> pubsub = new MapPubSub<>(par);
    MockMapSubscription sub = new MockMapSubscription();
    pubsub.subscribe(sub);
    pubsub.changed("X");
    pubsub.changed("X");
    pubsub.changed("X");
    pubsub.settle();
    pubsub.changed("Y");
    pubsub.changed("Y");
    pubsub.changed("Y");
    Assert.assertEquals(2, sub.publishes.size());
    Assert.assertEquals("CHANGE:X", sub.publishes.get(0));
    Assert.assertEquals("CHANGE:Y", sub.publishes.get(1));
    pubsub.__memory();
  }

  @Test
  public void trivial_alive() {
    Assert.assertTrue(new MapPubSub<String>(null).alive());
  }
}
