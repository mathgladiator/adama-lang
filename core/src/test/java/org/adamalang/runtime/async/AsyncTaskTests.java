/*
* Adama Platform and Language
* Copyright (C) 2021 - 2023 by Adama Platform Initiative, LLC
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as published
* by the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Affero General Public License for more details.
* 
* You should have received a copy of the GNU Affero General Public License
* along with this program.  If not, see <https://www.gnu.org/licenses/>.
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
    final var at = new AsyncTask(0, 1, NtPrincipal.NO_ONE, 123, "ch", 0, "origin", "ip", "message");
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
    final var at = new AsyncTask(0, 1, NtPrincipal.NO_ONE, null, "ch", 0, "origin", "ip","message");
    final var ref = new AtomicInteger(0);
    at.setAction(
        () -> {
          ref.incrementAndGet();
        });
    at.execute();
  }
}
