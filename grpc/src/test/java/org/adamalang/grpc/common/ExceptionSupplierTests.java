package org.adamalang.grpc.common;

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
