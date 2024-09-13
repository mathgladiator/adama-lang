/*
* Adama Platform and Language
* Copyright (C) 2021 - 2024 by Adama Platform Engineering, LLC
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
package org.adamalang.cli.implementations.code;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.Json;
import org.adamalang.runtime.data.Key;
import org.adamalang.runtime.deploy.AsyncByteCodeCache;
import org.adamalang.runtime.deploy.AsyncCompiler;
import org.adamalang.runtime.deploy.DeploymentFactory;
import org.adamalang.runtime.deploy.DeploymentPlan;
import org.adamalang.runtime.remote.Deliverer;
import org.adamalang.runtime.sys.LivingDocument;
import org.adamalang.translator.env.RuntimeEnvironment;
import org.adamalang.translator.jvm.LivingDocumentFactory;

import java.io.File;
import java.nio.file.Files;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class BenchmarkCommon {
  public static DeploymentPlan assemblePlan(String main, String imports) throws Exception {
      ObjectNode plan = Json.newJsonObject();
      plan.put("instrument", true);
      ObjectNode version = plan.putObject("versions").putObject("file");
      version.put("main", Files.readString(new File(main).toPath()));
      ObjectNode includes = version.putObject("includes");
      for (Map.Entry<String, String> entry : Imports.get(imports).entrySet()) {
        includes.put(entry.getKey(), entry.getValue());
      }
      plan.put("default", "file");
      plan.putArray("plan");
      return new DeploymentPlan(plan.toString(), (x, y) -> {});
  }

  public static LivingDocumentFactory makeFactory(String space, String key, DeploymentPlan deploymentPlan) throws Exception {
    AtomicReference<LivingDocumentFactory> factory = new AtomicReference<>();
    CountDownLatch latch = new CountDownLatch(1);
    new Thread(() -> AsyncCompiler.forge(RuntimeEnvironment.Beta, "Benchmark", null, deploymentPlan, Deliverer.FAILURE, new TreeMap<>(), AsyncByteCodeCache.DIRECT, new Callback<DeploymentFactory>() {
      @Override
      public void success(DeploymentFactory df) {
        df.fetch(new Key(space, key), new Callback<LivingDocumentFactory>() {
          @Override
          public void success(LivingDocumentFactory ldf) {
            factory.set(ldf);
            latch.countDown();
          }

          @Override
          public void failure(ErrorCodeException ex) {
            System.err.println("FETCH FAILED:" + ex.code);
            latch.countDown();
          }
        });
      }

      @Override
      public void failure(ErrorCodeException ex) {
        System.err.println("FORGE FAILED:" + ex.code);
        latch.countDown();
      }
    })).start();
    System.out.print("compiling:");
    while (!latch.await(100, TimeUnit.MILLISECONDS)) {
      System.out.print(".");
    }
    System.out.println("done!");
    return factory.get();
  }
}
