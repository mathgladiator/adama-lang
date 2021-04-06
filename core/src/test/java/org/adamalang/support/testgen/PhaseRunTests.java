package org.adamalang.support.testgen;

import org.junit.Assert;
import org.junit.Test;

public class PhaseRunTests {
    @Test
    public void disconnect() {
        PhaseRun.wrap((x) -> {}).disconnect();
    }

    @Test
    public void mustBeTrueCoverage() {
        PhaseRun.mustBeTrue(true, "x");
        try {
            PhaseRun.mustBeTrue(false, "xyz");
            Assert.fail();
        } catch(RuntimeException re) {
            Assert.assertEquals("xyz", re.getMessage());
        }
    }
}
