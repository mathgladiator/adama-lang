package org.adamalang.grpc.server;

import org.adamalang.grpc.TestBed;
import org.adamalang.grpc.client.Client;
import org.junit.Assert;
import org.junit.Test;

public class ServerTests {
    @Test
    public void client_survives_server_stop() throws Exception {
        try (TestBed bed = new TestBed(12346, "@connected(who) { return true; } public int x; @construct { x = 123; transition #p in 0.5; } #p { x++; } ")) {
            Client client = new Client(bed.identity, "127.0.0.1:12346", bed.clientExecutor);
            bed.assertBad(client.create("nope", "nope", "space", "1", null, "{}"));
            Assert.assertFalse(client.ping(2500));
            bed.startServer();
            Assert.assertTrue(client.ping(5000));
            bed.assertGood(client.create("nope", "nope", "space", "2", null, "{}"));
            bed.stopServer();
            Assert.assertFalse(client.ping(2500));
            bed.assertBad(client.create("nope", "nope", "space", "3", null, "{}"));
            bed.startServer();
            Assert.assertTrue(client.ping(5000));
            bed.assertGood(client.create("nope", "nope", "space", "4", null, "{}"));
        }
    }
}
