/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime;

import org.adamalang.runtime.contracts.TransactionLogger;
import org.adamalang.runtime.exceptions.ErrorCodeException;
import org.adamalang.runtime.logger.*;
import org.adamalang.runtime.mocks.MockTime;
import org.adamalang.runtime.ops.StdOutDocumentMonitor;
import org.adamalang.translator.jvm.LivingDocumentFactory;
import org.junit.Assert;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class RealDocumentSetup {
  public final LivingDocumentFactory factory;
  public final ObjectNodeLogger logger;
  public final MockTime time;
  public final Transactor transactor;
  public final Transactor mirror;

  public RealDocumentSetup(final String code) throws Exception {
    this(code, null);
  }

  public RealDocumentSetup(final String code, final ObjectNode node) throws Exception {
    this(code, node, true);
  }

  public RealDocumentSetup(final String code, final ObjectNode node, final boolean stdout) throws Exception {
    time = new MockTime();
    logger = node == null ? ObjectNodeLogger.fresh() : ObjectNodeLogger.recover(node);
    factory = LivingDocumentTests.compile(code);
    mirror = new Transactor(factory, null, null, NoOpLogger.INSTANCE);
    mirror.create();
    if (node != null) {
      mirror.insert(node.toString());
    }
    final TransactionLogger transactionLogger = new TransactionLogger() {
      @Override
      public void close() throws Exception {
      }

      @Override
      public void ingest(final Transaction t) throws ErrorCodeException {
        mirror.insert(t.delta);
        if (stdout) {
          System.out.println(" REQ :" + t.request.toString());
          System.out.println("DELTA:" + t.delta.toString());
        }
        logger.ingest(t);
      }
    };
    transactor = new Transactor(factory, stdout ? new StdOutDocumentMonitor() : null, time, transactionLogger);
  }

  public void assertCompare() {
    Assert.assertEquals(mirror.json(), transactor.json());
  }

  public void assertInitial() {
    Assert.assertEquals("{\"__constructed\":true,\"__entropy\":\"-5106534569952410475\",\"__seedUsed\":\"123\",\"__seq\":1}", logger.node.toString());
  }

  public void drive(final TransactionResult initial) throws Exception {
    var transactionResult = initial;
    while (transactionResult.needsInvalidation) {
      time.time += transactionResult.whenToInvalidMilliseconds;
      transactionResult = transactor.drive();
    }
  }
}
