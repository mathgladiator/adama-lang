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

import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpMethod;
import org.adamalang.api.*;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.Json;
import org.adamalang.common.Stream;
import org.adamalang.common.template.tree.T;
import org.adamalang.mysql.impl.MySQLFinderCore;
import org.adamalang.runtime.data.DocumentLocation;
import org.adamalang.runtime.data.Key;
import org.adamalang.runtime.sys.capacity.CurrentLoad;
import org.adamalang.system.BaseE2ETest;
import org.adamalang.system.TestEnvironment;
import org.adamalang.system.support.TestClient;
import org.junit.Assert;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class CreateWebSiteTests extends BaseE2ETest {
  @Test
  public void flow() throws Exception {
    TestEnvironment env = TestEnvironment.ENV;
    Assert.assertNotNull(env);
    String identity = env.getIdentity("csmd2@adama.games");
    {
      ClientSpaceCreateRequest scr = new ClientSpaceCreateRequest();
      scr.identity = identity;
      scr.space = "testweb";
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
    env.waitForCapacityReady("testweb");
    {
      ClientSpaceSetRequest setRequest = new ClientSpaceSetRequest();
      setRequest.identity = identity;
      setRequest.space = "testweb";
      setRequest.plan = Json.newJsonObject();
      StringBuilder script = new StringBuilder();
      script.append("@static { invent { return true; } create { return true; } }");
      script.append("@web options / { return { cors:true }; }");
      script.append("@web get / { return { html: \"Hello World\" }; }");
      setRequest.plan.putObject("versions").putObject("x").put("main", script.toString());
      setRequest.plan.put("default", "x");

      CountDownLatch latchSet = new CountDownLatch(1);
      AtomicBoolean success = new AtomicBoolean(false);
      env.globalClient.spaceSet(setRequest, new Callback<ClientSimpleResponse>() {
        @Override
        public void success(ClientSimpleResponse value) {
          success.set(true);
          latchSet.countDown();
        }

        @Override
        public void failure(ErrorCodeException ex) {
          ex.printStackTrace();
          latchSet.countDown();
        }
      });
      Assert.assertTrue(latchSet.await(50000, TimeUnit.MILLISECONDS));
      Assert.assertTrue(success.get());
    }

    { // GRAB THE RxHTML
      AtomicBoolean success = new AtomicBoolean(false);
      CountDownLatch latch = new CountDownLatch(1);
      TestClient tc = env.newClient();
      tc.headers.add("Host", "testweb.adama.games");
      tc.uri("/");
      tc.execute(new Callback<FullHttpResponse>() {
        @Override
        public void success(FullHttpResponse value) {
          byte[] body = new byte[value.content().readableBytes()];
          value.content().readBytes(body);
          System.err.println(new String(body, StandardCharsets.UTF_8));
          System.err.println(value);
          success.set(true);
          latch.countDown();
        }

        @Override
        public void failure(ErrorCodeException ex) {
          ex.printStackTrace();
          latch.countDown();
        }
      });
      Assert.assertTrue(latch.await(50000, TimeUnit.MILLISECONDS));
      Assert.assertTrue(success.get());
    }

    { // EXECUTE THE WEB GET
      AtomicBoolean success = new AtomicBoolean(false);
      CountDownLatch latch = new CountDownLatch(1);
      TestClient tc = env.newClient();
      tc.headers.add("Host", "testweb.adama.games");
      tc.uri("/~d/");
      tc.execute(new Callback<FullHttpResponse>() {
        @Override
        public void success(FullHttpResponse value) {
          byte[] body = new byte[value.content().readableBytes()];
          value.content().readBytes(body);
          System.err.println(new String(body, StandardCharsets.UTF_8));
          System.err.println(value);
          success.set(true);
          latch.countDown();
        }

        @Override
        public void failure(ErrorCodeException ex) {
          ex.printStackTrace();
          latch.countDown();
        }
      });
      Assert.assertTrue(latch.await(50000, TimeUnit.MILLISECONDS));
      Assert.assertTrue(success.get());
    }

    { // EXECUTE OPTIONS ON ROOT
      AtomicBoolean success = new AtomicBoolean(false);
      CountDownLatch latch = new CountDownLatch(1);
      TestClient tc = env.newClient();
      tc.headers.add("Host", "testweb.adama.games");
      tc.uri("/~d/");
      tc.method(HttpMethod.OPTIONS);
      tc.execute(new Callback<FullHttpResponse>() {
        @Override
        public void success(FullHttpResponse value) {
          byte[] body = new byte[value.content().readableBytes()];
          value.content().readBytes(body);
          System.err.println(new String(body, StandardCharsets.UTF_8));
          System.err.println(value);
          success.set(true);
          latch.countDown();
        }

        @Override
        public void failure(ErrorCodeException ex) {
          ex.printStackTrace();
          latch.countDown();
        }
      });
      Assert.assertTrue(latch.await(50000, TimeUnit.MILLISECONDS));
      Assert.assertTrue(success.get());
    }
  }
}
