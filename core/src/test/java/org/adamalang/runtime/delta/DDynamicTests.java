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
import org.adamalang.runtime.natives.NtDynamic;
import org.adamalang.runtime.natives.NtPrincipal;
import org.junit.Assert;
import org.junit.Test;

public class DDynamicTests {
  @Test
  public void flow() {
    final var db = new DDynamic();
    final var stream = new JsonStreamWriter();
    final var writer = PrivateLazyDeltaWriter.bind(NtPrincipal.NO_ONE, stream, null, 0);
    final var A = new NtDynamic("false");
    final var B = new NtDynamic("true");
    db.show(A, writer);
    db.show(A, writer);
    db.hide(writer);
    db.hide(writer);
    db.show(A, writer);
    db.show(A, writer);
    db.show(A, writer);
    db.show(A, writer);
    db.show(B, writer);
    db.show(B, writer);
    db.show(A, writer);
    db.show(A, writer);
    db.show(A, writer);
    Assert.assertEquals("falsenullfalsetruefalse", stream.toString());
    Assert.assertEquals(52, db.__memory());
    db.clear();
  }
}
