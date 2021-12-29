/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.runtime.async;

import org.adamalang.runtime.exceptions.AbortMessageException;
import org.adamalang.runtime.exceptions.RetryProgressException;
import org.adamalang.runtime.natives.NtClient;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

public class AsyncTaskTests {
  @Test
  public void abort_flow() {
    final var at = new AsyncTask(0, NtClient.NO_ONE, "ch", 0, "message");
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
    final var at = new AsyncTask(0, NtClient.NO_ONE, "ch", 0, "message");
    final var ref = new AtomicInteger(0);
    at.setAction(
        () -> {
          ref.incrementAndGet();
        });
    at.execute();
  }
}
