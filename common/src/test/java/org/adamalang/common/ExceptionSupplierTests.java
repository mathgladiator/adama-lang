/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * The 'LICENSE' file is in the root directory of the repository. Hint: it is MIT.
 * 
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.common;

import org.adamalang.common.ExceptionSupplier;
import org.junit.Assert;
import org.junit.Test;

public class ExceptionSupplierTests {
    @Test
    public void coverage() {
        ExceptionSupplier<String> supplier = new ExceptionSupplier<String>() {
            @Override
            public String get() throws Exception {
                throw new Exception();
            }
        };

        try {
            ExceptionSupplier.TO_RUNTIME(supplier).get();
            Assert.fail();
        } catch (RuntimeException re) {
        }
    }
}
