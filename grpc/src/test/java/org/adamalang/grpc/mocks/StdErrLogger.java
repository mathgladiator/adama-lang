package org.adamalang.grpc.mocks;

import org.adamalang.common.ExceptionLogger;

public class StdErrLogger implements ExceptionLogger {
    @Override
    public void convertedToErrorCode(Throwable t, int errorCode) {
        System.err.println("ERROR:" + errorCode);
        t.printStackTrace(System.err);
    }
}
