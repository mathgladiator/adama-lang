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
import io.netty.buffer.Unpooled;
import org.adamalang.CoreServices;
import org.adamalang.CoreServicesNexus;
import org.adamalang.caravan.events.EventCodec;
import org.adamalang.caravan.events.Events;
import org.adamalang.caravan.events.RestoreLoader;
import org.adamalang.cli.router.Arguments;
import org.adamalang.cli.runtime.Output;
import org.adamalang.common.Json;
import org.adamalang.runtime.deploy.DeploymentPlan;
import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.remote.Deliverer;
import org.adamalang.runtime.remote.ServiceRegistry;
import org.adamalang.runtime.sys.LivingDocument;
import org.adamalang.translator.jvm.LivingDocumentFactory;

import java.io.File;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class BenchmarkArchiveReplay {
  public static void go(Arguments.CodeBenchmarkArchiveReplayArgs args, Output.YesOrError output) throws Exception {
    CoreServices.install(CoreServicesNexus.NOOP());
    ObjectNode report = Json.newJsonObject();
    long time = System.currentTimeMillis();
    final DeploymentPlan deploymentPlan = BenchmarkCommon.assemblePlan(args.main, args.imports);
    LivingDocumentFactory factory = BenchmarkCommon.makeFactory(args.space, args.key, deploymentPlan);
    LivingDocument doc = factory.create(null);
    doc.__lateBind(args.space, args.key, Deliverer.FAILURE, ServiceRegistry.NOT_READY);


    ArrayList<byte[]> writes = RestoreLoader.load(new File(args.data));

    EventCodec.HandlerEvent event = new EventCodec.HandlerEvent() {
      boolean loaded = false;
      @Override
      public void handle(Events.Snapshot snapshot) {
        if (!loaded) {
          loaded = true;
          doc.__insert(new JsonStreamReader(snapshot.document));
          System.out.println("inserted snapshot:" + snapshot.seq);
        } else {
          System.out.println("ignore snapshot:" + snapshot.seq);
        }
      }

      @Override
      public void handle(Events.Batch batch) {
        for (Events.Change change : batch.changes) {
          handle(change);
        }
      }

      @Override
      public void handle(Events.Recover payload) {
      }

      @Override
      public void handle(Events.Change change) {
        if (loaded) {
          String command = Json.parseJsonObject(change.request).get("command").textValue();
          if ("send".equals(command)) {
            long started = System.currentTimeMillis();
            try {
              doc.__transact(change.request, factory);
            } catch (Throwable failed) {
            }
            long delta = System.currentTimeMillis() - started;
            if (delta > 500) {
              System.out.println(change.request + "::" + delta);
            }
          }
          doc.__patch(new JsonStreamReader(change.redo));
          doc.__commit("x", new JsonStreamWriter(), new JsonStreamWriter());
        }
      }
    };

    for (byte[] write : writes) {
      EventCodec.route(Unpooled.wrappedBuffer(write), event);
    }
  }
}
