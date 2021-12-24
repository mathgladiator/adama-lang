package org.adamalang.grpc.mocks;

import org.adamalang.grpc.client.contracts.CreateCallback;
import org.junit.Assert;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class AssertCreateFailure implements CreateCallback {

    private final CountDownLatch latch;
    public int code;

    public AssertCreateFailure() {
        this.latch = new CountDownLatch(1);
    }

    @Override
    public void created() {
        latch.countDown();
        Assert.fail();
    }

    @Override
    public void error(int code) {
        this.code = code;
        latch.countDown();
    }

    public void await(int expectedCode) {
        try {
            Assert.assertTrue(latch.await(1000, TimeUnit.MILLISECONDS));
            Assert.assertEquals(expectedCode, code);
        } catch (InterruptedException ie) {
            Assert.fail();
        }
    }
}
