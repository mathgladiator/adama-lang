package org.adamalang.runtime.deploy;

import org.adamalang.common.ErrorCodeException;
import org.junit.Assert;
import org.junit.Test;

public class DeploymentFactoryTests {

    @Test
    public void cantParse() throws Exception {
        DeploymentPlan plan = new DeploymentPlan("{\"versions\":{\"x\":\"@con\"},\"default\":\"x\",\"plan\":[{\"version\":\"x\",\"percent\":50,\"prefix\":\"k\",\"seed\":\"a2\"}]}", (t, errorCode) -> {

        });
        DeploymentFactoryBase base = new DeploymentFactoryBase();
        try {
            base.deploy("space", plan);
            Assert.fail();
        } catch (ErrorCodeException ex) {
            Assert.assertEquals(117823, ex.code);
        }
    }

    @Test
    public void cantType() throws Exception {
        DeploymentPlan plan = new DeploymentPlan("{\"versions\":{\"x\":\"public int x = true;\"},\"default\":\"x\",\"plan\":[{\"version\":\"x\",\"percent\":50,\"prefix\":\"k\",\"seed\":\"a2\"}]}", (t, errorCode) -> {

        });
        DeploymentFactoryBase base = new DeploymentFactoryBase();
        try {
            base.deploy("space", plan);
            Assert.fail();
        } catch (ErrorCodeException ex) {
            Assert.assertEquals(132157, ex.code);
        }
    }
}
