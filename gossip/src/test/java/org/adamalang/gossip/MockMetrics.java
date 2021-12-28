package org.adamalang.gossip;

import org.junit.Assert;

public class MockMetrics implements Metrics {

    private final StringBuilder seq;

    public MockMetrics() {
        this.seq = new StringBuilder();
    }

    @Override
    public void bump_sad_return() {
        seq.append("[SR]");
    }

    @Override
    public void bump_complement() {
        seq.append("[COMP]");
    }

    @Override
    public void bump_optimistic_return() {
        seq.append("[OPRET]");
    }

    @Override
    public void bump_turn_tables() {
        seq.append("[TT]");
    }

    @Override
    public void bump_start() {
        seq.append("[BS]");
    }

    @Override
    public void bump_found_reverse() {
        seq.append("[FR]");
    }

    @Override
    public void bump_quick_gossip() {
        seq.append("[QG]");
    }

    @Override
    public void bump_slow_gossip() {
        seq.append("[SG]");
    }

    @Override
    public void log_error(Throwable cause) {
        seq.append("[LOG-ERROR]");
        cause.printStackTrace();
    }

    public void assertFlow(String expected) {
        Assert.assertEquals(expected, seq.toString());
    }
}
