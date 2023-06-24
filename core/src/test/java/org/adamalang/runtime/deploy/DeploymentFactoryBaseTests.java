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
import org.adamalang.common.ErrorCodeException;
import org.adamalang.runtime.data.Key;
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.runtime.remote.Deliverer;
import org.adamalang.translator.jvm.LivingDocumentFactory;
import org.junit.Assert;
import org.junit.Test;

public class DeploymentFactoryBaseTests {
  @Test
  public void coverage_dumb() {
    DeploymentFactoryBase base = new DeploymentFactoryBase();
    base.fetch(
        new Key("space", "key"),
        new Callback<LivingDocumentFactory>() {
          @Override
          public void success(LivingDocumentFactory value) {
            Assert.fail();
          }

          @Override
          public void failure(ErrorCodeException ex) {
            Assert.assertEquals(134214, ex.code);
          }
        });
    Assert.assertEquals(0, base.spacesAvailable().size());
    Assert.assertNull(base.hashOf("space"));
    base.attachDeliverer(Deliverer.FAILURE);
    base.deliver(NtPrincipal.NO_ONE, new Key("space", "key"), 400, null, true, Callback.DONT_CARE_INTEGER);
    base.undeploy("space");
  }
}
