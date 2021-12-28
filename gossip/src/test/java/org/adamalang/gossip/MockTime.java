package org.adamalang.gossip;

public class MockTime implements TimeSource {
    public long currentTime = 0;

    @Override
    public long now() {
        return currentTime;
    }
}
