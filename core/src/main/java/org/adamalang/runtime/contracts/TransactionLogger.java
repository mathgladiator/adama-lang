/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.contracts;

import org.adamalang.runtime.exceptions.ErrorCodeException;
import org.adamalang.runtime.logger.Transaction;

/** the data model uses a log */
@Deprecated
public interface TransactionLogger {
  /** the log has been closed */
  public void close() throws Exception;
  /** write a single transaction to the logger */
  public void ingest(Transaction transaction) throws ErrorCodeException;
}
