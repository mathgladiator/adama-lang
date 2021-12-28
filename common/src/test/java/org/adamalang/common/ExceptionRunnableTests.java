package org.adamalang.common;

import org.junit.Assert;
import org.junit.Test;

public class ExceptionRunnableTests {
    @Test
    public void coverageSad() {
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

    @Test
    public void coverageHappy() {
        ExceptionRunnable runnable = new ExceptionRunnable() {
            @Override
            public void run() throws Exception {
            }
        };
        ExceptionRunnable.TO_RUNTIME(runnable).run();
    }
}
