/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * which is in the root directory of the repository. This file is part of the 'Adama'
 * project which is a programming language and document store for board games.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2021 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.runtime.async;

import org.adamalang.runtime.natives.NtClient;
import org.adamalang.runtime.stdlib.Utility;
import org.junit.Assert;
import org.junit.Test;

public class SinkTests {
  @Test
  public void flow_in_and_out() {
    final var sink = new Sink<String>("channel");
    sink.enqueue(new AsyncTask(0, NtClient.NO_ONE, "channel", 0, "message"), "Cake");
    final var sf = sink.dequeue(NtClient.NO_ONE);
    Assert.assertTrue(sf.exists());
    Assert.assertEquals("Cake", sf.await());
    final var sf2 = sink.dequeue(NtClient.NO_ONE);
    Assert.assertFalse(sf2.exists());
  }

  @Test
  public void flow_in_clear_out() {
    final var sink = new Sink<String>("channel");
    sink.enqueue(new AsyncTask(0, NtClient.NO_ONE, "channel", 0, "message"), "Cake");
    sink.clear();
    final var sf2 = sink.dequeue(NtClient.NO_ONE);
    Assert.assertFalse(sf2.exists());
  }

  @Test
  public void maybe_out_no_data() {
    final var sink = new Sink<String>("channel");
    final var sf = sink.dequeueMaybe(NtClient.NO_ONE);
    Assert.assertFalse(sf.exists());
  }

  @Test
  public void maybe_out_with_data() {
    final var sink = new Sink<String>("channel");
    sink.enqueue(new AsyncTask(0, NtClient.NO_ONE, "channel", 0, "message"), "Cake");
    final var sf = sink.dequeueMaybe(NtClient.NO_ONE);
    Assert.assertTrue(sf.exists());
    Assert.assertEquals("Cake", sf.await().get());
    final var sf2 = sink.dequeue(NtClient.NO_ONE);
    Assert.assertFalse(sf2.exists());
  }
}
