package org.adamalang.gossip;

import org.adamalang.gossip.proto.Endpoint;

import java.util.Objects;

public class Instance implements Comparable<Instance> {
    public final String id;
    public final String ip;
    public final int port;
    public final String role;

    private int counter;
    private long witness;

    public Instance(String id, String ip, int port, int counter, String role, TimeSource time) {
        this.id = id;
        this.ip = ip;
        this.port = port;
        this.counter = counter;
        this.witness = time.now();
        this.role = role;
    }

    public int counter() {
        return this.counter;
    }

    public Endpoint toEndpoint() {
        return Endpoint.newBuilder().setId(id).setIp(ip).setPort(port).setRole(role).setCounter(counter).build();
    }

    @Override
    public int compareTo(Instance o) {
        return id.compareTo(o.id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Instance instance = (Instance) o;
        return id.equals(instance.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, ip, port, role, counter, witness);
    }
}
