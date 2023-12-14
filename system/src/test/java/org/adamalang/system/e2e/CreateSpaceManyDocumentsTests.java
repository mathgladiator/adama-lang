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

import org.adamalang.api.*;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.Json;
import org.adamalang.common.Stream;
import org.adamalang.mysql.data.SpaceListingItem;
import org.adamalang.mysql.impl.MySQLFinderCore;
import org.adamalang.mysql.model.Capacity;
import org.adamalang.mysql.model.Hosts;
import org.adamalang.mysql.model.Spaces;
import org.adamalang.mysql.model.Users;
import org.adamalang.runtime.data.DocumentLocation;
import org.adamalang.runtime.data.Key;
import org.adamalang.runtime.sys.capacity.CapacityInstance;
import org.adamalang.system.BaseE2ETest;
import org.adamalang.system.TestEnvironment;
import org.checkerframework.checker.units.qual.K;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class CreateSpaceManyDocumentsTests extends BaseE2ETest {
  @Test
  public void flow() throws Exception {
    TestEnvironment env = TestEnvironment.ENV;
    Assert.assertNotNull(env);
    String identity = env.getIdentity("csmd1@adama.games");
    {
      ClientSpaceCreateRequest scr = new ClientSpaceCreateRequest();
      scr.identity = identity;
      scr.space = "test-space-csmd1";
      scr.template = "pubsub";
      CountDownLatch latch = new CountDownLatch(1);
      env.globalClient.spaceCreate(scr, new Callback<ClientSimpleResponse>() {
        @Override
        public void success(ClientSimpleResponse value) {
          latch.countDown();
        }

        @Override
        public void failure(ErrorCodeException ex) {
          System.err.println("spaceCreate; ERROR IN TEST:" + ex.code);
        }
      });
      Assert.assertTrue(latch.await(5000, TimeUnit.MILLISECONDS));
    }
    env.waitForCapacityReady("test-space-csmd1");
    CountDownLatch latch = new CountDownLatch(100);
    for (int k = 0; k < 100; k++) {
      CountDownLatch oneAtATime = new CountDownLatch(1);
      ClientDocumentCreateRequest docCreate = new ClientDocumentCreateRequest();
      docCreate.space = "test-space-csmd1";
      docCreate.arg = Json.newJsonObject();
      docCreate.key = "key-" + k;
      docCreate.identity = identity;
      env.globalClient.documentCreate(docCreate, new Callback<ClientSimpleResponse>() {
        @Override
        public void success(ClientSimpleResponse value) {
          latch.countDown();
          oneAtATime.countDown();
        }

        @Override
        public void failure(ErrorCodeException ex) {
          System.err.println("documentCreate; ERROR IN TEST:" + ex.code);
        }
      });
      Assert.assertTrue(oneAtATime.await(10000, TimeUnit.MILLISECONDS));
    }
    Assert.assertTrue(latch.await(60000, TimeUnit.MILLISECONDS));

    {
      ClientDocumentListRequest docsList = new ClientDocumentListRequest();
      docsList.identity = identity;
      docsList.space = "test-space-csmd1";
      ArrayList<ClientKeyListingResponse> responses = new ArrayList<>();
      CountDownLatch latchList = new CountDownLatch(1);
      env.globalClient.documentList(docsList, new Stream<ClientKeyListingResponse>() {
        @Override
        public void next(ClientKeyListingResponse value) {
          responses.add(value);
        }

        @Override
        public void complete() {
          latchList.countDown();
        }

        @Override
        public void failure(ErrorCodeException ex) {

        }
      });
      Assert.assertTrue(latchList.await(60000, TimeUnit.MILLISECONDS));
      Assert.assertEquals(100, responses.size());
    }

    MySQLFinderCore core = new MySQLFinderCore(env.db);
    HashMap<String, Integer> counts = new HashMap<>();
    for (int k = 0; k < 100; k++) {
      CountDownLatch latchX = new CountDownLatch(1);
      core.find(new Key("test-space-csmd1", "key-" + k), new Callback<DocumentLocation>() {
        @Override
        public void success(DocumentLocation value) {
          Integer prior = counts.get(value.machine);
          if (prior == null) {
            prior = 0;
          }
          counts.put(value.machine, prior + 1);
          latchX.countDown();
        }

        @Override
        public void failure(ErrorCodeException ex) {

        }
      });
      Assert.assertTrue(latchX.await(50000, TimeUnit.MILLISECONDS));
    }
    System.err.println(counts);



  }
}
