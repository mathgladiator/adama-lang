package org.adamalang.web.io;

import org.junit.Test;

public class NoOpJsonResponderTests {
    @Test
    public void coverage() {
        NoOpJsonResponder.INSTANCE.error(null);
        NoOpJsonResponder.INSTANCE.stream(null);
        NoOpJsonResponder.INSTANCE.finish(null);
    }
}
