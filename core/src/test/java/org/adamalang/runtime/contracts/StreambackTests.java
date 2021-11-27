package org.adamalang.runtime.contracts;

import org.junit.Test;

public class StreambackTests {
    @Test
    public void coverage() {
        Streamback.StreamStatus.Connected.toString();
        Streamback.StreamStatus.Disconnected.toString();
    }
}
