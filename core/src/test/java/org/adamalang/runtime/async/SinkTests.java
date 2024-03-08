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

import org.adamalang.runtime.natives.NtPrincipal;
import org.junit.Assert;
import org.junit.Test;

public class SinkTests {
  @Test
  public void flow_in_and_out() {
    final var sink = new Sink<String>("channel");
    sink.enqueue(new AsyncTask(0, 1, NtPrincipal.NO_ONE, null, "channel", 0, "origin", "ip","message"), "Cake");
    final var sf = sink.dequeue(NtPrincipal.NO_ONE);
    Assert.assertTrue(sf.exists());
    Assert.assertEquals("Cake", sf.await());
    final var sf2 = sink.dequeue(NtPrincipal.NO_ONE);
    Assert.assertFalse(sf2.exists());
  }

  @Test
  public void dequeue_if_works_as_expected() {
    final var sink = new Sink<String>("channel");
    sink.enqueue(new AsyncTask(0, 2, NtPrincipal.NO_ONE, null, "channel", 1000, "origin", "ip","message"), "A");
    Assert.assertFalse(sink.dequeueIf(NtPrincipal.NO_ONE, 500).exists());
    Assert.assertEquals("A", sink.dequeueIf(NtPrincipal.NO_ONE, 3000).await());
  }

  @Test
  public void flow_in_clear_out() {
    final var sink = new Sink<String>("channel");
    sink.enqueue(new AsyncTask(0, 3, NtPrincipal.NO_ONE, null, "channel", 0, "origin", "ip","message"), "Cake");
    sink.clear();
    final var sf2 = sink.dequeue(NtPrincipal.NO_ONE);
    Assert.assertFalse(sf2.exists());
  }

  @Test
  public void maybe_out_no_data() {
    final var sink = new Sink<String>("channel");
    final var sf = sink.dequeueMaybe(NtPrincipal.NO_ONE);
    Assert.assertFalse(sf.exists());
  }

  @Test
  public void maybe_out_with_data() {
    final var sink = new Sink<String>("channel");
    sink.enqueue(new AsyncTask(0, 4, NtPrincipal.NO_ONE, null, "channel", 0, "origin", "ip","message"), "Cake");
    final var sf = sink.dequeueMaybe(NtPrincipal.NO_ONE);
    Assert.assertTrue(sf.exists());
    Assert.assertEquals("Cake", sf.await().get());
    final var sf2 = sink.dequeue(NtPrincipal.NO_ONE);
    Assert.assertFalse(sf2.exists());
  }
}
