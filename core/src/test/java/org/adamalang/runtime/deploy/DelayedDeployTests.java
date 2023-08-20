/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.runtime.deploy;

import org.adamalang.common.Callback;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

public class DelayedDeployTests {
  @Test
  public void flow() {
    DelayedDeploy dd = new DelayedDeploy();
    dd.deploy("space", Callback.DONT_CARE_VOID);
    ArrayList<String> deployed = new ArrayList<>();
    dd.set(new Deploy() {
      @Override
      public void deploy(String space, Callback<Void> callback) {
        deployed.add(space);
        callback.success(null);
      }
    });
    Assert.assertEquals(1, deployed.size());
    dd.deploy("now", Callback.DONT_CARE_VOID);
    Assert.assertEquals(2, deployed.size());
    Assert.assertEquals("space", deployed.get(0));
    Assert.assertEquals("now", deployed.get(1));
  }
}
