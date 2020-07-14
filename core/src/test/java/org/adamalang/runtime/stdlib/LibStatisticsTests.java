/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.stdlib;

import org.adamalang.runtime.bridges.NativeBridge;
import org.adamalang.runtime.natives.lists.ArrayNtList;
import org.adamalang.runtime.natives.lists.EmptyNtList;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

public class LibStatisticsTests {
    @Test
    public void sumIntegers() {
        ArrayList<Integer> ints = new ArrayList<>();
        ints.add(1);
        ints.add(20);
        ints.add(300);
        ArrayNtList<Integer> list = new ArrayNtList<>(ints, NativeBridge.INTEGER_NATIVE_SUPPORT);
        Assert.assertEquals(321, LibStatistics.sumInts(list));
    }

    @Test
    public void sumDoubles() {
        ArrayList<Double> ints = new ArrayList<>();
        ints.add(1.5);
        ints.add(20.5);
        ints.add(300.75);
        ArrayNtList<Double> list = new ArrayNtList<>(ints, NativeBridge.DOUBLE_NATIVE_SUPPORT);
        Assert.assertEquals(322.75, LibStatistics.sumDoubles(list), 0.1);
    }

    @Test
    public void avgIntegers() {
        ArrayList<Integer> ints = new ArrayList<>();
        ints.add(1);
        ints.add(20);
        ints.add(300);
        ArrayNtList<Integer> list = new ArrayNtList<>(ints, NativeBridge.INTEGER_NATIVE_SUPPORT);
        Assert.assertEquals(107.0, LibStatistics.avgInts(list), 0.1);
    }

    @Test
    public void avgDoubles() {
        ArrayList<Double> ints = new ArrayList<>();
        ints.add(1.5);
        ints.add(20.5);
        ints.add(300.75);
        ArrayNtList<Double> list = new ArrayNtList<>(ints, NativeBridge.DOUBLE_NATIVE_SUPPORT);
        Assert.assertEquals(107.58333333333333, LibStatistics.avgDoubles(list), 0.1);
    }

    @Test
    public void avgEmpties() {
        Assert.assertEquals(0, LibStatistics.avgInts(new EmptyNtList<>(null)), 0.1);
        Assert.assertEquals(0, LibStatistics.avgDoubles(new EmptyNtList<>(null)), 0.1);
    }
}
