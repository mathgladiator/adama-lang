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
package org.adamalang.system.distributed;

import org.adamalang.common.*;
import org.adamalang.common.metrics.MetricsFactory;
import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.adamalang.mysql.data.SimpleSpaceInfo;
import org.adamalang.mysql.model.Spaces;
import org.adamalang.runtime.deploy.*;
import org.adamalang.runtime.remote.Deliverer;
import org.adamalang.services.FirstPartyServices;
import org.adamalang.system.common.CloudBoot;
import org.adamalang.system.common.DataBaseBoot;
import org.adamalang.system.contracts.JsonConfig;
import org.adamalang.web.client.WebClientBase;
import org.adamalang.web.client.WebClientBaseMetrics;
import org.adamalang.web.service.WebConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.TreeMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class PrepareStep {
  private static final Logger LOG = LoggerFactory.getLogger(PrepareStep.class);
  private static final ExceptionLogger EXLOG = ExceptionLogger.FOR(LOG);

  public static void run(JsonConfig config) throws Exception {
    AtomicBoolean alive = new AtomicBoolean(true);
    SimpleExecutor executor = SimpleExecutor.create("system");
    MetricsFactory metrics = new NoOpMetricsFactory();
    WebClientBase webClientBase = new WebClientBase(new WebClientBaseMetrics(metrics), new WebConfig(new ConfigObject(config.get_or_create_child("web"))));
    DataBaseBoot db = new DataBaseBoot(alive, config, metrics, executor);
    CloudBoot cb = new CloudBoot(alive, metrics, webClientBase, config.get_or_create_child("aws"), "prepare-step", executor);
    ArrayList<SimpleSpaceInfo> spaces = Spaces.listAllSpaces(db.database);
    FirstPartyServices.install(executor, executor, metrics, webClientBase, null, null);
    for (SimpleSpaceInfo space : spaces) {
      System.out.print("\u001b[33mCompile[\u001b[37m" + space.name + "\u001b[33m]\u001b[39m.");
      try {
        DeploymentPlan plan = new DeploymentPlan(Spaces.getPlan(db.database, space.id), EXLOG);
        ManagedAsyncByteCodeCache cache = new ManagedAsyncByteCodeCache(cb.s3);
        CountDownLatch latch = new CountDownLatch(1);
        AsyncCompiler.forge(space.name, null, plan, Deliverer.FAILURE, new TreeMap<>(), cache, new Callback<>() {
          @Override
          public void success(DeploymentFactory value) {
            System.out.println("\u001b[32mSUCCESS\u001b[39m");
            latch.countDown();
          }

          @Override
          public void failure(ErrorCodeException ex) {
            System.out.println("\u001b[31mFAIL[" + ex.code + "]\u001b[39m");
            latch.countDown();
          }
        });
        int ticks = 120;
        while (!latch.await(1000, TimeUnit.MILLISECONDS) && ticks > 0) {
          System.out.print(".");
          ticks--;
        }
        if (!latch.await(1000, TimeUnit.MILLISECONDS)) {
          System.out.println("\u001b[35mTIMEOUT!\u001b[39m");
        }
      } catch (Exception ex){
        System.out.println("\u001b[31mFAIL[" + ex.getMessage() + "]\u001b[39m");
      }
    }
  }
}
