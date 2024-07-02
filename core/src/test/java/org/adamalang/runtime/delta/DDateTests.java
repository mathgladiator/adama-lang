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
import org.adamalang.runtime.natives.NtDate;
import org.adamalang.runtime.natives.NtPrincipal;
import org.junit.Assert;
import org.junit.Test;

public class DDateTests {
  @Test
  public void flow() {
    final var db = new DDate();
    final var stream = new JsonStreamWriter();
    final var writer = PrivateLazyDeltaWriter.bind(NtPrincipal.NO_ONE, stream, null, 0);
    db.show(new NtDate(1, 2, 3), writer);
    db.show(new NtDate(3, 4, 5), writer);
    db.hide(writer);
    db.hide(writer);
    db.show(new NtDate(1, 2, 3), writer);
    db.show(new NtDate(3, 4, 5), writer);
    Assert.assertEquals("\"1-02-03\"\"3-04-05\"null\"1-02-03\"\"3-04-05\"", stream.toString());
    Assert.assertEquals(56, db.__memory());
    db.clear();
  }
}
