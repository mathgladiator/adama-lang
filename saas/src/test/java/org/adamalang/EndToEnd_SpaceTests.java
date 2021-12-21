package org.adamalang;

import org.junit.Test;

public class EndToEnd_SpaceTests {
    @Test
    public void flow() throws Exception {
        try (TestFrontEnd fe = new TestFrontEnd()) {
            String alice = fe.generateIdentity("alice@x.com");
            String bob = fe.generateIdentity("bob@x.com");
        }
    }
}
