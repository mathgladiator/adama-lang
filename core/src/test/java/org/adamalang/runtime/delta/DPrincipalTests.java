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

import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.json.PrivateLazyDeltaWriter;
import org.adamalang.runtime.natives.NtPrincipal;
import org.junit.Assert;
import org.junit.Test;

public class DPrincipalTests {
  @Test
  public void flow() {
    final var db = new DPrincipal();
    final var stream = new JsonStreamWriter();
    final var writer = PrivateLazyDeltaWriter.bind(NtPrincipal.NO_ONE, stream, null, 0);
    final var A = new NtPrincipal("a", "local");
    db.show(NtPrincipal.NO_ONE, writer);
    db.show(NtPrincipal.NO_ONE, writer);
    db.show(A, writer);
    db.show(A, writer);
    db.hide(writer);
    db.hide(writer);
    db.show(A, writer);
    db.show(A, writer);
    db.show(NtPrincipal.NO_ONE, writer);
    db.show(NtPrincipal.NO_ONE, writer);
    Assert.assertEquals("{\"@t\":1,\"agent\":\"?\",\"authority\":\"?\"}{\"@t\":1,\"agent\":\"a\",\"authority\":\"local\"}null{\"@t\":1,\"agent\":\"a\",\"authority\":\"local\"}{\"@t\":1,\"agent\":\"?\",\"authority\":\"?\"}", stream.toString());
    Assert.assertEquals(36, db.__memory());
    db.clear();
  }
}
