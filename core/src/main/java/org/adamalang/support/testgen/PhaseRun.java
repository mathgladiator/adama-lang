/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.support.testgen;

import org.adamalang.common.Callback;
import org.adamalang.common.SimpleExecutor;
import org.adamalang.common.TimeSource;
import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.adamalang.runtime.contracts.DocumentMonitor;
import org.adamalang.runtime.data.Key;
import org.adamalang.runtime.contracts.Perspective;
import org.adamalang.runtime.exceptions.GoodwillExhaustedException;
import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.natives.NtClient;
import org.adamalang.runtime.sys.CoreMetrics;
import org.adamalang.runtime.sys.DocumentThreadBase;
import org.adamalang.runtime.sys.DurableLivingDocument;
import org.adamalang.translator.jvm.LivingDocumentFactory;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

public class PhaseRun {
  public static void go(final LivingDocumentFactory factory, final DocumentMonitor monitor, final AtomicBoolean passedTests, final StringBuilder outputFile) throws Exception {
    final var testTime = new AtomicLong(0);
    final var time = (TimeSource) () -> testTime.get();
    outputFile.append("--JAVA RUNNING-------------------------------------").append("\n");
    DumbDataService dds = new DumbDataService((patch) -> {
      outputFile.append(patch.request + "-->" + patch.redo + " need:" + patch.requiresFutureInvalidation + " in:" + patch.whenToInvalidateMilliseconds + "\n");
      testTime.addAndGet(Math.max(patch.whenToInvalidateMilliseconds / 2, 25));
    });
    DumbDataService.DumbDurableLivingDocumentAcquire acquire = new DumbDataService.DumbDurableLivingDocumentAcquire();
    try {
      Key key = new Key("0", "0");
      DocumentThreadBase base = new DocumentThreadBase(dds, new CoreMetrics(new NoOpMetricsFactory()), SimpleExecutor.NOW, time);
      DurableLivingDocument.fresh(key, factory, NtClient.NO_ONE, "{}", "0", monitor, base, acquire);
      DurableLivingDocument doc = acquire.get();
      doc.invalidate(Callback.DONT_CARE_INTEGER);
      outputFile.append("CPU:").append(doc.getCodeCost()).append("\n");
      outputFile.append("MEMORY:").append(doc.getMemoryBytes()).append("\n");
      doc.createPrivateView(NtClient.NO_ONE, wrap(str -> {
        outputFile.append("+ NO_ONE DELTA:").append(str).append("\n");
      }), new JsonStreamReader("{}"), DumbDataService.makePrinterPrivateView("NO_ONE", outputFile));
      doc.connect(NtClient.NO_ONE, DumbDataService.makePrinterInt("NO_ONE", outputFile));
      final var rando = new NtClient("rando", "random-place");
      doc.createPrivateView(rando, wrap(str -> {
        outputFile.append("+ RANDO DELTA:").append(str).append("\n");
      }), new JsonStreamReader("{}"), DumbDataService.makePrinterPrivateView("RANDO", outputFile));
      doc.connect(rando, DumbDataService.makePrinterInt("RANDO", outputFile));
      doc.invalidate(DumbDataService.makePrinterInt("RANDO", outputFile));

      outputFile.append("MEMORY:" + doc.getMemoryBytes() + "\n");
      outputFile.append("--JAVA RESULTS-------------------------------------").append("\n");
      outputFile.append(doc.json()).append("\n");
      outputFile.append("--DUMP RESULTS-------------------------------------").append("\n");
      final var json = doc.json();
      dds.setData(json);
      outputFile.append(json).append("\n");
      DumbDataService.DumbDurableLivingDocumentAcquire acquire2 = new DumbDataService.DumbDurableLivingDocumentAcquire();
      DurableLivingDocument.load(key, factory, monitor, base, acquire2);
      DurableLivingDocument doc2 = acquire2.get();
      outputFile.append(doc2.json()).append("\n");
      mustBeTrue(doc2.json().equals(json), "JSON don't match load, dump cycle");
    } catch (final RuntimeException gee) {
      passedTests.set(false);
      Throwable search = gee;
      while (search.getCause() != null) {
        search = search.getCause();
        if (search instanceof GoodwillExhaustedException) {
          outputFile.append("GOODWILL EXHAUSTED:" + gee.getMessage()).append("!!!\n!!!\n");
        }
      }
      outputFile.append("RuntimeException:" + gee.getMessage()).append("!!!\n!!!\n");
    }
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
