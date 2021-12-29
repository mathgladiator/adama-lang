/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
*/
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
