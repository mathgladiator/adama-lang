package org.adamalang.gossip;

import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeSet;

public class InstanceSet {
    private final String hash;
    private final ArrayList<Instance> instances;
    private long touched;

    public InstanceSet(String hash, TreeSet<Instance> instances, long now) {
        this.hash = hash;
        this.instances = new ArrayList<>(instances);
        this.touched = now;
    }

    public ArrayList<Integer> counters() {
        ArrayList<Integer> list = new ArrayList<>(instances.size());
        for (Instance instance : instances) {
            list.add(instance.counter());
        }
        return list;
    }

    public void ingest(Collection<Integer> counters, long now) {
        this.touched = now;
    }

    public String hash() {
        return hash;
    }
}
