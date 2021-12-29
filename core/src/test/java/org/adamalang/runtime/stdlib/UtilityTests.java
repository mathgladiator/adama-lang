/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.runtime.stdlib;

import org.adamalang.runtime.natives.NtMaybe;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.function.Function;

public class UtilityTests {
  @Test
  public void convert_array() {
    final Function<Integer, Integer[]> makeArray = (final Integer n) -> new Integer[n];
    final Function<String, Integer> conv = (final String s) -> Integer.parseInt(s);
    final var data = new String[] { "123", "42" };
    final var arr = Utility.convertMultiple(data, makeArray, conv);
    Assert.assertEquals(123, (int) arr[0]);
    Assert.assertEquals(42, (int) arr[1]);
  }

  @Test
  public void convert_list() {
    final Function<Integer, Integer[]> makeArray = (final Integer n) -> new Integer[n];
    final Function<String, Integer> conv = (final String s) -> Integer.parseInt(s);
    final var list = new ArrayList<String>();
    list.add("123");
    list.add("42");
    final var arr = Utility.convertMultiple(list, makeArray, conv);
    Assert.assertEquals(123, (int) arr[0]);
    Assert.assertEquals(42, (int) arr[1]);
  }

  @Test
  public void convert_maybe() {
    final var ms = new NtMaybe<String>();
    Assert.assertFalse(Utility.convertMaybe(ms, (final String s) -> Integer.parseInt(s)).has());
    ms.set("123");
    Assert.assertTrue(Utility.convertMaybe(ms, (final String s) -> Integer.parseInt(s)).has());
    Assert.assertEquals(123, (int) Utility.convertMaybe(ms, (final String s) -> Integer.parseInt(s)).get());
  }

  @Test
  public void convert_single() {
    Assert.assertEquals(123, (int) Utility.convertSingle("123", (final String s) -> Integer.parseInt(s)));
  }

  @Test
  public void coverage() {
    Utility.identity(123);
    Utility.identity(123L);
  }

  @Test
  public void lookup() {
    final var X = new Integer[] { 1, 3, 5 };
    Assert.assertEquals(1, (int) Utility.lookup(X, 0).get());
    Assert.assertEquals(3, (int) Utility.lookup(X, 1).get());
    Assert.assertEquals(5, (int) Utility.lookup(X, 2).get());
    Assert.assertFalse(Utility.lookup(X, -1).has());
    Assert.assertFalse(Utility.lookup(X, 4).has());
  }
}
