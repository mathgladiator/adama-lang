package org.adamalang.extern;

import org.adamalang.mysql.DataBase;
import org.adamalang.common.ExceptionLogger;

public class ExternNexus {

    public final Email email;
    public final DataBase dataBase;

    public ExternNexus(Email email, DataBase dataBase) {
        this.email = email;
        this.dataBase = dataBase;
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
