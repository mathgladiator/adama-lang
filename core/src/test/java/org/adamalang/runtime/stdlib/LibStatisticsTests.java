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

import org.adamalang.runtime.natives.lists.ArrayNtList;
import org.adamalang.runtime.natives.lists.EmptyNtList;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

public class LibStatisticsTests {
  @Test
  public void minmaxEmpties() {
    Assert.assertFalse(LibStatistics.minInts(new EmptyNtList<>()).has());
    Assert.assertFalse(LibStatistics.minLongs(new EmptyNtList<>()).has());
    Assert.assertFalse(LibStatistics.minDoubles(new EmptyNtList<>()).has());
    Assert.assertFalse(LibStatistics.maxInts(new EmptyNtList<>()).has());
    Assert.assertFalse(LibStatistics.maxLongs(new EmptyNtList<>()).has());
    Assert.assertFalse(LibStatistics.maxDoubles(new EmptyNtList<>()).has());
  }

  @Test
  public void minMaxDoubles() {
    final var vals = new ArrayList<Double>();
    vals.add(20.5);
    vals.add(1.5);
    vals.add(300.75);
    vals.add(100.5);
    final var list = new ArrayNtList<>(vals);
    Assert.assertEquals(1.5, LibStatistics.minDoubles(list).get(), 0.1);
    Assert.assertEquals(300.75, LibStatistics.maxDoubles(list).get(), 0.1);
  }

  @Test
  public void minMaxIntegers() {
    final var ints = new ArrayList<Integer>();
    ints.add(20);
    ints.add(1);
    ints.add(300);
    ints.add(100);
    final var list = new ArrayNtList<>(ints);
    Assert.assertEquals(1, (int) LibStatistics.minInts(list).get());
    Assert.assertEquals(300, (int) LibStatistics.maxInts(list).get());
  }

  @Test
  public void minMaxLongs() {
    final var lngs = new ArrayList<Long>();
    lngs.add(20L);
    lngs.add(-25L);
    lngs.add(450L);
    lngs.add(100L);
    final var list = new ArrayNtList<>(lngs);
    Assert.assertEquals(-25, (long) LibStatistics.minLongs(list).get());
    Assert.assertEquals(450, (long) LibStatistics.maxLongs(list).get());
  }

  @Test
  public void avgEmpties() {
    Assert.assertFalse(LibStatistics.avgInts(new EmptyNtList<>()).has());
    Assert.assertFalse(LibStatistics.avgLongs(new EmptyNtList<>()).has());
    Assert.assertFalse(LibStatistics.avgDoubles(new EmptyNtList<>()).has());
  }

  @Test
  public void avgDoubles() {
    final var vals = new ArrayList<Double>();
    vals.add(1.5);
    vals.add(20.5);
    vals.add(300.75);
    final var list = new ArrayNtList<>(vals);
    Assert.assertEquals(107.58333333333333, LibStatistics.avgDoubles(list).get(), 0.1);
  }

  @Test
  public void avgIntegers() {
    final var ints = new ArrayList<Integer>();
    ints.add(1);
    ints.add(20);
    ints.add(300);
    final var list = new ArrayNtList<>(ints);
    Assert.assertEquals(107.0, LibStatistics.avgInts(list).get(), 0.1);
  }

  @Test
  public void avgLongs() {
    final var lngs = new ArrayList<Long>();
    lngs.add(1L);
    lngs.add(20L);
    lngs.add(300L);
    final var list = new ArrayNtList<>(lngs);
    Assert.assertEquals(107.0, LibStatistics.avgLongs(list).get(), 0.1);
  }

  @Test
  public void sumEmpties() {
    Assert.assertFalse(LibStatistics.sumInts(new EmptyNtList<>()).has());
    Assert.assertFalse(LibStatistics.sumLongs(new EmptyNtList<>()).has());
    Assert.assertFalse(LibStatistics.sumDoubles(new EmptyNtList<>()).has());
  }

  @Test
  public void sumDoubles() {
    final var ints = new ArrayList<Double>();
    ints.add(1.5);
    ints.add(20.5);
    ints.add(300.75);
    final var list = new ArrayNtList<>(ints);
    Assert.assertEquals(322.75, LibStatistics.sumDoubles(list).get(), 0.1);
  }

  @Test
  public void sumIntegers() {
    final var ints = new ArrayList<Integer>();
    ints.add(1);
    ints.add(20);
    ints.add(300);
    final var list = new ArrayNtList<>(ints);
    Assert.assertEquals(321, (int) LibStatistics.sumInts(list).get());
  }

  @Test
  public void sumLongs() {
    final var longs = new ArrayList<Long>();
    longs.add(1L);
    longs.add(200000L);
    longs.add(30000000000L);
    final var list = new ArrayNtList<>(longs);
    Assert.assertEquals(30000200001L, (long) LibStatistics.sumLongs(list).get());
  }

