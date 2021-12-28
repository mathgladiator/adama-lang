package org.adamalang.gossip;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;

public class EngineTests {
    @Test
    public void convergence10() throws Exception {
        ArrayList<Engine> engines = new ArrayList<>();
        TimeSource time = new TimeSource() {
            @Override
            public long now() {
                return System.currentTimeMillis();
            }
        };
        MockMetrics metrics = new MockMetrics();

        HashSet<String> initial = new HashSet<>();
        initial.add("127.0.0.1:20000");
        initial.add("127.0.0.1:20009");

        for (int k = 0; k < 10; k++) {
            Engine engine = new Engine(time, initial, "127.0.0.1", 20000 + k, metrics);
            engines.add(engine);
            engine.start();
        }
        Thread.sleep(5000);
        for (Engine engine : engines) {
            engine.close();
        }
        // TODO: test that all the engines have the same hash
    }
}
