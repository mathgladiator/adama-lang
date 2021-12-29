/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * The 'LICENSE' file is in the root directory of the repository. Hint: it is MIT.
 * 
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.gossip;

import org.adamalang.common.MachineIdentity;
import org.adamalang.common.TimeSource;
import org.junit.Test;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class EngineTests {
    @Test
    public void convergence10() throws Exception {
        ArrayList<Engine> engines = new ArrayList<>();
        MockMetrics metrics = new MockMetrics();

        HashSet<String> initial = new HashSet<>();
        initial.add("127.0.0.1:20000");
        initial.add("127.0.0.1:20009");
        MachineIdentity identity = MachineIdentity.fromFile(prefixForLocalhost());

        for (int k = 0; k < 10; k++) {
            Engine engine = new Engine(identity, TimeSource.REAL_TIME, initial, 20000 + k, metrics);
            engines.add(engine);
            engine.start();
        }
        for (int k = 0; k < 25; k++) {
            HashSet<String> versions = new HashSet<>();
            CountDownLatch latch = new CountDownLatch(engines.size());
            for (Engine engine : engines) {
                engine.hash((hash) -> {
                    versions.add(hash);
                    latch.countDown();
                });
            }
            latch.await(5000, TimeUnit.MILLISECONDS);
            Thread.sleep(1000);
            System.err.println("ROUND:" + versions.size());
        }
        for (Engine engine : engines) {
            engine.close();
        }
        // TODO: test that all the engines have the same hash
    }

    private String prefixForLocalhost() {
        for (String search : new String[] { "./", "../", "./grpc/"}) {
            String candidate = search + "localhost.identity";
            File file = new File(candidate);
            if (file.exists()) {
                return candidate;
            }
        }
        throw new NullPointerException("could not find identity.localhost");
    }
}
