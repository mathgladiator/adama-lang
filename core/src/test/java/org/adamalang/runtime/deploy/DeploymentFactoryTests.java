/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.runtime.deploy;

import org.adamalang.common.ErrorCodeException;
import org.adamalang.runtime.remote.Deliverer;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

public class DeploymentFactoryTests {

  @Test
  public void cantParse() throws Exception {
    DeploymentPlan plan =
        new DeploymentPlan(
            "{\"versions\":{\"x\":\"@con\"},\"default\":\"x\",\"plan\":[{\"version\":\"x\",\"percent\":50,\"prefix\":\"k\",\"seed\":\"a2\"}]}",
            (t, errorCode) -> {});

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
    DeploymentPlan plan =
        new DeploymentPlan(
            "{\"versions\":{\"x\":\"public int x = true;\"},\"default\":\"x\",\"plan\":[{\"version\":\"x\",\"percent\":50,\"prefix\":\"k\",\"seed\":\"a2\"}]}",
            (t, errorCode) -> {});

    DeploymentFactoryBase base = new DeploymentFactoryBase();
    try {
      base.deploy("space", plan);
      Assert.fail();
    } catch (ErrorCodeException ex) {
      Assert.assertEquals(132157, ex.code);
    }
  }

  @Test
  public void happy() throws Exception {
    DeploymentPlan plan =
        new DeploymentPlan(
            "{\"versions\":{\"x\":\"public int x = 123;\"},\"default\":\"x\",\"plan\":[{\"version\":\"x\",\"percent\":50,\"prefix\":\"k\",\"seed\":\"a2\"}]}",
            (t, errorCode) -> {});
    DeploymentFactoryBase base = new DeploymentFactoryBase();
    base.deploy("space", plan);
    Assert.assertEquals("0w9NHaDbD2fTGSLlHuGyCQ==", base.hashOf("space"));
  }

  @Test
  public void rxhtml() throws Exception {
    DeploymentPlan plan =
        new DeploymentPlan(
            "{\"versions\":{\"x\":{\"main\":\"public int x = 123;\",\"rxhtml\":\"<forest><page uri=\\\"/\\\">Hello World</page></forest>\"}},\"default\":\"x\",\"plan\":[]}",
            (t, errorCode) -> {});
    DeploymentFactoryBase base = new DeploymentFactoryBase();
    base.deploy("space", plan);
    Assert.assertEquals("W9ngBjBRMNDRTZY3N3OjKA==", base.hashOf("space"));
  }

  @Test
  public void happyDirect() throws Exception {
    DeploymentPlan plan =
        new DeploymentPlan(
            "{\"versions\":{\"x\":\"public int x = 123;\"},\"default\":\"x\",\"plan\":[{\"version\":\"x\",\"percent\":50,\"prefix\":\"k\",\"seed\":\"a2\"}]}",
            (t, errorCode) -> {});
    DeploymentFactory newFactory =
        new DeploymentFactory("space", "Space_", new AtomicInteger(1000), null, plan, Deliverer.FAILURE);
    Assert.assertEquals(1, newFactory.spacesAvailable().size());
  }
}
