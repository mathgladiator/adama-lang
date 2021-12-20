package org.adamalang.extern;

import org.adamalang.mysql.Base;

public class ExternNexus {

    public final Email email;
    public final Base base;

    public ExternNexus(Email email, Base base) {
        this.email = email;
        this.base = base;
    }
}
