/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.translator.env.topo;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class TopologicalSortTests {

  private static Set<String> D(String... keys) {
    HashSet<String> result = new HashSet<>();
    for (String key : keys) {
      result.add(key);
    }
    return result;
  }

  @Test
  public void linear_out_of_order() {
    TopologicalSort<String> ts = new TopologicalSort<>();
    ts.add("c", "vc", D("b"));
    ts.add("b", "vb", D("a"));
    ts.add("a", "va", null);
    ArrayList<String> sorted = ts.sort();
    Assert.assertEquals("va", sorted.get(0));
    Assert.assertEquals("vb", sorted.get(1));
    Assert.assertEquals("vc", sorted.get(2));
  }

  @Test
  public void complex() {
    TopologicalSort<String> ts = new TopologicalSort<>();
    ts.add("c", "vc", D("b", "d"));
    ts.add("b", "vb", D("a", "e"));
    ts.add("a", "va", null);
    ts.add("d", "vd", D("a"));
    ts.add("e", "ve", null);
    ArrayList<String> sorted = ts.sort();
    Assert.assertEquals("va", sorted.get(0));
    Assert.assertEquals("ve", sorted.get(1));
    Assert.assertEquals("vb", sorted.get(2));
    Assert.assertEquals("vd", sorted.get(3));
    Assert.assertEquals("vc", sorted.get(4));
  }

  @Test
  public void cycles() {
    TopologicalSort<String> ts = new TopologicalSort<>();
    ts.add("c", "vc", D("b"));
    ts.add("b", "vb", D("a"));
    ts.add("a", "va", D("c"));
    ArrayList<String> sorted = ts.sort();
    Assert.assertEquals("vb", sorted.get(0));
    Assert.assertEquals("vc", sorted.get(1));
    Assert.assertEquals("va", sorted.get(2));
    Assert.assertFalse(ts.cycles().isEmpty());
  }


}
