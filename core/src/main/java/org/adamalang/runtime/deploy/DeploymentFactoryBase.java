package org.adamalang.runtime.deploy;

import org.adamalang.ErrorCodes;
import org.adamalang.runtime.contracts.Callback;
import org.adamalang.runtime.contracts.Key;
import org.adamalang.runtime.contracts.LivingDocumentFactoryFactory;
import org.adamalang.runtime.exceptions.ErrorCodeException;
import org.adamalang.translator.jvm.LivingDocumentFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/** this is the base for all spaces to resolve against */
public class DeploymentFactoryBase implements LivingDocumentFactoryFactory {
    private final AtomicInteger newClassId;
    private final ConcurrentHashMap<String, DeploymentFactory> spaces;

    public DeploymentFactoryBase() {
        this.newClassId = new AtomicInteger(0);
        this.spaces = new ConcurrentHashMap<>();
    }

    public void deploy(String space, DeploymentPlan plan) throws ErrorCodeException {
        DeploymentFactory prior = spaces.get(space);
        StringBuilder spacePrefix = new StringBuilder().append("Space_");
        for (int k = 0; k < space.length(); k++) {
            char ch = space.charAt(k);
            if (Character.isAlphabetic(ch)) {
                spacePrefix.append(ch);
            }
        }
        spacePrefix.append("_");
        DeploymentFactory newFactory = new DeploymentFactory(spacePrefix.toString(), newClassId, prior, plan);
        spaces.put(space, newFactory);
    }

    @Override
    public void fetch(Key key, Callback<LivingDocumentFactory> callback) {
        DeploymentFactory factory = spaces.get(key.space);
        if (factory == null) {
            callback.failure(new ErrorCodeException(ErrorCodes.DEPLOYMENT_FACTORY_CANT_FIND_SPACE));
            return;
        }
        factory.fetch(key, callback);
    }
}
