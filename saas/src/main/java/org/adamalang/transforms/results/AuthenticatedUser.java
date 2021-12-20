package org.adamalang.transforms.results;

import org.adamalang.runtime.natives.NtClient;

public class AuthenticatedUser {
    public static enum Source {
        Developer,
        Authority,
    }
    public final int userId;
    public final NtClient who;

    public AuthenticatedUser(int userId, NtClient who) {
        this.userId = userId;
        this.who = who;
    }

    public boolean isDeveloper() {
        return true;
    }
}
