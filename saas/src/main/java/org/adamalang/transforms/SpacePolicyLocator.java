package org.adamalang.transforms;

import org.adamalang.ErrorCodes;
import org.adamalang.extern.ExternNexus;
import org.adamalang.mysql.Base;
import org.adamalang.mysql.frontend.Spaces;
import org.adamalang.runtime.contracts.ExceptionLogger;
import org.adamalang.runtime.exceptions.ErrorCodeException;
import org.adamalang.transforms.results.SpacePolicy;
import org.adamalang.runtime.contracts.Callback;
import org.adamalang.web.io.AsyncTransform;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;

public class SpacePolicyLocator implements AsyncTransform<String, SpacePolicy> {
    public final Executor executor;
    public final Base base;
    private final ExceptionLogger logger;
    public final ConcurrentHashMap<String, SpacePolicy> policies;

    public SpacePolicyLocator(Executor executor, ExternNexus nexus) {
        this.executor = executor;
        this.base = nexus.base;
        this.policies = new ConcurrentHashMap<>();
        this.logger = nexus.makeLogger(SpacePolicyLocator.class);
    }

    @Override
    public void execute(String spaceName, Callback<SpacePolicy> callback) {
        // TODO: validate the space name
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
                callback.failure(ErrorCodeException.detectOrWrap(ErrorCodes.SPACE_POLICY_LOCATOR_UNKNOWN_EXCEPTION, ex, logger));
            }
        });
    }
}
