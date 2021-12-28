package org.adamalang.gossip;

import org.adamalang.common.TimeSource;

public class MockTime implements TimeSource {
    public long currentTime = 0;

    @Override
    public long nowMilliseconds() {
        return currentTime;
    }
}
