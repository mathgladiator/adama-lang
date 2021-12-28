package org.adamalang.gossip;

import org.adamalang.common.TimeSource;
import org.adamalang.gossip.proto.Endpoint;

import java.util.*;

public class InstanceSetChain {
    private final TimeSource time;
    private final HashMap<String, Instance> primary;
    private InstanceSet current;
    private final GarbageMap<InstanceSet> history;
    private final GarbageMap<Instance> recentlyLearnedAbout;
    private final GarbageMap<Instance> recentlyDeleted;

    public InstanceSetChain(TimeSource time) {
        this.time = time;
        this.primary = new HashMap<>();
        this.history = new GarbageMap<>();
        this.current = new InstanceSet(new TreeSet<>(), time.nowMilliseconds());
        this.recentlyLearnedAbout = new GarbageMap<>();
        this.recentlyDeleted = new GarbageMap<>();
    }

    public InstanceSet find(String hash) {
        if (current.hash().equals(hash)) {
            return current;
        }
        return history.get(hash);
    }

    public InstanceSet current() {
        return this.current;
    }

    public Collection<Endpoint> recent() {
        ArrayList<Endpoint> list = new ArrayList<>();
        Iterator<Instance> instance = recentlyLearnedAbout.iterator();
        while (instance.hasNext()) {
            list.add(instance.next().toEndpoint());
        }
        return list;
    }

    public long now() {
        return time.nowMilliseconds();
    }

    public Collection<Endpoint> missing(InstanceSet set) {
        return current.missing(set);
    }

    public Collection<Endpoint> all() {
        return current.toEndpoints();
    }

    public Runnable pick(String id) {
        Instance instance = primary.get(id);
        if (instance != null) {
            return () -> instance.bump(time.nowMilliseconds());
        } else {
            return null;
        }
    }

    public long scan() {
        long now = time.nowMilliseconds();
        long min = now;
        TreeSet<Instance> clone = null;
        Iterator<Map.Entry<String, Instance>> iterator = primary.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Instance> entry = iterator.next();
            Instance instance = entry.getValue();
            if (instance.tooOldMustDelete(now)) {
                if (clone == null) {
                    clone = current.clone();
                }
                recentlyDeleted.put(entry.getKey(), instance, now);
                recentlyLearnedAbout.remove(entry.getKey());
                iterator.remove();
                clone.remove(instance);
            } else if (instance.witnessed() < min) {
                min = instance.witnessed();
            }
        }
        if (clone != null) {
            history.put(current.hash(), current, now);
            current = new InstanceSet(clone, now);
        }
        return min;
    }

    public void gc() {
        long now = time.nowMilliseconds();
        history.gc(now);
        recentlyDeleted.gc(now);
        recentlyLearnedAbout.gc(now);
    }

    public void ingest(Collection<Endpoint> endpoints, Set<String> deletes) {
        long now = time.nowMilliseconds();

        TreeSet<Instance> clone = null;
        for (Endpoint ep : endpoints) {
            Instance prior = primary.get(ep.getId());
            if (prior != null) {
                prior.absorb(ep.getCounter(), now);
            } else {
                Instance newInstance = recentlyDeleted.remove(ep.getId());;
                if (newInstance == null) {
                    newInstance = new Instance(ep, now);
                } else {
                    newInstance.absorb(ep.getCounter(), now);
                }
                if (clone == null) {
                    clone = current.clone();
                }
                primary.put(ep.getId(), newInstance);
                clone.add(newInstance);
                recentlyLearnedAbout.put(ep.getId(), newInstance, now);
            }
        }
        for (String delId : deletes) {
            Instance prior = primary.get(delId);
            if (prior != null) {
                if (prior.canDelete(now)) {
                    recentlyLearnedAbout.remove(delId);
                    recentlyDeleted.put(delId, prior, now);
                    primary.remove(delId);
                    if (clone == null) {
                        clone = current.clone();
                    }
                    clone.remove(prior);
                }
            }
        }
        if (clone != null) {
            history.put(current.hash(), current, now);
            current = new InstanceSet(clone, now);
        }
    }

    public Collection<String> deletes() {
        return recentlyDeleted.keys();
    }
}
