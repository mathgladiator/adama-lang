/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.logger;

import org.junit.Test;

public class NoOpLoggerTests {
  @Test
  public void coverage() throws Exception {
    NoOpLogger.INSTANCE.ingest(null);
    NoOpLogger.INSTANCE.close();
  }
}
