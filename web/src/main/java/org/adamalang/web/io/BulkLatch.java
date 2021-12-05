package org.adamalang.web.io;

import org.adamalang.runtime.contracts.Callback;
import org.adamalang.runtime.exceptions.ErrorCodeException;

import java.util.concurrent.Executor;
import java.util.function.Supplier;

public class BulkLatch<T> {
    public Executor executor;
    public int outstanding;
    public final Callback<T> callback;
    public Supplier<T> supply;

    private Integer errorCode;

    public BulkLatch(Executor executor, int outstanding, Callback<T> callback) {
        this.outstanding = outstanding;
        this.callback = callback;
        this.supply = null;
        this.errorCode = null;
    }

    public void with(Supplier<T> supply) {
        this.supply = supply;
    }

    private synchronized boolean countdownWithLock(Integer newErrorCode) {
        if (newErrorCode != null) {
            if (errorCode == null) {
                errorCode = newErrorCode;
            } else {
                if (newErrorCode < errorCode) {
                    errorCode = newErrorCode;
                }
            }
        }
        outstanding --;
        return outstanding == 0;
    }

    public void countdown(Integer newErrorCode) {
        if (countdownWithLock(newErrorCode)) {
            executor.execute(() -> {
                if (errorCode == null) {
                    T value = supply.get();
                    callback.success(value);
                } else {
                    callback.failure(new ErrorCodeException(errorCode));
                }
            });
        }

    }
}
