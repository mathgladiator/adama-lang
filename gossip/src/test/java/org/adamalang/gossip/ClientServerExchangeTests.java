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

import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;
import java.util.concurrent.atomic.AtomicBoolean;

public class ClientServerExchangeTests extends CommonTest {

    private void exchange(InstanceSetChain X, InstanceSetChain Y, Metrics M) {
        ClientObserver observer = new ClientObserver((r) -> r.run(), X, M);
        ServerHandler serverHandler = new ServerHandler((r) -> r.run(), Y, new AtomicBoolean(true), M);
        observer.initiate(serverHandler.exchange(observer));
    }

    @Test
    public void errors() {
        MockMetrics metrics = new MockMetrics();
        MockTime timeX = new MockTime();
        InstanceSetChain X = new InstanceSetChain(timeX);
        ClientObserver observer = new ClientObserver((r) -> r.run(), X, metrics);
        ServerHandler serverHandler = new ServerHandler((r) -> r.run(), X, new AtomicBoolean(true), metrics);
        observer.initiate(serverHandler.exchange(observer));
        serverHandler.exchange(observer).onError(new RuntimeException());
        observer.onError(new RuntimeException());
        metrics.assertFlow("[BS][OPRET][QG][LOG-ERROR][LOG-ERROR]");
    }

    @Test
    public void cross_propagte_deletes() {
        MockMetrics metrics = new MockMetrics();
        MockTime timeX = new MockTime();
        MockTime timeY = new MockTime();
        MockTime timeZ = new MockTime();
        InstanceSetChain X = new InstanceSetChain(timeX);
        InstanceSetChain Y = new InstanceSetChain(timeY);
        InstanceSetChain Z = new InstanceSetChain(timeZ);
        Assert.assertEquals("1B2M2Y8AsgTpgAmY7PhCfg==", X.current().hash());
        Assert.assertEquals("1B2M2Y8AsgTpgAmY7PhCfg==", Y.current().hash());
        Assert.assertEquals("1B2M2Y8AsgTpgAmY7PhCfg==", Z.current().hash());
        X.ingest(ENDPOINTS(A()), Collections.emptySet());
        Y.ingest(ENDPOINTS(B()), Collections.emptySet());
        Z.ingest(ENDPOINTS(C()), Collections.emptySet());
        Runnable a = X.pick("id-a");
        Runnable b = Y.pick("id-b");
        Runnable c = Z.pick("id-c");
        timeX.currentTime = 100;
        timeY.currentTime = 97;
        timeZ.currentTime = 99;
        Runnable jump = () -> {
            timeX.currentTime += 1000;
            timeY.currentTime += 200;
            timeZ.currentTime += 200;
        };
        a.run();
        b.run();
        c.run();
        Assert.assertNotNull(b);
        Assert.assertNotNull(c);
        Assert.assertNull(Y.pick("id-a"));
        Assert.assertNull(Y.pick("id-c"));
        exchange(X, Y, metrics);
        exchange(Y, Z, metrics);
        exchange(Z, X, metrics);
        Assert.assertEquals("eb185Ztym02Bltf2zsaBlw==", X.current().hash());
        Assert.assertEquals("eb185Ztym02Bltf2zsaBlw==", Y.current().hash());
        Assert.assertEquals("eb185Ztym02Bltf2zsaBlw==", Z.current().hash());
        for (int k = 0; k < 26; k++) {
            jump.run();
            b.run();
            c.run();
            X.scan();
            Y.scan();
            Z.scan();
            X.gc();
            Y.gc();
            Z.gc();
            if (k == 25) {
                Assert.assertEquals("bB5OfNoxECzcmeJ2hDQrIA==", X.current().hash());
                Assert.assertEquals("eb185Ztym02Bltf2zsaBlw==", Y.current().hash());
                Assert.assertEquals("eb185Ztym02Bltf2zsaBlw==", Z.current().hash());
            } else {
                // all is groovy
                Assert.assertEquals("eb185Ztym02Bltf2zsaBlw==", X.current().hash());
                Assert.assertEquals("eb185Ztym02Bltf2zsaBlw==", Y.current().hash());
                Assert.assertEquals("eb185Ztym02Bltf2zsaBlw==", Z.current().hash());
            }
            exchange(X, Y, metrics);
            exchange(Y, Z, metrics);
            exchange(Z, X, metrics);
        }
        Assert.assertEquals("bB5OfNoxECzcmeJ2hDQrIA==", X.current().hash());
        Assert.assertEquals("bB5OfNoxECzcmeJ2hDQrIA==", Y.current().hash());
        Assert.assertEquals("bB5OfNoxECzcmeJ2hDQrIA==", Z.current().hash());
    }


