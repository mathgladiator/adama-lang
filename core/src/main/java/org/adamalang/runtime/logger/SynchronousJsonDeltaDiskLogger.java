/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import org.adamalang.runtime.contracts.TransactionLogger;
import org.adamalang.runtime.exceptions.ErrorCodeException;

/** a logger which spools out individual transactions for a single document */
public class SynchronousJsonDeltaDiskLogger implements TransactionLogger {
  public static SynchronousJsonDeltaDiskLogger openFillAndAppend(final File file, final TransactionLogger target) throws ErrorCodeException {
    final var recordsReadAtStart = pump(file, target);
    return new SynchronousJsonDeltaDiskLogger(recordsReadAtStart, file, target);
  }

  public static int pump(final File file, final TransactionLogger target) throws ErrorCodeException {
    try {
      if (file.exists()) {
        var records = 0;
        final var buffered = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
        try {
          NewlineJsonTransactionDiskRecord record;
          while ((record = new NewlineJsonTransactionDiskRecord(buffered)).valid()) {
            records++;
            target.ingest(record.toTransaction());
          }
        } finally {
          buffered.close();
        }
        return records;
      }
      return -1;
    } catch (final Exception ex) {
      ex.printStackTrace();
      throw new ErrorCodeException(ErrorCodeException.SYNC_DISK_LOGGER_CANT_PUMP, ex);
    }
  }

  private final File file;
  private final int recordsReadAtStart;
  private final TransactionLogger target;
  private final PrintWriter writer;

  public SynchronousJsonDeltaDiskLogger(final int recordsReadAtStart, final File file, final TransactionLogger target) throws ErrorCodeException {
    try {
      this.recordsReadAtStart = recordsReadAtStart;
      this.file = file;
      this.target = target;
      writer = new PrintWriter(new FileOutputStream(file, true), false, StandardCharsets.UTF_8);
    } catch (final IOException ioe) {
      ioe.printStackTrace();
      throw new ErrorCodeException(ErrorCodeException.SYNC_DISK_LOGGER_CANT_OPEN_APPEND, ioe);
    }
  }

  @Override
  public void close() throws Exception {
    try {
      writer.flush();
    } finally {
      writer.close();
    }
  }

  public File getFile() {
    return file;
  }

  public int getRecordsReadAtStart() {
    return recordsReadAtStart;
  }

  @Override
  public void ingest(final Transaction transaction) throws ErrorCodeException {
    NewlineJsonTransactionDiskRecord.writeTo(transaction, writer);
    writer.flush();
    target.ingest(transaction);
  }
}
