/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.mocks;

import org.adamalang.runtime.contracts.TimeSource;

public class MockTime implements TimeSource {
  public long time;

  public MockTime() {
    time = 0;
  }

  public MockTime(long t) {
    time = t;
  }

  @Override
  public long nowMilliseconds() {
    return time;
  }
}
