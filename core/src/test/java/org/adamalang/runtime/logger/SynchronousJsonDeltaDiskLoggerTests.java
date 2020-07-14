/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.logger;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;

public class SynchronousJsonDeltaDiskLoggerTests {
    @Test
    public void flow_file_create_from_nothing() throws Exception {
        File dump = File.createTempFile("__test_file", ".jsonlog");
        File dumpDoesNotExist = new File(dump.getName() + ".na");
        try {
            { // create and populate the file
                ObjectNodeLogger target = ObjectNodeLogger.fresh();
                SynchronousJsonDeltaDiskLogger logger = SynchronousJsonDeltaDiskLogger.openFillAndAppend(dumpDoesNotExist, target);
                logger.getFile();
                Assert.assertEquals(-1, logger.getRecordsReadAtStart());
                logger.ingest(new Transaction(-1, JsonHelper.encodeObject("x", "1"), JsonHelper.encodeObject("delta", "data"), new TransactionResult(true, 0, 1)));
                logger.ingest(new Transaction(-1, JsonHelper.encodeObject("x", "2"), JsonHelper.encodeObject("delta", "data_overwrite"), new TransactionResult(true, 0, 2)));
                logger.ingest(new Transaction(-1, JsonHelper.encodeObject("x", "3"), JsonHelper.encodeObject("delta1", "d1"), new TransactionResult(true, 0, 3)));
                logger.ingest(new Transaction(-1, JsonHelper.encodeObject("x", "4"), JsonHelper.encodeObject("delta2", "d2"), new TransactionResult(true, 0, 4)));
                logger.close();
                Assert.assertEquals("{\"delta\":\"data_overwrite\",\"delta1\":\"d1\",\"delta2\":\"d2\"}", target.node.toString());
            }
            { // read the file off of disk
                ObjectNodeLogger target = ObjectNodeLogger.fresh();
                SynchronousJsonDeltaDiskLogger logger = SynchronousJsonDeltaDiskLogger.openFillAndAppend(dumpDoesNotExist, target);
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

    @Test
    public void flow_file_create_from_empty_file() throws Exception {
        File dump = File.createTempFile("__test_file", ".jsonlog");
        try {
            { // create and populate the file
                ObjectNodeLogger target = ObjectNodeLogger.fresh();
                SynchronousJsonDeltaDiskLogger logger = SynchronousJsonDeltaDiskLogger.openFillAndAppend(dump, target);
                logger.getFile();
                Assert.assertEquals(0, logger.getRecordsReadAtStart());
                logger.ingest(new Transaction(-1, JsonHelper.encodeObject("x", "1"), JsonHelper.encodeObject("delta", "data"), new TransactionResult(true, 0, 1)));
                logger.ingest(new Transaction(-1, JsonHelper.encodeObject("x", "2"), JsonHelper.encodeObject("delta", "data_overwrite"), new TransactionResult(true, 0, 2)));
                logger.ingest(new Transaction(-1, JsonHelper.encodeObject("x", "3"), JsonHelper.encodeObject("delta1", "d1"), new TransactionResult(true, 0, 3)));
                logger.ingest(new Transaction(-1, JsonHelper.encodeObject("x", "4"), JsonHelper.encodeObject("delta2", "d2"), new TransactionResult(true, 0, 4)));
                logger.close();
                Assert.assertEquals("{\"delta\":\"data_overwrite\",\"delta1\":\"d1\",\"delta2\":\"d2\"}", target.node.toString());
            }
            { // read the file off of disk
                ObjectNodeLogger target = ObjectNodeLogger.fresh();
                SynchronousJsonDeltaDiskLogger logger = SynchronousJsonDeltaDiskLogger.openFillAndAppend(dump, target);
                logger.getFile();
                Assert.assertEquals(4, logger.getRecordsReadAtStart());
                logger.close();
                Assert.assertEquals("{\"delta\":\"data_overwrite\",\"delta1\":\"d1\",\"delta2\":\"d2\"}", target.node.toString());
            }
        } finally {
            dump.delete();
        }
    }
}
