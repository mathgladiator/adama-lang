/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.runtime.async;

import org.adamalang.runtime.natives.NtPrincipal;
import org.junit.Assert;
import org.junit.Test;

public class SinkTests {
  @Test
  public void flow_in_and_out() {
    final var sink = new Sink<String>("channel");
    sink.enqueue(new AsyncTask(0, NtPrincipal.NO_ONE, "channel", 0, "origin", "ip","message"), "Cake");
    final var sf = sink.dequeue(NtPrincipal.NO_ONE);
    Assert.assertTrue(sf.exists());
    Assert.assertEquals("Cake", sf.await());
    final var sf2 = sink.dequeue(NtPrincipal.NO_ONE);
    Assert.assertFalse(sf2.exists());
  }

  @Test
  public void dequeue_if_works_as_expected() {
    final var sink = new Sink<String>("channel");
    sink.enqueue(new AsyncTask(0, NtPrincipal.NO_ONE, "channel", 1000, "origin", "ip","message"), "A");
    Assert.assertFalse(sink.dequeueIf(NtPrincipal.NO_ONE, 500).exists());
    Assert.assertEquals("A", sink.dequeueIf(NtPrincipal.NO_ONE, 3000).await());
  }

  @Test
  public void flow_in_clear_out() {
    final var sink = new Sink<String>("channel");
    sink.enqueue(new AsyncTask(0, NtPrincipal.NO_ONE, "channel", 0, "origin", "ip","message"), "Cake");
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
    sink.enqueue(new AsyncTask(0, NtPrincipal.NO_ONE, "channel", 0, "origin", "ip","message"), "Cake");
    final var sf = sink.dequeueMaybe(NtPrincipal.NO_ONE);
    Assert.assertTrue(sf.exists());
    Assert.assertEquals("Cake", sf.await().get());
    final var sf2 = sink.dequeue(NtPrincipal.NO_ONE);
    Assert.assertFalse(sf2.exists());
  }
}
