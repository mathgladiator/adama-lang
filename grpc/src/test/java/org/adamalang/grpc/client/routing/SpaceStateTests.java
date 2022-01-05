/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.grpc.client.routing;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class SpaceStateTests {
  @Test
  public void flow() {
    AtomicReference<String> last = new AtomicReference<>("");
    Consumer<Set<String>> share =
        (set) -> {
          last.set("");
          for (String e : new TreeSet<>(set)) {
            last.set(last.get() + "/" + e);
          }
        };
    SpaceState state = new SpaceState();
    ArrayList<String> pub = new ArrayList<>();
    Runnable unsubscribe = state.subscribe("key", pub::add);
    state.add("x");
    Assert.assertEquals(1, state.list().size());
    state.recompute(share);
    Assert.assertEquals("/x", last.get());
    state.add("y");
    state.add("z");
    state.add("t");
    Assert.assertEquals(4, state.list().size());
    state.recompute(share);
    Assert.assertEquals("/t/x/y/z", last.get());
    Assert.assertEquals(3, pub.size());
    Assert.assertNull(pub.get(0));
    Assert.assertEquals("x", pub.get(1));
    Assert.assertEquals("z", pub.get(2));
    state.subtract("y");
    state.recompute(share);
    Assert.assertEquals("/t/x/z", last.get());
    Assert.assertEquals(3, pub.size());
    state.recompute(share);
    Assert.assertEquals("/t/x/z", last.get());
    Assert.assertEquals(3, pub.size());
    Assert.assertEquals(3, pub.size());
    unsubscribe.run();
    Assert.assertEquals(4, pub.size());
    Assert.assertEquals(null, pub.get(3));
    state.subtract("z");
    state.recompute(share);
    Assert.assertEquals("/t/x", last.get());
    Assert.assertEquals(4, pub.size());
    state.subscribe("key", pub::add);
    Assert.assertEquals(5, pub.size());
    Assert.assertEquals("x", pub.get(4));
  }
}