    @Test
    public void cross_propagte() {
        MockMetrics metrics = new MockMetrics();
        MockTime time = new MockTime();
        InstanceSetChain X = new InstanceSetChain(time);
        InstanceSetChain Y = new InstanceSetChain(time);
        InstanceSetChain Z = new InstanceSetChain(time);
        Assert.assertEquals("1B2M2Y8AsgTpgAmY7PhCfg==", X.current().hash());
        Assert.assertEquals("1B2M2Y8AsgTpgAmY7PhCfg==", Y.current().hash());
        Assert.assertEquals("1B2M2Y8AsgTpgAmY7PhCfg==", Z.current().hash());
        X.ingest(ENDPOINTS(A()), Collections.emptySet());
        Y.ingest(ENDPOINTS(B()), Collections.emptySet());
        Z.ingest(ENDPOINTS(C()), Collections.emptySet());
        Assert.assertEquals("rzhcX9WgM1AwjqKNBz6eJg==", X.current().hash());
        Assert.assertEquals("PlBLj9Ty9gKbLiKc59dLig==", Y.current().hash());
        Assert.assertEquals("5iYK+2JucxrYj7ST72OS2Q==", Z.current().hash());
        exchange(X, Y, metrics);
        Assert.assertEquals("ltgPF0P/6UcAj3+6Mnd/sA==", X.current().hash());
        Assert.assertEquals("ltgPF0P/6UcAj3+6Mnd/sA==", Y.current().hash());
        Assert.assertEquals("5iYK+2JucxrYj7ST72OS2Q==", Z.current().hash());
        exchange(Y, Z, metrics);
        Assert.assertEquals("ltgPF0P/6UcAj3+6Mnd/sA==", X.current().hash());
        Assert.assertEquals("eb185Ztym02Bltf2zsaBlw==", Y.current().hash());
        Assert.assertEquals("eb185Ztym02Bltf2zsaBlw==", Z.current().hash());
        exchange(Z, X, metrics);
        Assert.assertEquals("eb185Ztym02Bltf2zsaBlw==", X.current().hash());
        Assert.assertEquals("eb185Ztym02Bltf2zsaBlw==", Y.current().hash());
        Assert.assertEquals("eb185Ztym02Bltf2zsaBlw==", Z.current().hash());
        metrics.assertFlow("[BS][SR][FR][TT][BS][SR][FR][TT][BS][OPRET][QG]");
    }

