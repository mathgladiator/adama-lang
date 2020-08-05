/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.support.testgen;

import org.adamalang.runtime.contracts.DocumentMonitor;
import org.adamalang.runtime.contracts.TimeSource;
import org.adamalang.runtime.contracts.TransactionLogger;
import org.adamalang.runtime.exceptions.ErrorCodeException;
import org.adamalang.runtime.exceptions.GoodwillExhaustedException;
import org.adamalang.runtime.logger.NoOpLogger;
import org.adamalang.runtime.logger.ObjectNodeLogger;
import org.adamalang.runtime.logger.Transaction;
import org.adamalang.runtime.logger.Transactor;
import org.adamalang.runtime.natives.NtClient;
import org.adamalang.translator.jvm.LivingDocumentFactory;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class PhaseRun {
    public static void go(LivingDocumentFactory factory, DocumentMonitor monitor, AtomicBoolean passedTests, StringBuilder outputFile) throws Exception {
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
                outputFile.append(t.request.toString() + "-->" + t.delta.toString() + " need:" + t.transactionResult.needsInvalidation + " in:" + t.transactionResult.whenToInvalidMilliseconds + "\n");
                objectNodeLog.ingest(t);
                testTime.addAndGet(Math.max(t.transactionResult.whenToInvalidMilliseconds / 2, 25));
            }
        };
        testTransactionLogger.close(); // stupid coverage
        final var transactor = new Transactor(factory, monitor, time, testTransactionLogger);
        transactor.construct(NtClient.NO_ONE, "{}", "0");
        try {
            transactor.createView(NtClient.NO_ONE, str -> {
                outputFile.append("+ NO_ONE DELTA:").append(str).append("\n");
            });
            try {
                transactor.connect(NtClient.NO_ONE);
            } catch (final ErrorCodeException e) {
                outputFile.append("NO_ONE was DENIED:" + e.code + "\n");
            }
            final var rando = new NtClient("rando", "random-place");
            transactor.createView(rando, str -> {
                outputFile.append("+ RANDO DELTA:").append(str).append("\n");
            });
            try {
                transactor.connect(rando);
            } catch (final ErrorCodeException e) {
                outputFile.append("RANDO was DENIED:" + e.code + "\n");
            }
            var transactionResult = transactor.invalidate();
            while (transactionResult.needsInvalidation) {
                transactionResult = transactor.invalidate();
            }
        } catch (final GoodwillExhaustedException gee) {
            passedTests.set(false);
            outputFile.append("GOODWILL EXHAUSTED:" + gee.getMessage()).append("!!!\n!!!\n");
        }
        transactor.bill();
        outputFile.append("--JAVA RESULTS-------------------------------------").append("\n");
        outputFile.append(objectNodeLog.node.toString()).append("\n");
        outputFile.append("--DUMP RESULTS-------------------------------------").append("\n");
        final var json = transactor.json();
        outputFile.append(json).append("\n");
        final var __t = new Transactor(factory, monitor, time, new NoOpLogger());
        __t.create();
        __t.insert(json);
        outputFile.append(__t.json()).append("\n");
        if (!__t.json().equals(json)) { throw new RuntimeException("Json were not equal"); }
    }
}
