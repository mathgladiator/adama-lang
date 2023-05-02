/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's Apache2); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.runtime.async;

import org.adamalang.runtime.exceptions.AbortMessageException;
import org.adamalang.runtime.exceptions.RetryProgressException;
import org.adamalang.runtime.natives.NtPrincipal;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

public class AsyncTaskTests {
  @Test
  public void abort_flow() {
    final var at = new AsyncTask(0, NtPrincipal.NO_ONE, "ch", 0, "origin", "ip", "message");
    at.setAction(
        () -> {
          throw new AbortMessageException();
        });
    try {
      at.execute();
      Assert.fail();
    } catch (final RetryProgressException rpe) {
    }
  }

  @Test
  public void ideal_flow() throws Exception {
    final var at = new AsyncTask(0, NtPrincipal.NO_ONE, "ch", 0, "origin", "ip","message");
    final var ref = new AtomicInteger(0);
    at.setAction(
        () -> {
          ref.incrementAndGet();
        });
    at.execute();
  }
}