    @Test
    public void cross_propagte_very_slow() {
        MockMetrics metrics = new MockMetrics();
        MockTime time = new MockTime();
        InstanceSetChain X = new InstanceSetChain(time);
        InstanceSetChain Y = new InstanceSetChain(time);
        InstanceSetChain Z = new InstanceSetChain(time);
        Assert.assertEquals("1B2M2Y8AsgTpgAmY7PhCfg==", X.current().hash());
        Assert.assertEquals("1B2M2Y8AsgTpgAmY7PhCfg==", Y.current().hash());
        Assert.assertEquals("1B2M2Y8AsgTpgAmY7PhCfg==", Z.current().hash());
        X.ingest(ENDPOINTS(A()), Collections.emptySet());
        Y.ingest(ENDPOINTS(B()), Collections.emptySet());
        Z.ingest(ENDPOINTS(C()), Collections.emptySet());
        time.currentTime += 1000000;
        X.gc();
        Y.gc();
        Z.gc();
        Assert.assertEquals("rzhcX9WgM1AwjqKNBz6eJg==", X.current().hash());
        Assert.assertEquals("PlBLj9Ty9gKbLiKc59dLig==", Y.current().hash());
        Assert.assertEquals("5iYK+2JucxrYj7ST72OS2Q==", Z.current().hash());
        exchange(X, Y, metrics);
        Assert.assertEquals("ltgPF0P/6UcAj3+6Mnd/sA==", X.current().hash());
        Assert.assertEquals("ltgPF0P/6UcAj3+6Mnd/sA==", Y.current().hash());
        Assert.assertEquals("5iYK+2JucxrYj7ST72OS2Q==", Z.current().hash());
        exchange(Y, Z, metrics);
        Assert.assertEquals("ltgPF0P/6UcAj3+6Mnd/sA==", X.current().hash());
        Assert.assertEquals("eb185Ztym02Bltf2zsaBlw==", Y.current().hash());
        Assert.assertEquals("eb185Ztym02Bltf2zsaBlw==", Z.current().hash());
        exchange(Z, X, metrics);
        Assert.assertEquals("eb185Ztym02Bltf2zsaBlw==", X.current().hash());
        Assert.assertEquals("eb185Ztym02Bltf2zsaBlw==", Y.current().hash());
        Assert.assertEquals("eb185Ztym02Bltf2zsaBlw==", Z.current().hash());
        metrics.assertFlow("[BS][SR][SG][COMP][BS][SR][SG][COMP][BS][SR][SG][COMP]");
    }

    @Test
    public void simple_propagate() {
        MockMetrics metrics = new MockMetrics();
        MockTime time = new MockTime();
        InstanceSetChain X = new InstanceSetChain(time);
        InstanceSetChain Y = new InstanceSetChain(time);
        InstanceSetChain Z = new InstanceSetChain(time);
        Assert.assertEquals("1B2M2Y8AsgTpgAmY7PhCfg==", X.current().hash());
        Assert.assertEquals("1B2M2Y8AsgTpgAmY7PhCfg==", Y.current().hash());
        Assert.assertEquals("1B2M2Y8AsgTpgAmY7PhCfg==", Z.current().hash());
        X.ingest(ENDPOINTS(A()), Collections.emptySet());
        Assert.assertEquals("rzhcX9WgM1AwjqKNBz6eJg==", X.current().hash());
        Assert.assertEquals("1B2M2Y8AsgTpgAmY7PhCfg==", Y.current().hash());
        Assert.assertEquals("1B2M2Y8AsgTpgAmY7PhCfg==", Z.current().hash());
        exchange(X, Y, metrics);
        Assert.assertEquals("rzhcX9WgM1AwjqKNBz6eJg==", X.current().hash());
        Assert.assertEquals("rzhcX9WgM1AwjqKNBz6eJg==", Y.current().hash());
        Assert.assertEquals("1B2M2Y8AsgTpgAmY7PhCfg==", Z.current().hash());
        exchange(Y, Z, metrics);
        Assert.assertEquals("rzhcX9WgM1AwjqKNBz6eJg==", X.current().hash());
        Assert.assertEquals("rzhcX9WgM1AwjqKNBz6eJg==", Y.current().hash());
        Assert.assertEquals("rzhcX9WgM1AwjqKNBz6eJg==", Z.current().hash());
        metrics.assertFlow("[BS][OPRET][QG][BS][OPRET][QG]");
    }

    @Test
    public void exchange_empty() {
        MockMetrics metrics = new MockMetrics();
        MockTime time = new MockTime();
        InstanceSetChain X = new InstanceSetChain(time);
        InstanceSetChain Y = new InstanceSetChain(time);
        exchange(X, Y, metrics);
        Assert.assertEquals("1B2M2Y8AsgTpgAmY7PhCfg==", X.current().hash());
        Assert.assertEquals("1B2M2Y8AsgTpgAmY7PhCfg==", Y.current().hash());
        metrics.assertFlow("[BS][OPRET][QG]");
    }

}
