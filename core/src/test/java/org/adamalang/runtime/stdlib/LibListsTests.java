/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.runtime.stdlib;

import org.adamalang.runtime.natives.NtList;
import org.adamalang.runtime.natives.lists.ArrayNtList;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

public class LibListsTests {
  @Test
  public void flatten() {
    ArrayList<NtList<Integer>> x = new ArrayList<>();
    {
      ArrayList<Integer> z = new ArrayList<>();
      z.add(1);
      z.add(2);
      z.add(3);
      x.add(new ArrayNtList<>(z));
    }
    {
      ArrayList<Integer> z = new ArrayList<>();
      z.add(4);
      z.add(5);
      x.add(new ArrayNtList<>(z));
    }
    {
      ArrayList<Integer> z = new ArrayList<>();
      z.add(6);
      z.add(7);
      x.add(new ArrayNtList<>(z));
    }
    NtList<NtList<Integer>> result = new ArrayNtList<>(x);
    NtList<Integer> vals = LibLists.flatten(result);
    Assert.assertEquals(7, vals.size());
    for (int k = 0; k < 7; k++) {
      Assert.assertEquals(k + 1, (int) vals.lookup(k).get());
    }
  }

  @Test
  public void reverse() {
    ArrayList<Integer> z = new ArrayList<>();
    z.add(1);
    z.add(2);
    z.add(3);
    NtList<Integer> vals = LibLists.reverse(new ArrayNtList<>(z));
    Assert.assertEquals(3, vals.size());
    for (int k = 0; k < 3; k++) {
      Assert.assertEquals(3 - k, (int) vals.lookup(k).get());
    }
  }

  @Test
  public void skip() {
    ArrayList<Integer> z = new ArrayList<>();
    z.add(1);
    z.add(2);
    z.add(3);
    z.add(4);
    z.add(5);
    NtList<Integer> vals = LibLists.skip(new ArrayNtList<>(z), 2);
    Assert.assertEquals(3, vals.size());
    for (int k = 0; k < 3; k++) {
      Assert.assertEquals(3 + k, (int) vals.lookup(k).get());
    }
  }

  @Test
  public void drop() {
    ArrayList<Integer> z = new ArrayList<>();
    z.add(1);
    z.add(2);
    z.add(3);
    z.add(4);
    z.add(5);
    NtList<Integer> vals = LibLists.drop(new ArrayNtList<>(z), 2);
    Assert.assertEquals(3, vals.size());
    for (int k = 0; k < 3; k++) {
      Assert.assertEquals(1 + k, (int) vals.lookup(k).get());
    }
  }
}
