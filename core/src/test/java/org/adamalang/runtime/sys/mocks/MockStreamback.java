/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.runtime.sys.mocks;

import org.adamalang.runtime.contracts.Streamback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.runtime.sys.CoreStream;
import org.junit.Assert;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class MockStreamback implements Streamback {

    private ErrorCodeException failure;
    private CoreStream stream;
    private final CountDownLatch began;
    private final CountDownLatch failed;
    private ArrayList<String> dataList;
    private final ArrayList<CountDownLatch> latches;

    public MockStreamback() {
        this.began = new CountDownLatch(1);
        this.failure = null;
        this.failed = new CountDownLatch(1);
        this.dataList = new ArrayList<>();
        this.latches = new ArrayList<>();
    }

    public CoreStream get() {
        return stream;
    }

    @Override
    public void onSetupComplete(CoreStream stream) {
        this.stream = stream;
        if (began.getCount() == 0) {
            Assert.fail();
        }
        began.countDown();
    }

    public void await_began() {
        try {
            Assert.assertTrue(began.await(2000, TimeUnit.MILLISECONDS));
        } catch (InterruptedException ie) {
            Assert.fail();
        }
    }

    public synchronized String get(int k) {
        return dataList.get(k);
    }

    public synchronized Runnable latchAt(int count) {
        CountDownLatch latch = new CountDownLatch(count);
        latches.add(latch);
        return () -> {
            try {
                Assert.assertTrue(latch.await(2000, TimeUnit.MILLISECONDS));
            } catch (InterruptedException ie) {
                Assert.fail();
            }
        };
    }

    @Override
    public void status(StreamStatus status) {
        next("STATUS:" + status);
    }

    @Override
    public synchronized void next(String data) {
        this.dataList.add(data);
        Iterator<CountDownLatch> it = latches.iterator();
        while (it.hasNext()) {
            CountDownLatch latch = it.next();
            latch.countDown();
            if (latch.getCount() == 0) {
                it.remove();
            }
        }
    }

    @Override
    public void failure(ErrorCodeException exception) {
        failure = exception;
        failed.countDown();
    }

    public void await_failure(int code) {
        try {
            Assert.assertTrue(failed.await(2000, TimeUnit.MILLISECONDS));
            Assert.assertEquals(code, failure.code);
        } catch (InterruptedException ie) {
            Assert.fail();
        }
    }

    public void await_any_failure() {
        try {
            Assert.assertTrue(failed.await(2000, TimeUnit.MILLISECONDS));
            Assert.assertTrue(failure.code >= 1);
        } catch (InterruptedException ie) {
            Assert.fail();
        }
    }
}
