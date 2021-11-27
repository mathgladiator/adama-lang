package org.adamalang.runtime.sys.mocks;

import org.adamalang.runtime.contracts.Callback;
import org.adamalang.runtime.exceptions.ErrorCodeException;
import org.junit.Assert;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class NullCallbackLatch implements Callback<Void> {
    private final CountDownLatch latch;
    private final AtomicBoolean failed;

    public NullCallbackLatch() {
        this.latch = new CountDownLatch(1);
        this.failed = new AtomicBoolean(false);
    }

    @Override
    public void success(Void value) {
        latch.countDown();
    }

    @Override
    public void failure(ErrorCodeException ex) {
        failed.set(true);
        latch.countDown();
    }

    public void await_success() throws Exception {
        Assert.assertTrue(latch.await(2000, TimeUnit.MILLISECONDS));
        Assert.assertFalse(failed.get());
    }

    public void await_failure() throws Exception {
        Assert.assertTrue(latch.await(2000, TimeUnit.MILLISECONDS));
        Assert.assertTrue(failed.get());
    }
}
