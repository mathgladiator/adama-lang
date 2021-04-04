/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.logger;

import org.adamalang.runtime.contracts.TransactionLogger;
import org.adamalang.runtime.exceptions.ErrorCodeException;

/** a logger which does no logging */
@Deprecated
public class NoOpLogger implements TransactionLogger {
  public static final NoOpLogger INSTANCE = new NoOpLogger();

  @Override
  public void close() throws ErrorCodeException {
  }

  @Override
  public void ingest(final Transaction transaction) throws ErrorCodeException {
  }
}
