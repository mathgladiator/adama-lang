package org.adamalang.gossip;

import org.adamalang.gossip.proto.BeginGossip;
import org.adamalang.gossip.proto.Endpoint;

import java.util.*;

public class InstanceSetChain {
    private final TimeSource time;
    private final HashMap<String, Instance> primary;
    private InstanceSet current;
    private final HashMap<String, InstanceSet> history;

    public InstanceSetChain(TimeSource time) {
        this.time = time;
        this.primary = new HashMap<>();
        this.history = new HashMap<>();
    }

    public InstanceSet find(String hash) {
        return history.get(hash);
    }

    public InstanceSet current() {
        return this.current;
    }

    public Collection<Endpoint> recent() {
        return Collections.emptyList();
    }

    public long now() {
        return time.now();
    }

    public Collection<Endpoint> missing(InstanceSet set) {
        return Collections.emptyList();
    }

    public Collection<Endpoint> all() {
        return Collections.emptyList();
    }

    public void ingest(Collection<Endpoint> endpoints) {

    }
}
