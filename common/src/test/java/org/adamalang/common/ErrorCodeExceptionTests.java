package org.adamalang.common;

import org.junit.Assert;
import org.junit.Test;

public class ErrorCodeExceptionTests {
    @Test
    public void coverage() {
        ExceptionLogger logger = (t, errorCode) -> {
        };
        ErrorCodeException ex1 = new ErrorCodeException(42);
        ErrorCodeException ex2 = new ErrorCodeException(500, new NullPointerException());
        ErrorCodeException ex3 = new ErrorCodeException(4242, "nope");
        Assert.assertEquals(42, ErrorCodeException.detectOrWrap(100, ex1, logger).code);
        Assert.assertEquals(500, ErrorCodeException.detectOrWrap(100, new RuntimeException(ex2), logger).code);
        Assert.assertEquals(100, ErrorCodeException.detectOrWrap(100, new NullPointerException(), logger).code);
        Assert.assertEquals(42, ex1.code);
        Assert.assertEquals(500, ex2.code);
        Assert.assertEquals(4242, ex3.code);
    }
}
