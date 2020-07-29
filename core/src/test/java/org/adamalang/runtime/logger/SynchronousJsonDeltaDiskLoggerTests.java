/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.logger;

import java.io.File;
import org.adamalang.runtime.exceptions.ErrorCodeException;
import org.junit.Assert;
import org.junit.Test;

public class SynchronousJsonDeltaDiskLoggerTests {
  @Test
  public void flow_cant_open_dir1() throws Exception {
    final var dump = File.createTempFile("__test_file", ".jsonlog");
    final var dumpIsDir = new File(dump.getName() + ".dir");
    dumpIsDir.mkdir();
    try {
      final var target = ObjectNodeLogger.fresh();
      SynchronousJsonDeltaDiskLogger.openFillAndAppend(dumpIsDir, target);
      Assert.fail();
    } catch (final ErrorCodeException ece) {
      Assert.assertEquals(5200, ece.code);
    } finally {
      dump.delete();
      dumpIsDir.delete();
    }
  }

  @Test
  public void flow_cant_open_dir2() throws Exception {
    final var dump = File.createTempFile("__test_file", ".jsonlog");
    final var dumpIsDir = new File(dump.getName() + ".dir2");
    dumpIsDir.mkdir();
    try {
      final var target = ObjectNodeLogger.fresh();
      new SynchronousJsonDeltaDiskLogger(0, dumpIsDir, target);
      Assert.fail();
    } catch (final ErrorCodeException ece) {
      Assert.assertEquals(5201, ece.code);
    } finally {
      dump.delete();
      dumpIsDir.delete();
    }
  }

  @Test
  public void flow_file_create_from_empty_file() throws Exception {
    final var dump = File.createTempFile("__test_file", ".jsonlog");
    try {
      { // create and populate the file
        final var target = ObjectNodeLogger.fresh();
        final var logger = SynchronousJsonDeltaDiskLogger.openFillAndAppend(dump, target);
        logger.getFile();
        Assert.assertEquals(0, logger.getRecordsReadAtStart());
        logger.ingest(new Transaction(-1, JsonHelper.encodeObject("x", "1").toString(), JsonHelper.encodeObject("delta", "data").toString(), new TransactionResult(true, 0, 1)));
        logger.ingest(new Transaction(-1, JsonHelper.encodeObject("x", "2").toString(), JsonHelper.encodeObject("delta", "data_overwrite").toString(), new TransactionResult(true, 0, 2)));
        logger.ingest(new Transaction(-1, JsonHelper.encodeObject("x", "3").toString(), JsonHelper.encodeObject("delta1", "d1").toString(), new TransactionResult(true, 0, 3)));
        logger.ingest(new Transaction(-1, JsonHelper.encodeObject("x", "4").toString(), JsonHelper.encodeObject("delta2", "d2").toString(), new TransactionResult(true, 0, 4)));
        logger.close();
        Assert.assertEquals("{\"delta\":\"data_overwrite\",\"delta1\":\"d1\",\"delta2\":\"d2\"}", target.node.toString());
      }
      { // read the file off of disk
        final var target = ObjectNodeLogger.fresh();
        final var logger = SynchronousJsonDeltaDiskLogger.openFillAndAppend(dump, target);
        logger.getFile();
        Assert.assertEquals(4, logger.getRecordsReadAtStart());
        logger.close();
        Assert.assertEquals("{\"delta\":\"data_overwrite\",\"delta1\":\"d1\",\"delta2\":\"d2\"}", target.node.toString());
      }
    } finally {
      dump.delete();
    }
  }

  @Test
  public void flow_file_create_from_nothing() throws Exception {
    final var dump = File.createTempFile("__test_file", ".jsonlog");
    final var dumpDoesNotExist = new File(dump.getName() + ".na");
    try {
      { // create and populate the file
        final var target = ObjectNodeLogger.fresh();
        final var logger = SynchronousJsonDeltaDiskLogger.openFillAndAppend(dumpDoesNotExist, target);
        logger.getFile();
        Assert.assertEquals(-1, logger.getRecordsReadAtStart());
        logger.ingest(new Transaction(-1, JsonHelper.encodeObject("x", "1").toString(), JsonHelper.encodeObject("delta", "data").toString(), new TransactionResult(true, 0, 1)));
        logger.ingest(new Transaction(-1, JsonHelper.encodeObject("x", "2").toString(), JsonHelper.encodeObject("delta", "data_overwrite").toString(), new TransactionResult(true, 0, 2)));
        logger.ingest(new Transaction(-1, JsonHelper.encodeObject("x", "3").toString(), JsonHelper.encodeObject("delta1", "d1").toString(), new TransactionResult(true, 0, 3)));
        logger.ingest(new Transaction(-1, JsonHelper.encodeObject("x", "4").toString(), JsonHelper.encodeObject("delta2", "d2").toString(), new TransactionResult(true, 0, 4)));
        logger.close();
        Assert.assertEquals("{\"delta\":\"data_overwrite\",\"delta1\":\"d1\",\"delta2\":\"d2\"}", target.node.toString());
      }
      { // read the file off of disk
        final var target = ObjectNodeLogger.fresh();
        final var logger = SynchronousJsonDeltaDiskLogger.openFillAndAppend(dumpDoesNotExist, target);
        logger.getFile();
        Assert.assertEquals(4, logger.getRecordsReadAtStart());
        logger.close();
        Assert.assertEquals("{\"delta\":\"data_overwrite\",\"delta1\":\"d1\",\"delta2\":\"d2\"}", target.node.toString());
      }
    } finally {
      dump.delete();
      dumpDoesNotExist.delete();
    }
  }
}
