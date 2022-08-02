/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.net.client.routing.cache;

import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;

public class RoutingTableTests {
  @Test
  public void flow() {
    RoutingTable table = new RoutingTable();
    Assert.assertNull(table.random());
    Assert.assertEquals(0, table.targetsFor("space").size());
    table.integrate("t1", Collections.singleton("space"));
    Assert.assertEquals("t1", table.random());
    Assert.assertEquals("t1", table.get("space", "key"));
    Assert.assertEquals(1, table.targetsFor("space").size());
    table.remove("t1");
    Assert.assertNull(table.random());
    Assert.assertEquals(0, table.targetsFor("space").size());
    table.integrate("t1", Collections.singleton("space"));
    Assert.assertEquals(1, table.targetsFor("space").size());
    table.integrate("t1", Collections.emptyList());
    Assert.assertEquals(0, table.targetsFor("space").size());
    table.integrate("t1", Collections.singleton("space"));
    table.integrate("t2", Collections.singleton("space"));
    table.integrate("t3", Collections.singleton("space"));
    Assert.assertEquals(3, table.targetsFor("space").size());
    Assert.assertEquals("t2", table.get("space", "key1"));
    Assert.assertEquals("t1", table.get("space", "key2"));
    Assert.assertEquals("t1", table.get("space", "key3"));
    Assert.assertEquals("t1", table.get("space", "key4"));
    Assert.assertEquals("t3", table.get("space", "key5"));
    Assert.assertEquals("t1", table.get("space", "key6"));
    Assert.assertEquals("t2", table.get("space", "key7"));
    Assert.assertEquals("t3", table.get("space", "key8"));
    table.integrate("t1", Collections.emptyList());
    table.integrate("t2", Collections.emptyList());
    table.integrate("t3", Collections.emptyList());
  }
}
