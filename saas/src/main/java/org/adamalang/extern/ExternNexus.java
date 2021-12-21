package org.adamalang.extern;

import org.adamalang.mysql.Base;
import org.adamalang.runtime.contracts.ExceptionLogger;

public class ExternNexus {

    public final Email email;
    public final Base base;

    public ExternNexus(Email email, Base base) {
        this.email = email;
        this.base = base;
    }

    public ExceptionLogger makeLogger(Class<?> clazz) {
        return new ExceptionLogger() {
            @Override
            public void convertedToErrorCode(Throwable t, int errorCode) {
                t.printStackTrace();
            }
        };
    }
}
