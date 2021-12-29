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

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
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
