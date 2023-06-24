/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
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
    Assert.assertEquals("vd", sorted.get(1));
    Assert.assertEquals("ve", sorted.get(2));
    Assert.assertEquals("vb", sorted.get(3));
    Assert.assertEquals("vc", sorted.get(4));
  }

  @Test
  public void cycles() {
    TopologicalSort<String> ts = new TopologicalSort<>();
    ts.add("c", "vc", D("b"));
    ts.add("b", "vb", D("a"));
    ts.add("a", "va", D("c"));
    ArrayList<String> sorted = ts.sort();
    Assert.assertEquals("va", sorted.get(0));
    Assert.assertEquals("vb", sorted.get(1));
    Assert.assertEquals("vc", sorted.get(2));
    Assert.assertFalse(ts.cycles().isEmpty());
  }

  @Test
  public void linear() {
    TopologicalSort<String> ts = new TopologicalSort<>();
    ts.add("a", "va", null);
    ts.add("b", "vb", D("a"));
    ts.add("c", "vc", D("b"));
    ArrayList<String> sorted = ts.sort();
    Assert.assertEquals("va", sorted.get(0));
    Assert.assertEquals("vb", sorted.get(1));
    Assert.assertEquals("vc", sorted.get(2));
    Assert.assertTrue(ts.cycles().isEmpty());
  }
}
