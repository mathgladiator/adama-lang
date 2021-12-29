/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.gossip;

import org.adamalang.gossip.proto.Endpoint;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.TreeSet;

public class InstanceSetTests extends CommonTest {
    @Test
    public void empty() {
        TreeSet<Instance> instances = new TreeSet<>();
        InstanceSet set = new InstanceSet(instances, 100);
        TreeSet<Instance> clone = set.clone();
        Assert.assertEquals(0, clone.size());
        ArrayList<Integer> counters = set.counters();
        Assert.assertEquals(0, counters.size());
        ArrayList<Endpoint> endpoints = set.toEndpoints();
        Assert.assertEquals(0, endpoints.size());
        ArrayList<Endpoint> missing = set.missing(set);
        Assert.assertEquals(0, missing.size());
        set.ingest(new ArrayList<>(), 40);
        Assert.assertEquals("1B2M2Y8AsgTpgAmY7PhCfg==", set.hash());
    }

    @Test
    public void singleton() {
        TreeSet<Instance> instances = new TreeSet<>();
        Instance a = A();
        instances.add(a);
        InstanceSet set = new InstanceSet(instances, 100);
        TreeSet<Instance> clone = set.clone();
        Assert.assertEquals(1, clone.size());
        Assert.assertEquals(A(), clone.first());
        ArrayList<Integer> counters = set.counters();
        Assert.assertEquals(1, counters.size());
        Assert.assertEquals(100, (int) counters.get(0));
        ArrayList<Endpoint> endpoints = set.toEndpoints();
        Assert.assertEquals(1, endpoints.size());
        Assert.assertEquals("id-a", endpoints.get(0).getId());
        ArrayList<Endpoint> missing1 = set.missing(set);
        Assert.assertEquals(0, missing1.size());
        ArrayList<Endpoint> missing2 = set.missing(new InstanceSet(new TreeSet<>(), 0));
        Assert.assertEquals(1, missing2.size());
        Assert.assertEquals("id-a", missing2.get(0).getId());
        ArrayList<Integer> newCounters = new ArrayList<>();
        newCounters.add(1000);
        set.ingest(newCounters, 40);
        Assert.assertEquals("rzhcX9WgM1AwjqKNBz6eJg==", set.hash());
        Assert.assertEquals(1000, a.counter());
    }

    private void assertFour(TreeSet<Instance> instances) {
        InstanceSet set = new InstanceSet(instances, 100);
        TreeSet<Instance> clone = set.clone();
        Assert.assertEquals(4, clone.size());
        ArrayList<Integer> counters = set.counters();
        Assert.assertEquals(4, counters.size());
        Assert.assertEquals(100, (int) counters.get(0));
        Assert.assertEquals(200, (int) counters.get(1));
        Assert.assertEquals(300, (int) counters.get(2));
        Assert.assertEquals(400, (int) counters.get(3));
        ArrayList<Endpoint> endpoints = set.toEndpoints();
        Assert.assertEquals(4, endpoints.size());
        Assert.assertEquals("id-a", endpoints.get(0).getId());
        Assert.assertEquals("id-b", endpoints.get(1).getId());
        Assert.assertEquals("id-c", endpoints.get(2).getId());
        Assert.assertEquals("id-d", endpoints.get(3).getId());
        ArrayList<Endpoint> missing1 = set.missing(set);
        Assert.assertEquals(0, missing1.size());
        ArrayList<Endpoint> missing2 = set.missing(new InstanceSet(new TreeSet<>(), 0));
        Assert.assertEquals(4, missing2.size());
        Assert.assertEquals("id-a", missing2.get(0).getId());
        Assert.assertEquals("id-b", missing2.get(1).getId());
        Assert.assertEquals("id-c", missing2.get(2).getId());
        Assert.assertEquals("id-d", missing2.get(3).getId());
        TreeSet<Instance> subset1 = new TreeSet<>();
        subset1.add(A());
        subset1.add(C());
        ArrayList<Endpoint> missing3 = set.missing(new InstanceSet(new TreeSet<>(subset1), 0));
        Assert.assertEquals(2, missing3.size());
        Assert.assertEquals("id-b", missing3.get(0).getId());
        Assert.assertEquals("id-d", missing3.get(1).getId());
        set.ingest(counters(1000, 2000, 3000, 4000), 200);
        Assert.assertEquals("ZOTUaGI8E3qWqKVSFNHp3Q==", set.hash());
        Assert.assertEquals(1000, instances.first().counter());
        Assert.assertEquals(4000, instances.last().counter());
    }



    @Test
    public void four_in_order() {
        assertFour(INSTANCES(A(), B(), C(), D()));
    }

    @Test
    public void four_out_of_order() {
        assertFour(INSTANCES(A(), C(), B(), D()));
        assertFour(INSTANCES(C(), A(), D(), B()));
        assertFour(INSTANCES(D(), B(), C(), A()));
        assertFour(INSTANCES(D(), C(), B(), A()));
    }

    @Test
    public void four_duplicates() {
        assertFour(INSTANCES(A(), A(), B(), B(), D(), C()));
        assertFour(INSTANCES(A(), C(), D(), B(), D(), C()));
        assertFour(INSTANCES(A(), C(), D(), B(), D(), A()));
    }
}
