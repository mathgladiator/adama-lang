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
package org.adamalang.runtime.delta;

import org.adamalang.runtime.delta.secure.TestKey;
import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.json.PrivateLazyDeltaWriter;
import org.adamalang.runtime.natives.NtPrincipal;
import org.junit.Assert;
import org.junit.Test;

public class DMapTests {
  @Test
  @SuppressWarnings("unchecked")
  public void flow() {
    final var map = new DMap<Integer, DBoolean>();
    {
      final var stream = new JsonStreamWriter();
      final var writer = PrivateLazyDeltaWriter.bind(NtPrincipal.NO_ONE, stream, null, TestKey.ENCODER);
      final var delta = writer.planObject();
      final DMap<Integer, DBoolean>.Walk walk = map.begin();
      walk.next(42, DBoolean::new).show(true, delta.planField("" + 42));
      walk.next(1, DBoolean::new).show(false, delta.planField("" + 1));
      walk.end(delta);
      delta.end();
      Assert.assertEquals("{\"42\":true,\"1\":false}", stream.toString());
      Assert.assertEquals(200, map.__memory());
    }
    {
      final var stream = new JsonStreamWriter();
      final var writer = PrivateLazyDeltaWriter.bind(NtPrincipal.NO_ONE, stream, null, TestKey.ENCODER);
      final var delta = writer.planObject();
      delta.manifest();
      final DMap<Integer, DBoolean>.Walk walk = map.begin();
      walk.next(42, DBoolean::new).show(true, delta.planField("" + 42));
      walk.next(1, DBoolean::new).show(false, delta.planField("" + 1));
      walk.end(delta);
      delta.end();
      Assert.assertEquals("{}", stream.toString());
      Assert.assertEquals(200, map.__memory());
    }
    {
      final var stream = new JsonStreamWriter();
      final var writer = PrivateLazyDeltaWriter.bind(NtPrincipal.NO_ONE, stream, null, TestKey.ENCODER);
      final var delta = writer.planObject();
      delta.manifest();
      final DMap<Integer, DBoolean>.Walk walk = map.begin();
      walk.next(42, DBoolean::new).show(false, delta.planField("" + 42));
      walk.end(delta);
      delta.end();
      Assert.assertEquals("{\"42\":false,\"1\":null}", stream.toString());
      Assert.assertEquals(120, map.__memory());
    }
    {
      final var stream = new JsonStreamWriter();
      final var writer = PrivateLazyDeltaWriter.bind(NtPrincipal.NO_ONE, stream, null, TestKey.ENCODER);
      map.hide(writer);
      map.hide(writer);
      Assert.assertEquals("null", stream.toString());
      Assert.assertEquals(40, map.__memory());
    }
    map.clear();
  }
}
