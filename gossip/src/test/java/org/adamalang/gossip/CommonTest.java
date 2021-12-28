package org.adamalang.gossip;

import org.adamalang.gossip.proto.Endpoint;

import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeSet;

public class CommonTest {
    public static Instance A() { return new Instance(Endpoint.newBuilder().setCounter(100).setId("id-a").setIp("ip1").setRole("proxy").setPort(123).build(), 0); }
    public static Instance B() { return new Instance(Endpoint.newBuilder().setCounter(200).setId("id-b").setIp("ip2").setRole("proxy").setPort(234).build(), 0); }
    public static Instance C() { return new Instance(Endpoint.newBuilder().setCounter(300).setId("id-c").setIp("ip1").setRole("proxy").setPort(345).build(), 0); }
    public static Instance D() { return new Instance(Endpoint.newBuilder().setCounter(400).setId("id-d").setIp("ip2").setRole("proxy").setPort(456).build(), 0); }

    public static TreeSet<Instance> INSTANCES(Instance... instances) {
        TreeSet<Instance> set = new TreeSet<>();
        for (Instance instance : instances) {
            set.add(instance);
        }
        return set;
    }

    public static Collection<Endpoint> ENDPOINTS(Instance... instances) {
        ArrayList<Endpoint> set = new ArrayList<>();
        for (Instance instance : instances) {
            set.add(instance.toEndpoint());
        }
        return set;
    }

    public ArrayList<Integer> counters(int... values) {
        ArrayList<Integer> c = new ArrayList<>(values.length);
        for (int val : values) {
            c.add(val);
        }
        return c;
    }
}
