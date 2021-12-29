/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * The 'LICENSE' file is in the root directory of the repository. Hint: it is MIT.
 * 
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.runtime.delta;

import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.json.PrivateLazyDeltaWriter;
import org.adamalang.runtime.natives.NtClient;
import org.junit.Assert;
import org.junit.Test;

public class DMapTests {
  @Test
  @SuppressWarnings("unchecked")
  public void flow() {
    final var map = new DMap<Integer, DBoolean>();
    {
      final var stream = new JsonStreamWriter();
      final var writer = PrivateLazyDeltaWriter.bind(NtClient.NO_ONE, stream, null);
      final var delta = writer.planObject();
      final DMap<Integer, DBoolean>.Walk walk = map.begin();
      walk.next(42, DBoolean::new).show(true, delta.planField("" + 42));
      walk.next(1, DBoolean::new).show(false, delta.planField("" + 1));
      walk.end(delta);
      delta.end();
      Assert.assertEquals("{\"42\":true,\"1\":false}", stream.toString());
    }
    {
      final var stream = new JsonStreamWriter();
      final var writer = PrivateLazyDeltaWriter.bind(NtClient.NO_ONE, stream, null);
      final var delta = writer.planObject();
      delta.manifest();
      final DMap<Integer, DBoolean>.Walk walk = map.begin();
      walk.next(42, DBoolean::new).show(true, delta.planField("" + 42));
      walk.next(1, DBoolean::new).show(false, delta.planField("" + 1));
      walk.end(delta);
      delta.end();
      Assert.assertEquals("{}", stream.toString());
    }
    {
      final var stream = new JsonStreamWriter();
      final var writer = PrivateLazyDeltaWriter.bind(NtClient.NO_ONE, stream, null);
      final var delta = writer.planObject();
      delta.manifest();
      final DMap<Integer, DBoolean>.Walk walk = map.begin();
      walk.next(42, DBoolean::new).show(false, delta.planField("" + 42));
      walk.end(delta);
      delta.end();
      Assert.assertEquals("{\"42\":false,\"1\":null}", stream.toString());
    }
    {
      final var stream = new JsonStreamWriter();
      final var writer = PrivateLazyDeltaWriter.bind(NtClient.NO_ONE, stream, null);
      map.hide(writer);
      map.hide(writer);
      Assert.assertEquals("null", stream.toString());
    }
  }
}
