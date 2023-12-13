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

import com.lambdaworks.crypto.SCryptUtil;
import org.adamalang.api.ClientAccountLoginRequest;
import org.adamalang.api.ClientInitSetupAccountRequest;
import org.adamalang.api.ClientInitiationResponse;
import org.adamalang.api.ClientSimpleResponse;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.mysql.data.SpaceInfo;
import org.adamalang.mysql.data.SpaceListingItem;
import org.adamalang.mysql.model.Spaces;
import org.adamalang.mysql.model.Users;
import org.adamalang.system.BaseE2ETest;
import org.adamalang.system.TestEnvironment;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class SimpleTests extends BaseE2ETest {
  @Test
  public void init_to_login() throws Exception {
    TestEnvironment env = TestEnvironment.ENV;
    Assert.assertNotNull(env);
    ClientInitSetupAccountRequest cis = new ClientInitSetupAccountRequest();
    cis.email = "tester@adama.games";
    {
      CountDownLatch latch = new CountDownLatch(1);
      AtomicBoolean success = new AtomicBoolean(false);
      env.globalClient.initSetupAccount(cis, new Callback<ClientSimpleResponse>() {
        @Override
        public void success(ClientSimpleResponse value) {
          System.err.println("-=[INIT ACCOUNT]=-");
          latch.countDown();
          success.set(true);
        }

        @Override
        public void failure(ErrorCodeException ex) {
          System.err.println("FAILED:" + ex.code);
          latch.countDown();
        }
      });
      Assert.assertTrue(latch.await(30000, TimeUnit.MILLISECONDS));
      Assert.assertTrue(success.get());
    }
    // POKE BEHIND THE SCENES TO SET THE PASSWORD
    int userId = Users.getUserId(env.db, "tester@adama.games");
    Users.setPasswordHash(env.db, userId, SCryptUtil.scrypt("password", 16384, 8, 1));
    for (SpaceListingItem info : Spaces.list(env.db, 1, null, 100)) {
      System.err.println(info.name);
    }
    {
      CountDownLatch latch = new CountDownLatch(1);
      AtomicBoolean success = new AtomicBoolean(false);
      ClientAccountLoginRequest calr = new ClientAccountLoginRequest();
      calr.email = "tester@adama.games";
      calr.password = "password";
      env.globalClient.accountLogin(calr, new Callback<ClientInitiationResponse>() {
        @Override
        public void success(ClientInitiationResponse value) {
          System.err.println("-=[LOGIN!]=-");
          latch.countDown();
          success.set(true);
        }

        @Override
        public void failure(ErrorCodeException ex) {
          latch.countDown();
        }
      });
      Assert.assertTrue(latch.await(5000, TimeUnit.MILLISECONDS));
      Assert.assertTrue(success.get());
    }
  }
}
