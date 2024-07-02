/*
* Adama Platform and Language
* Copyright (C) 2021 - 2024 by Adama Platform Engineering, LLC
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

import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.json.PrivateLazyDeltaWriter;
import org.adamalang.runtime.natives.NtPrincipal;
import org.junit.Assert;
import org.junit.Test;

public class DListTests {
  @Test
  public void flow() {
    final var list = new DList<DBoolean>();
    {
      final var stream = new JsonStreamWriter();
      final var writer = PrivateLazyDeltaWriter.bind(NtPrincipal.NO_ONE, stream, null, 0);
      final var delta = writer.planObject();
      list.getPrior(0, DBoolean::new).show(true, delta.planField(0));
      list.getPrior(1, DBoolean::new).show(false, delta.planField(1));
      list.getPrior(2, DBoolean::new).show(true, delta.planField(2));
      list.rectify(3, delta);
      delta.end();
      Assert.assertEquals("{\"0\":true,\"1\":false,\"2\":true,\"@s\":3}", stream.toString());
      Assert.assertEquals(248, list.__memory());
    }
    {
      final var stream = new JsonStreamWriter();
      final var writer = PrivateLazyDeltaWriter.bind(NtPrincipal.NO_ONE, stream, null, 0);
      final var delta = writer.planObject();
      delta.manifest();
      list.getPrior(0, DBoolean::new).show(true, delta.planField(0));
      list.getPrior(1, DBoolean::new).show(false, delta.planField(1));
      list.getPrior(2, DBoolean::new).show(true, delta.planField(2));
      list.rectify(3, delta);
      delta.end();
      Assert.assertEquals("{}", stream.toString());
      Assert.assertEquals(248, list.__memory());
    }
    {
      final var stream = new JsonStreamWriter();
      final var writer = PrivateLazyDeltaWriter.bind(NtPrincipal.NO_ONE, stream, null, 0);
      final var delta = writer.planObject();
      delta.manifest();
      list.getPrior(0, DBoolean::new).show(true, delta.planField(0));
      list.getPrior(1, DBoolean::new).show(true, delta.planField(1));
      list.rectify(2, delta);
      delta.end();
      Assert.assertEquals("{\"1\":true,\"2\":null,\"@s\":2}", stream.toString());
      Assert.assertEquals(208, list.__memory());
    }
    {
      final var stream = new JsonStreamWriter();
      final var writer = PrivateLazyDeltaWriter.bind(NtPrincipal.NO_ONE, stream, null, 0);
      list.hide(writer);
      list.hide(writer);
      Assert.assertEquals("null", stream.toString());
      Assert.assertEquals(128, list.__memory());
    }
    list.clear();
  }
}
