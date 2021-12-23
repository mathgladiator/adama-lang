package org.adamalang.grpc;

import org.adamalang.grpc.client.Client;
import org.junit.Test;

public class EndToEnd_HappyTests {
    @Test
    public void ss() throws Exception {
        try (TestBed bed = new TestBed(12345, "@connected(who) { return true; } public int x; @construct { x = 123; transition #p in 0.5; } #p { x++; } ")) {
            Client client = new Client(bed.identity, "127.0.0.1:12345", bed.clientExecutor);
        }
    }
}
