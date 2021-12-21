package org.adamalang.transforms.results;

import org.adamalang.runtime.natives.NtClient;

public class AuthenticatedUser {
    public static enum Source {
        Social,
        Adama,
        Authority,
    }
    public final Source source;
    public final int id;
    public final NtClient who;

    public AuthenticatedUser(Source source, int id, NtClient who) {
        this.source = source;
        this.id = id;
        this.who = who;
    }
}
