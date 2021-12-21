package org.adamalang.transforms;

import org.adamalang.ErrorCodes;
import org.adamalang.mysql.Base;
import org.adamalang.mysql.frontend.Spaces;
import org.adamalang.runtime.exceptions.ErrorCodeException;
import org.adamalang.transforms.results.SpacePolicy;
import org.adamalang.runtime.contracts.Callback;
import org.adamalang.web.io.AsyncTransform;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;

public class SpacePolicyLocator implements AsyncTransform<String, SpacePolicy> {

    public final Base base;
    public final Executor executor;
    public final ConcurrentHashMap<String, SpacePolicy> policies;

    public SpacePolicyLocator(Executor executor, Base base) {
        this.executor = executor;
        this.base = base;
        this.policies = new ConcurrentHashMap<>();
    }

    @Override
    public void execute(String spaceName, Callback<SpacePolicy> callback) {
        SpacePolicy policy = policies.get(spaceName);
        if (policy != null) {
            callback.success(policy);
            return;
        }
        executor.execute(() -> {
            try {
                Spaces.Space space = Spaces.getSpaceId(base, spaceName);
                policies.putIfAbsent(spaceName, new SpacePolicy(space));
                callback.success(policies.get(spaceName));
            } catch (Exception ex) {
                ex.printStackTrace(); // TODO: log this
                callback.failure(new ErrorCodeException(ErrorCodes.SPACE_POLICY_LOCATOR_UNKNOWN_EXCEPTION, ex));
            }
        });
    }
}
