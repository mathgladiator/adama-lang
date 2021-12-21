package org.adamalang.transforms;

import org.adamalang.ErrorCodes;
import org.adamalang.extern.ExternNexus;
import org.adamalang.mysql.Base;
import org.adamalang.mysql.frontend.Users;
import org.adamalang.runtime.contracts.Callback;
import org.adamalang.runtime.contracts.ExceptionLogger;
import org.adamalang.runtime.exceptions.ErrorCodeException;
import org.adamalang.web.io.AsyncTransform;

import java.util.concurrent.Executor;

public class UserIdResolver implements AsyncTransform<String, Integer> {
    private final Executor executor;
    private final Base base;
    private final ExceptionLogger logger;

    public UserIdResolver(Executor executor, ExternNexus nexus) {
        this.executor = executor;
        this.base = nexus.base;
        this.logger = nexus.makeLogger(UserIdResolver.class);
    }
    @Override
    public void execute(String email, Callback<Integer> callback) {
        executor.execute(() -> {
            try {
                callback.success(Users.getOrCreateUserId(base, email));
            } catch (Exception ex) {
                callback.failure(ErrorCodeException.detectOrWrap(ErrorCodes.USERID_RESOLVE_UNKNOWN_EXCEPTION, ex, logger));
            }
        });
    }
}
