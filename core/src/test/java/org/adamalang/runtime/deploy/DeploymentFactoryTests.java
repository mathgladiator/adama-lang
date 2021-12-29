/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
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
