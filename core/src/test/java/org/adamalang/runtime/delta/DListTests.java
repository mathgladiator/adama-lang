/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.delta;

import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.json.PrivateLazyDeltaWriter;
import org.adamalang.runtime.natives.NtClient;
import org.junit.Assert;
import org.junit.Test;

public class DListTests {
  @Test
  public void flow() {
    final var list = new DList<DBoolean>();
    {
      final var stream = new JsonStreamWriter();
      final var writer = PrivateLazyDeltaWriter.bind(NtClient.NO_ONE, stream, null);
      final var delta = writer.planObject();
      list.getPrior(0, DBoolean::new).show(true, delta.planField(0));
      list.getPrior(1, DBoolean::new).show(false, delta.planField(1));
      list.getPrior(2, DBoolean::new).show(true, delta.planField(2));
      list.rectify(3, delta);
      delta.end();
      Assert.assertEquals("{\"0\":true,\"1\":false,\"2\":true,\"@s\":3}", stream.toString());
    }
    {
      final var stream = new JsonStreamWriter();
      final var writer = PrivateLazyDeltaWriter.bind(NtClient.NO_ONE, stream, null);
      final var delta = writer.planObject();
      delta.manifest();
      list.getPrior(0, DBoolean::new).show(true, delta.planField(0));
      list.getPrior(1, DBoolean::new).show(false, delta.planField(1));
      list.getPrior(2, DBoolean::new).show(true, delta.planField(2));
      list.rectify(3, delta);
      delta.end();
      Assert.assertEquals("{}", stream.toString());
    }
    {
      final var stream = new JsonStreamWriter();
      final var writer = PrivateLazyDeltaWriter.bind(NtClient.NO_ONE, stream, null);
      final var delta = writer.planObject();
      delta.manifest();
      list.getPrior(0, DBoolean::new).show(true, delta.planField(0));
      list.getPrior(1, DBoolean::new).show(true, delta.planField(1));
      list.rectify(2, delta);
      delta.end();
      Assert.assertEquals("{\"1\":true,\"2\":null}", stream.toString());
    }
    {
      final var stream = new JsonStreamWriter();
      final var writer = PrivateLazyDeltaWriter.bind(NtClient.NO_ONE, stream, null);
      list.hide(writer);
      list.hide(writer);
      Assert.assertEquals("null", stream.toString());
    }
  }
}
