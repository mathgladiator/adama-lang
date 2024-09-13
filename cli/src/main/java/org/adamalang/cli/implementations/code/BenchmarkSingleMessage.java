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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.CoreServices;
import org.adamalang.CoreServicesNexus;
import org.adamalang.cli.router.Arguments;
import org.adamalang.cli.runtime.Output;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.Json;
import org.adamalang.runtime.contracts.Perspective;
import org.adamalang.runtime.deploy.DeploymentPlan;
import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.runtime.sys.LivingDocument;
import org.adamalang.runtime.sys.LivingDocumentChange;
import org.adamalang.runtime.sys.PerfTracker;
import org.adamalang.translator.jvm.LivingDocumentFactory;

import java.io.File;
import java.nio.file.Files;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Supplier;

public class BenchmarkSingleMessage {
  public static void go(Arguments.CodeBenchmarkMessageArgs args, Output.YesOrError output) throws Exception {
    CoreServices.install(CoreServicesNexus.NOOP());
    ObjectNode report = Json.newJsonObject();
    long time = System.currentTimeMillis();
    final DeploymentPlan deploymentPlan = BenchmarkCommon.assemblePlan(args.main, args.imports);
    ObjectNode instruction = Json.parseJsonObject(Files.readString(new File(args.message).toPath()));
    LivingDocumentFactory factory = BenchmarkCommon.makeFactory(instruction.get("space").textValue(), instruction.get("key").textValue(), deploymentPlan);
    LivingDocument doc = factory.create(null);
    NtPrincipal who = new NtPrincipal(instruction.get("agent").textValue(), instruction.get("authority").textValue());
    Supplier<ObjectNode> snap = () -> {
      JsonStreamWriter writer = new JsonStreamWriter();
      doc.__writeRxReport(writer);
      return Json.parseJsonObject(writer.toString());
    };
    System.out.println("[inserting]");
    long loadStart = System.currentTimeMillis();
    String jsonLoad = Files.readString(new File(args.data).toPath());
    report.put("load-time-ms", System.currentTimeMillis() - loadStart);
    long parseStart = System.currentTimeMillis();
    doc.__insert(new JsonStreamReader(jsonLoad));
    report.put("parse-time-ms", System.currentTimeMillis() - parseStart);
    {
      JsonStreamWriter writer = new JsonStreamWriter();
      long dumpStart = System.currentTimeMillis();
      doc.__dump(writer);
      report.put("dump-size-bytes", writer.toString().length());
      report.put("dump-time-ms", System.currentTimeMillis() - dumpStart);
      report.put("memory-size-bytes", doc.__memory());
    }
    doc.__perf.dump(10.0);
    doc.__perf.measureLightning();
    if (instruction.has("connect") && instruction.get("connect").booleanValue()){
      System.out.println("[connecting]");
      final var writer = new JsonStreamWriter();
      writer.beginObject();
      writer.writeObjectFieldIntro("command");
      writer.writeFastString("connect");
      writer.writeObjectFieldIntro("timestamp");
      writer.writeLong(time);
      writer.writeObjectFieldIntro("who");
      writer.writeNtPrincipal(who);
      writer.writeObjectFieldIntro("key");
      writer.writeString(instruction.get("key").textValue());
      writer.writeObjectFieldIntro("origin");
      writer.writeString("origin");
      writer.writeObjectFieldIntro("ip");
      writer.writeString("0.0.0.0");
      writer.endObject();
      doc.__transact(writer.toString(), factory);
      report.set("connect", Json.parseJsonObject(doc.__perf.dump(5.0)));
      report.set("connect-strike", filteredLightning(doc.__perf));
      report.put("post-connect-memory-size-bytes", doc.__memory());
    }
    doc.__createView(who, Perspective.DEAD);
    Runnable invalidate = () -> {
      System.out.println("[invalidate]");
      final var writer = new JsonStreamWriter();
      writer.beginObject();
      writer.writeObjectFieldIntro("command");
      writer.writeFastString("invalidate");
      writer.writeObjectFieldIntro("timestamp");
      writer.writeLong(time);
      writer.writeObjectFieldIntro("who");
      writer.writeNtPrincipal(who);
      writer.writeObjectFieldIntro("key");
      writer.writeString(instruction.get("key").textValue());
      writer.writeObjectFieldIntro("origin");
      writer.writeString("origin");
      writer.writeObjectFieldIntro("ip");
      writer.writeString("0.0.0.0");
      writer.endObject();
      try {
        doc.__transact(writer.toString(), factory);
      } catch (ErrorCodeException e) {
        throw new RuntimeException(e);
      }
    };
    {
      invalidate.run();
      report.set("invalidate", Json.parseJsonObject(doc.__perf.dump(5.0)));
      report.set("invalidate-strike", filteredLightning(doc.__perf));
      report.put("post-invalidate-memory-size-bytes", doc.__memory());
    }
    {
      invalidate.run();
      report.set("invalidate-again", Json.parseJsonObject(doc.__perf.dump(5.0)));
      report.set("invalidate-again-strike", filteredLightning(doc.__perf));
      report.put("post-invalidate-again-memory-size-bytes", doc.__memory());
    }
    if (instruction.has("channel")) {
      System.out.println("[send]");
      final var writer = new JsonStreamWriter();
      writer.beginObject();
      writer.writeObjectFieldIntro("command");
      writer.writeFastString("send");
      writer.writeObjectFieldIntro("timestamp");
      writer.writeLong(time);
      writer.writeObjectFieldIntro("who");
      writer.writeNtPrincipal(who);
      writer.writeObjectFieldIntro("key");
      writer.writeString(instruction.get("key").textValue());
      writer.writeObjectFieldIntro("origin");
      writer.writeString("origin");
      writer.writeObjectFieldIntro("ip");
      writer.writeString("0.0.0.0");
      writer.writeObjectFieldIntro("channel");
      writer.writeFastString(instruction.get("channel").textValue());
      writer.writeObjectFieldIntro("message");
      writer.injectJson(instruction.get("message").toString());
      writer.endObject();
      LivingDocumentChange change = doc.__transact(writer.toString(), factory);
      report.set("send", Json.parseJsonObject(doc.__perf.dump(5.0)));
      report.set("send-strike", filteredLightning(doc.__perf));
      if (change != null) {
        report.set("send-redo", Json.parseJsonObject(change.update.redo));
        report.set("send-undo", Json.parseJsonObject(change.update.undo));
      }
    }
    doc.__settle(Collections.emptySet());
    invalidate.run();
    report.set("rx-report", snap.get());
    Files.writeString(new File(args.dumpTo).toPath(), report.toPrettyString());
    output.out();
  }
  private static ObjectNode cloneFilteredLightning(ObjectNode child) {
    if (child.has("__ms")) {
      if (child.get("__ms").intValue() < 5) {
        return null;
      }
    }
    ObjectNode clone = Json.newJsonObject();
    Iterator<Map.Entry<String, JsonNode>> it = child.fields();
    while (it.hasNext()) {
      Map.Entry<String, JsonNode> e = it.next();
      if (e.getValue().isObject()) {
        ObjectNode result = cloneFilteredLightning((ObjectNode) e.getValue());
        if (result != null) {
          clone.set(e.getKey(), result);
        }
      } else {
        clone.set(e.getKey(), e.getValue());
      }
    }
    return clone;
  }

  private static ObjectNode filteredLightning(PerfTracker tracker) {
    ObjectNode all = Json.parseJsonObject(tracker.getLightningJsonAndReset());
    return cloneFilteredLightning(all);
  }
}
