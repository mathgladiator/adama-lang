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

public class DInt64Tests {
  @Test
  public void flow() {
    final var db = new DInt64();
    final var stream = new JsonStreamWriter();
    final var writer = PrivateLazyDeltaWriter.bind(NtPrincipal.NO_ONE, stream, null, 0);
    db.show(1L, writer);
    db.show(1L, writer);
    db.show(22L, writer);
    db.show(22L, writer);
    db.show(3L, writer);
    db.show(3L, writer);
    db.hide(writer);
    db.hide(writer);
    db.show(4L, writer);
    db.show(4L, writer);
    db.show(5L, writer);
    db.show(5L, writer);
    Assert.assertEquals("\"1\"\"22\"\"3\"null\"4\"\"5\"", stream.toString());
    Assert.assertEquals(40, db.__memory());
    db.clear();
  }
}
