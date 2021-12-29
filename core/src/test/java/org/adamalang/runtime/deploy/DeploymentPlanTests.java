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

public class DeploymentPlanTests {

    public void parseTest(String json, int expectedError) {
        try {
            new DeploymentPlan(json, (t, errorCode) -> {

            });
            Assert.fail();
        } catch (ErrorCodeException ex) {
            Assert.assertEquals(expectedError, ex.code);
        }
    }

    @Test
    public void badHash() {
        try {
            DeploymentPlan.hash(null, null);
            Assert.fail();
        } catch (RuntimeException ex) {

        }
    }
    @Test
    public void happy() throws Exception {
        new DeploymentPlan("{\"versions\":{\"x\":\"\"},\"default\":\"x\",\"plan\":[{\"version\":\"x\",\"percent\":50,\"prefix\":\"k\",\"seed\":\"a2\"}]}", (t, errorCode) -> {

        });
    }
    @Test
    public void stage_percent_bad() {
        parseTest("{\"versions\":{\"x\":\"\"},\"default\":\"x\",\"plan\":[{\"version\":\"x\",\"percent\":\"x\"}]}", 151615);
    }
    @Test
    public void stage_no_version() {
        parseTest("{\"versions\":{\"x\":\"\"},\"default\":\"x\",\"plan\":[{}]}", 199768);
    }
    @Test
    public void stage_version_doest_exist() {
        parseTest("{\"versions\":{\"x\":\"\"},\"default\":\"x\",\"plan\":[{\"version\":\"y\"}]}", 120895);
    }
    @Test
    public void stage_bad_field() {
        parseTest("{\"versions\":{\"x\":\"\"},\"default\":\"x\",\"plan\":[{\"version\":\"x\",\"z\":true}]}", 116812);
    }
    @Test
    public void stage_bad_element() {
        parseTest("{\"versions\":{\"x\":\"\"},\"default\":\"x\",\"plan\":[true]}", 176703);
    }
    @Test
    public void plan_bad_type() {
        parseTest("{\"versions\":{\"x\":\"\"},\"default\":\"x\",\"plan\":\"z\"}", 126012);
    }
    @Test
    public void no_default() {
        parseTest("{\"versions\":{\"x\":\"\"}}", 143948);
    }
    @Test
    public void default_invalid() {
        parseTest("{\"versions\":{\"x\":\"\"},\"default\":\"y\"}", 145980);
    }
    @Test
    public void no_versions() {
        parseTest("{}", 115788);
    }
    @Test
    public void empty_versions() {
        parseTest("{\"versions\":{}}", 115788);
    }
    @Test
    public void bad_versions_type() {
        parseTest("{\"versions\":true}", 155711);
    }
    @Test
    public void invalid_field() {
        parseTest("{\"x\":{}}", 143430);
    }
    @Test
    public void must_be_obj() {
        parseTest("[]", 117818);
    }
    @Test
    public void must_be_json() {
        parseTest("x", 116812);
    }
}
