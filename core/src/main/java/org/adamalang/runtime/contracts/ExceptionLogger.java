package org.adamalang.runtime.contracts;

/** allows exceptions to be monitored externally */
public interface ExceptionLogger {

    public void convertedToErrorCode(Throwable t, int errorCode);
}
