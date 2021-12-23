package org.adamalang.grpc.client;

import java.util.HashSet;
import java.util.concurrent.Executor;

public class MultiplexProtocolPool {

    public final Executor executor;
    public HashSet<MultiplexProtocol> pool;

    public MultiplexProtocolPool(Executor executor) {
        this.executor = executor;
        this.pool = new HashSet<>();
    }

    public Runnable register(MultiplexProtocol protocol) {
        pool.add(protocol);
        return () -> {
            pool.remove(protocol);
        };
    }
}
