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
package org.adamalang.system.e2e;

import org.adamalang.api.ClientDocumentCreateRequest;
import org.adamalang.api.ClientSimpleResponse;
import org.adamalang.api.ClientSpaceCreateRequest;
import org.adamalang.api.SpaceCreateRequest;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.Json;
import org.adamalang.system.BaseE2ETest;
import org.adamalang.system.TestEnvironment;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class CreateSpaceManyDocumentsTests extends BaseE2ETest {
  // @Test
  public void flow() throws Exception {
    TestEnvironment env = TestEnvironment.ENV;
    Assert.assertNotNull(env);
    String identity = env.getIdentity("csmd1@adama.games");
    {
      ClientSpaceCreateRequest scr = new ClientSpaceCreateRequest();
      scr.identity = identity;
      scr.space = "test-space-csmd1";
      scr.template = "webapp";
      CountDownLatch latch = new CountDownLatch(1);
      env.globalClient.spaceCreate(scr, new Callback<ClientSimpleResponse>() {
        @Override
        public void success(ClientSimpleResponse value) {
          latch.countDown();
        }

        @Override
        public void failure(ErrorCodeException ex) {

        }
      });
      Assert.assertTrue(latch.await(5000, TimeUnit.MILLISECONDS));
    }

    CountDownLatch latch = new CountDownLatch(1);
    for (int k = 0; k < 100; k++) {
      ClientDocumentCreateRequest docCreate = new ClientDocumentCreateRequest();
      docCreate.space = "test-space-csmd1";
      docCreate.arg = Json.newJsonObject();
      docCreate.key = "key-" + k;
      docCreate.identity = identity;
      env.globalClient.documentCreate(docCreate, new Callback<ClientSimpleResponse>() {
        @Override
        public void success(ClientSimpleResponse value) {
          latch.countDown();
        }

        @Override
        public void failure(ErrorCodeException ex) {

        }
      });
    }
    Assert.assertTrue(latch.await(60000, TimeUnit.MILLISECONDS));


  }
}
