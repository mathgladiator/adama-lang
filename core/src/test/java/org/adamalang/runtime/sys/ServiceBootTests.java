/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.runtime.sys;

import org.adamalang.common.Callback;
import org.adamalang.runtime.deploy.MockDeploy;
import org.adamalang.runtime.sys.capacity.MockCapacityOverseer;
import org.junit.Assert;
import org.junit.Test;

public class ServiceBootTests {

  @Test
  public void coverage() {
    MockCapacityOverseer overseer = new MockCapacityOverseer();
    overseer.add("a", "region", "machine", Callback.DONT_CARE_VOID);
    overseer.add("b", "region", "machine", Callback.DONT_CARE_VOID);
    MockDeploy deploy = new MockDeploy();
    ServiceBoot.initializeWithDeployments("region", "machine", overseer, deploy, 5000);
    Assert.assertEquals(2, deploy.deployed.size());
  }
}
