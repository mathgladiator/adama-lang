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
package org.adamalang.support.testgen;

import org.adamalang.common.Callback;
import org.adamalang.common.SimpleExecutor;
import org.adamalang.common.TimeSource;
import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.adamalang.runtime.contracts.DocumentMonitor;
import org.adamalang.runtime.data.Key;
import org.adamalang.runtime.contracts.Perspective;
import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.runtime.remote.MetricsReporter;
import org.adamalang.runtime.remote.SampleService;
import org.adamalang.runtime.remote.Service;
import org.adamalang.runtime.remote.ServiceRegistry;
import org.adamalang.runtime.sys.*;
import org.adamalang.translator.jvm.LivingDocumentFactory;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class PhaseRun {
  public static void go(final LivingDocumentFactory factory, final DocumentMonitor monitor, final AtomicBoolean passedTests, final StringBuilder outputFile) throws Exception {
    ServiceRegistry.add("sample", SampleService.class, (s, stringObjectHashMap, keys) -> new SampleService());
    final var testTime = new AtomicLong(0);
    final var time = (TimeSource) () -> testTime.get();
    outputFile.append("--JAVA RUNNING-------------------------------------").append("\n");
    AtomicBoolean sawALoad = new AtomicBoolean(false);
    DumbDataService dds = new DumbDataService((patch) -> {
      if (patch.request.contains("\"command\":\"load\"")) {
        sawALoad.set(true);
      }
      outputFile.append(patch.request + "-->" + patch.redo + " need:" + patch.requiresFutureInvalidation + " in:" + patch.whenToInvalidateMilliseconds + "\n");
      testTime.addAndGet(Math.max(patch.whenToInvalidateMilliseconds / 2, 25));
    });
    DumbDataService.DumbDurableLivingDocumentAcquire acquire = new DumbDataService.DumbDurableLivingDocumentAcquire();
    Key key = new Key("0", "0");
    DocumentThreadBase base = new DocumentThreadBase(new ServiceShield(), new MetricsReporter() {
      @Override
      public void emitMetrics(Key key, String metricsPayload) {
      }
    }, dds, new CoreMetrics(new NoOpMetricsFactory()), SimpleExecutor.NOW, time);
    DurableLivingDocument.fresh(key, factory, new CoreRequestContext(NtPrincipal.NO_ONE, "origin", "ip", key.key), "{}", "0", monitor, base, acquire);
    DurableLivingDocument doc = acquire.get();
    doc.invalidate(Callback.DONT_CARE_INTEGER);
    outputFile.append("CPU:").append(doc.getCodeCost()).append("\n");
    outputFile.append("MEMORY:").append(doc.getMemoryBytes()).append("\n");
    doc.createPrivateView(NtPrincipal.NO_ONE, wrap(str -> {
      outputFile.append("+ NO_ONE DELTA:").append(str).append("\n");
    }), new JsonStreamReader("{}"), DumbDataService.makePrinterPrivateView("NO_ONE", outputFile));
    doc.connect(new CoreRequestContext(NtPrincipal.NO_ONE, "phase", "ip", "key"), DumbDataService.makePrinterInt("NO_ONE", outputFile));
    final var rando = new NtPrincipal("rando", "random-place");
    doc.createPrivateView(rando, wrap(str -> {
      outputFile.append("+ RANDO DELTA:").append(str).append("\n");
    }), new JsonStreamReader("{}"), DumbDataService.makePrinterPrivateView("RANDO", outputFile));
    doc.connect(new CoreRequestContext(rando, "phase", "ip", "key"), DumbDataService.makePrinterInt("RANDO", outputFile));
    doc.invalidate(DumbDataService.makePrinterInt("RANDO", outputFile));
    outputFile.append("MEMORY:" + doc.getMemoryBytes() + "\n");
    outputFile.append("--JAVA RESULTS-------------------------------------").append("\n");
    outputFile.append(doc.json()).append("\n");
    outputFile.append("--DUMP RESULTS-------------------------------------").append("\n");
    outputFile.append(doc.document().__metrics()).append("\n");
    outputFile.append("--METRIC RESULTS-----------------------------------").append("\n");
    final var json = doc.json();
    dds.setData(json);
    outputFile.append(json).append("\n");
    DumbDataService.DumbDurableLivingDocumentAcquire acquire2 = new DumbDataService.DumbDurableLivingDocumentAcquire();
    DurableLivingDocument.load(key, factory, monitor, base, acquire2);

    DurableLivingDocument doc2 = acquire2.get();
    outputFile.append(doc2.json()).append("\n");
    if (sawALoad.get()) {
      outputFile.append("SKIPPING JSON COMPARE AS A LOAD WAS DETECTED\n");
      return;
    }
    mustBeTrue(doc2.json().equals(json), "JSON don't match load, dump cycle");
  }

  public static Perspective wrap(Consumer<String> consumer) {
    return new Perspective() {
      @Override
      public void data(String data) {
        consumer.accept(data);
      }

      @Override
      public void disconnect() {
      }
    };
  }

  public static void mustBeTrue(boolean v, String ex) {
    if (!v) {
      throw new RuntimeException(ex);
    }
  }
}
