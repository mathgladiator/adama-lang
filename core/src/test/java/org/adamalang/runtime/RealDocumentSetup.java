/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.runtime.contracts.TransactionLogger;
import org.adamalang.runtime.logger.TransactionResult;
import org.adamalang.runtime.logger.ObjectNodeLogger;
import org.adamalang.runtime.logger.Transaction;
import org.adamalang.runtime.logger.Transactor;
import org.adamalang.runtime.mocks.MockTime;
import org.adamalang.runtime.ops.StdOutDocumentMonitor;
import org.adamalang.translator.jvm.LivingDocumentFactory;
import org.junit.Assert;

public class RealDocumentSetup {
    public final MockTime time;
    public final ObjectNodeLogger logger;
    public final Transactor transactor;
    public final LivingDocumentFactory factory;
    public RealDocumentSetup(String code) throws Exception {
        this(code, null);
    }
    public RealDocumentSetup(String code, ObjectNode node) throws Exception {
        this(code, node, true);
    }
    public RealDocumentSetup(String code, ObjectNode node, boolean stdout) throws Exception {
        this.time = new MockTime();
        this.logger = node == null ? ObjectNodeLogger.fresh() : ObjectNodeLogger.recover(node);
        factory = LivingDocumentTests.compile(code);
        TransactionLogger transactionLogger = new TransactionLogger() {
            @Override
            public void ingest(Transaction t) throws Exception {
                if (stdout) {
                    System.out.println(" REQ :" + t.request.toString());
                    System.out.println("DELTA:" + t.delta.toString());
                }
                logger.ingest(t);
            }

            @Override
            public void close() throws Exception {

            }
        };
        transactor = new Transactor(factory, stdout ? new StdOutDocumentMonitor() : null, time, transactionLogger);
    }

    public void drive(TransactionResult initial) throws Exception {
        TransactionResult transactionResult = initial;
        while (transactionResult.needsInvalidation) {
            time.time += transactionResult.whenToInvalidMilliseconds;
            transactionResult = transactor.invalidate();
        }
    }

    public void assertInitial() {
        Assert.assertEquals("{\"__constructed\":true,\"__entropy\":\"-5106534569952410475\",\"__seedUsed\":\"123\",\"__seq\":1}", logger.node.toString());
    }

}