  @Test
  public void medianEmpties() {
    Assert.assertFalse(LibStatistics.medianDoubles(new EmptyNtList<>()).has());
    Assert.assertFalse(LibStatistics.medianInts(new EmptyNtList<>()).has());
    Assert.assertFalse(LibStatistics.medianLongs(new EmptyNtList<>()).has());
  }

  @Test
  public void medianDoubles1() {
    final var vals = new ArrayList<Double>();
    vals.add(1.5);
    vals.add(20.5);
    vals.add(300.75);
    final var list = new ArrayNtList<>(vals);
    Assert.assertEquals(20.5, LibStatistics.medianDoubles(list).get(), 0.1);
  }

  @Test
  public void medianIntegers1() {
    final var ints = new ArrayList<Integer>();
    ints.add(1);
    ints.add(20);
    ints.add(300);
    final var list = new ArrayNtList<>(ints);
    Assert.assertEquals(20, (int) LibStatistics.medianInts(list).get());
  }

  @Test
  public void medianLongs1() {
    final var longs = new ArrayList<Long>();
    longs.add(1L);
    longs.add(200000L);
    longs.add(30000000000L);
    final var list = new ArrayNtList<>(longs);
    Assert.assertEquals(200000L, (long) LibStatistics.medianLongs(list).get());
  }

  @Test
  public void medianDoubles2() {
    final var vals = new ArrayList<Double>();
    vals.add(20.5);
    final var list = new ArrayNtList<>(vals);
    Assert.assertEquals(20.5, LibStatistics.medianDoubles(list).get(), 0.1);
  }

  @Test
  public void medianIntegers2() {
    final var ints = new ArrayList<Integer>();
    ints.add(20);
    final var list = new ArrayNtList<>(ints);
    Assert.assertEquals(20, (int) LibStatistics.medianInts(list).get());
  }

  @Test
  public void medianLongs2() {
    final var longs = new ArrayList<Long>();
    longs.add(200000L);
    final var list = new ArrayNtList<>(longs);
    Assert.assertEquals(200000L, (long) LibStatistics.medianLongs(list).get());
  }

  @Test
  public void medianDoubles3() {
    final var vals = new ArrayList<Double>();
    vals.add(400.5);
    vals.add(1.5);
    vals.add(10.5);
    vals.add(20.5);
    final var list = new ArrayNtList<>(vals);
    Assert.assertEquals(15.5, LibStatistics.medianDoubles(list).get(), 0.1);
  }

  @Test
  public void medianIntegers3() {
    final var ints = new ArrayList<Integer>();
    ints.add(400);
    ints.add(1);
    ints.add(10);
    ints.add(20);
    final var list = new ArrayNtList<>(ints);
    Assert.assertEquals(15, (int) LibStatistics.medianInts(list).get());
  }

  @Test
  public void medianLongs3() {
    final var longs = new ArrayList<Long>();
    longs.add(400L);
    longs.add(1L);
    longs.add(10L);
    longs.add(20L);
    final var list = new ArrayNtList<>(longs);
    Assert.assertEquals(15L, (long) LibStatistics.medianLongs(list).get());
  }

  @Test
  public void percentilesEmpties() {
    Assert.assertFalse(LibStatistics.percentileInts(new EmptyNtList<>(), 0.9).has());
    Assert.assertFalse(LibStatistics.percentileDoubles(new EmptyNtList<>(), 0.9).has());
    Assert.assertFalse(LibStatistics.percentileLongs(new EmptyNtList<>(), 0.9).has());
  }

  @Test
  public void percentilesDoubles() {
    final var vals = new ArrayList<Double>();
    vals.add(400.5);
    vals.add(1.5);
    vals.add(10.5);
    vals.add(20.5);
    final var list = new ArrayNtList<>(vals);
    Assert.assertEquals(400.5, LibStatistics.percentileDoubles(list, 0.95).get(), 0.1);
  }

  @Test
  public void percentilesInteger() {
    final var ints = new ArrayList<Integer>();
    ints.add(400);
    ints.add(1);
    ints.add(10);
    ints.add(20);
    final var list = new ArrayNtList<>(ints);
    Assert.assertEquals(400, (int) LibStatistics.percentileInts(list, 0.95).get());
  }

  @Test
  public void percentilesLong() {
    final var longs = new ArrayList<Long>();
    longs.add(400L);
    longs.add(1L);
    longs.add(10L);
    longs.add(20L);
    final var list = new ArrayNtList<>(longs);
    Assert.assertEquals(400L, (long) LibStatistics.percentileLongs(list, 0.95).get());
  }
}
