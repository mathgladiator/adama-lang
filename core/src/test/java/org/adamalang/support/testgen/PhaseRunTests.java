package org.adamalang.support.testgen;

import org.junit.Test;

public class PhaseRunTests {
    @Test
    public void disconnect() {
        PhaseRun.wrap((x) -> {}).disconnect();
    }
}
