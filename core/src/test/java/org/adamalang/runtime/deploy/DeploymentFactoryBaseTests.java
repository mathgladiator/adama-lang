package org.adamalang.runtime.deploy;

import org.adamalang.runtime.contracts.Callback;
import org.adamalang.runtime.contracts.Key;
import org.adamalang.runtime.exceptions.ErrorCodeException;
import org.adamalang.translator.jvm.LivingDocumentFactory;
import org.junit.Assert;
import org.junit.Test;

public class DeploymentFactoryBaseTests {
    @Test
    public void notFound() {
        DeploymentFactoryBase base = new DeploymentFactoryBase();
        base.fetch(new Key("space", "key"), new Callback<LivingDocumentFactory>() {
            @Override
            public void success(LivingDocumentFactory value) {
                Assert.fail();
            }

            @Override
            public void failure(ErrorCodeException ex) {
                Assert.assertEquals(134214, ex.code);
            }
        });
    }
}
