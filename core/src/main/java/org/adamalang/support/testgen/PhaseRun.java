/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.support.testgen;

import java.io.PrintWriter;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import org.adamalang.runtime.DurableLivingDocument;
import org.adamalang.runtime.contracts.*;
import org.adamalang.runtime.exceptions.ErrorCodeException;
import org.adamalang.runtime.exceptions.GoodwillExhaustedException;
import org.adamalang.runtime.logger.NoOpLogger;
import org.adamalang.runtime.logger.ObjectNodeLogger;
import org.adamalang.runtime.logger.Transaction;
import org.adamalang.runtime.logger.Transactor;
import org.adamalang.runtime.natives.NtClient;
import org.adamalang.translator.jvm.LivingDocumentFactory;

public class PhaseRun {
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

  public static void go(final LivingDocumentFactory factory, final DocumentMonitor monitor, final AtomicBoolean passedTests, final StringBuilder outputFile) throws Exception {
    final var testTime = new AtomicLong(0);
    final var time = (TimeSource) () -> testTime.get();
    outputFile.append("--JAVA RUNNING-------------------------------------").append("\n");
    final var objectNodeLog = ObjectNodeLogger.fresh();
    final TransactionLogger testTransactionLogger = new TransactionLogger() {
      @Override
      public void close() throws Exception {
      }

      @Override
      public void ingest(final Transaction t) throws ErrorCodeException {
        outputFile.append(t.request.toString() + "-->" + t.forwardDelta.toString() + " need:" + t.transactionResult.needsInvalidation + " in:" + t.transactionResult.whenToInvalidMilliseconds + "\n");
        objectNodeLog.ingest(t);
        testTime.addAndGet(Math.max(t.transactionResult.whenToInvalidMilliseconds / 2, 25));
      }
    };
    testTransactionLogger.close(); // stupid coverage
    DumbDataService dds = new DumbDataService((patch) -> {
      outputFile.append(patch.request.toString() + "-->" + patch.redo.toString() + " need:" + patch.requiresFutureInvalidation + " in:" + patch.whenToInvalidateMilliseconds + "\n");
      testTime.addAndGet(Math.max(patch.whenToInvalidateMilliseconds / 2, 25));
    });

    DumbDataService.DumbDurableLivingDocumentAcquire acquire = new DumbDataService.DumbDurableLivingDocumentAcquire();

    try {
      DurableLivingDocument.fresh(0, factory, NtClient.NO_ONE, "{}", "0", monitor, time, dds, acquire);
      DurableLivingDocument doc = acquire.get();

      doc.createPrivateView(NtClient.NO_ONE, wrap(str -> {
        outputFile.append("+ NO_ONE DELTA:").append(str).append("\n");
      }));
      try {
        doc.connect(NtClient.NO_ONE, DumbDataService.NOOPINT);
      } catch (final RuntimeException e) {
        outputFile.append("NO_ONE was DENIED\n");
      }
      final var rando = new NtClient("rando", "random-place");
      doc.createPrivateView(rando, wrap(str -> {
        outputFile.append("+ RANDO DELTA:").append(str).append("\n");
      }));
      try {
        doc.connect(rando, DumbDataService.NOOPINT);
      } catch (final RuntimeException e) {
        outputFile.append("RANDO was DENIED:\n");
      }
      doc.invalidate(DumbDataService.NOOPINT);

      doc.bill(DumbDataService.NOOPINT);
      outputFile.append("--JAVA RESULTS-------------------------------------").append("\n");
      outputFile.append(objectNodeLog.node.toString()).append("\n");
      outputFile.append("--DUMP RESULTS-------------------------------------").append("\n");
      final var json = doc.json();
      dds.setData(json);
      outputFile.append(json).append("\n");
      DumbDataService.DumbDurableLivingDocumentAcquire acquire2 = new DumbDataService.DumbDurableLivingDocumentAcquire();
      DurableLivingDocument.load(0, factory, monitor, time, dds, acquire2);
      DurableLivingDocument doc2 = acquire2.get();
      outputFile.append(doc2.json()).append("\n");
      if (!doc2.json().equals(json)) {
        throw new RuntimeException("Json were not equal");
      }
    } catch (final RuntimeException gee) {
      passedTests.set(false);
      Throwable search = gee;
      while (search.getCause() != null) {
        search = search.getCause();
        if (search instanceof GoodwillExhaustedException) {
          outputFile.append("GOODWILL EXHAUSTED:" + gee.getMessage()).append("!!!\n!!!\n");
          return;
        }
      }
      throw gee;
    }
  }
}
