package org.adamalang.runtime.sys;

import org.adamalang.runtime.contracts.Key;
import org.adamalang.runtime.contracts.SimpleExecutor;
import org.junit.Test;

public class SimpleExecutorNowTests {
    @Test
    public void coverage() {
        SimpleExecutor.NOW.execute(() -> {});
        SimpleExecutor.NOW.schedule(new Key("space", "key"), () -> {}, 1000L);
        SimpleExecutor.NOW.shutdown();
    }
}
