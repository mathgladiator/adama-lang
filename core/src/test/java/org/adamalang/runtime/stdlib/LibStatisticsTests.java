/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.stdlib;

import java.util.ArrayList;
import org.adamalang.runtime.natives.lists.ArrayNtList;
import org.adamalang.runtime.natives.lists.EmptyNtList;
import org.junit.Assert;
import org.junit.Test;

public class LibStatisticsTests {
  @Test
  public void avgDoubles() {
    final var ints = new ArrayList<Double>();
    ints.add(1.5);
    ints.add(20.5);
    ints.add(300.75);
    final var list = new ArrayNtList<>(ints);
    Assert.assertEquals(107.58333333333333, LibStatistics.avgDoubles(list), 0.1);
  }

  @Test
  public void avgEmpties() {
    Assert.assertEquals(0, LibStatistics.avgInts(new EmptyNtList<>()), 0.1);
    Assert.assertEquals(0, LibStatistics.avgDoubles(new EmptyNtList<>()), 0.1);
  }

  @Test
  public void avgIntegers() {
    final var ints = new ArrayList<Integer>();
    ints.add(1);
    ints.add(20);
    ints.add(300);
    final var list = new ArrayNtList<>(ints);
    Assert.assertEquals(107.0, LibStatistics.avgInts(list), 0.1);
  }

  @Test
  public void sumDoubles() {
    final var ints = new ArrayList<Double>();
    ints.add(1.5);
    ints.add(20.5);
    ints.add(300.75);
    final var list = new ArrayNtList<>(ints);
    Assert.assertEquals(322.75, LibStatistics.sumDoubles(list), 0.1);
  }

  @Test
  public void sumIntegers() {
    final var ints = new ArrayList<Integer>();
    ints.add(1);
    ints.add(20);
    ints.add(300);
    final var list = new ArrayNtList<>(ints);
    Assert.assertEquals(321, LibStatistics.sumInts(list));
  }
}
