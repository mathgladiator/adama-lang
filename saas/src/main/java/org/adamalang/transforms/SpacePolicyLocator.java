package org.adamalang.transforms;

import org.adamalang.transforms.results.SpacePolicy;
import org.adamalang.runtime.contracts.Callback;
import org.adamalang.web.io.AsyncTransform;

public class SpacePolicyLocator implements AsyncTransform<String, SpacePolicy> {
    @Override
    public void execute(String parameter, Callback<SpacePolicy> callback) {
        callback.success(null);
    }
}
