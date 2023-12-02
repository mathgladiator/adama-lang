/*
* Adama Platform and Language
* Copyright (C) 2021 - 2023 by Adama Platform Initiative, LLC
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as published
* by the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Affero General Public License for more details.
* 
* You should have received a copy of the GNU Affero General Public License
* along with this program.  If not, see <https://www.gnu.org/licenses/>.
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
    DeploymentFactoryBase base = new DeploymentFactoryBase(AsyncByteCodeCache.DIRECT);
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
