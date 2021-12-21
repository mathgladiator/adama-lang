package org.adamalang.transforms;

import org.adamalang.ErrorCodes;
import org.adamalang.mysql.Base;
import org.adamalang.mysql.frontend.Users;
import org.adamalang.runtime.contracts.Callback;
import org.adamalang.runtime.exceptions.ErrorCodeException;
import org.adamalang.web.io.AsyncTransform;

import java.util.concurrent.Executor;

public class UserIdResolver implements AsyncTransform<String, Integer> {
    private final Executor executor;
    private final Base base;

    public UserIdResolver(Executor executor, Base base) {
        this.executor = executor;
        this.base = base;
    }
    @Override
    public void execute(String email, Callback<Integer> callback) {
        executor.execute(() -> {
            try {
                callback.success(Users.getOrCreateUserId(base, email));
            } catch (Exception ex) {
                ex.printStackTrace(); // TODO: LOG THIS
                callback.failure(new ErrorCodeException(ErrorCodes.USERID_RESOLVE_UNKNOWN_EXCEPTION, ex));
            }
        });
    }
}
