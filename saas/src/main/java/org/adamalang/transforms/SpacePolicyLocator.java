package org.adamalang.transforms;

import org.adamalang.ErrorCodes;
import org.adamalang.extern.ExternNexus;
import org.adamalang.mysql.DataBase;
import org.adamalang.mysql.frontend.Spaces;
import org.adamalang.common.ExceptionLogger;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.transforms.results.SpacePolicy;
import org.adamalang.common.Callback;
import org.adamalang.web.io.AsyncTransform;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;

public class SpacePolicyLocator implements AsyncTransform<String, SpacePolicy> {
    public final Executor executor;
    public final DataBase dataBase;
    private final ExceptionLogger logger;
    public final ConcurrentHashMap<String, SpacePolicy> policies;

    public SpacePolicyLocator(Executor executor, ExternNexus nexus) {
        this.executor = executor;
        this.dataBase = nexus.dataBase;
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
                Spaces.Space space = Spaces.getSpaceId(dataBase, spaceName);
                policies.putIfAbsent(spaceName, new SpacePolicy(space));
                callback.success(policies.get(spaceName));
            } catch (Exception ex) {
                callback.failure(ErrorCodeException.detectOrWrap(ErrorCodes.SPACE_POLICY_LOCATOR_UNKNOWN_EXCEPTION, ex, logger));
            }
        });
    }
}
