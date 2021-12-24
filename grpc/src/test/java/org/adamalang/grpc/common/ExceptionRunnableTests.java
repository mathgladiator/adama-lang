package org.adamalang.grpc.common;

import org.junit.Assert;
import org.junit.Test;

public class ExceptionRunnableTests {
    @Test
    public void coverage() {
        ExceptionRunnable runnable = new ExceptionRunnable() {
            @Override
            public void run() throws Exception {
                throw new Exception();
            }
        };

        try {
            ExceptionRunnable.TO_RUNTIME(runnable).run();
            Assert.fail();
        } catch (RuntimeException re) {
        }
    }
}
