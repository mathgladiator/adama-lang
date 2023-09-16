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
import org.adamalang.runtime.natives.NtComplex;
import org.adamalang.runtime.natives.NtPrincipal;
import org.junit.Assert;
import org.junit.Test;

public class DComplexTests {
  @Test
  public void flow() {
    final var db = new DComplex();
    final var stream = new JsonStreamWriter();
    final var writer = PrivateLazyDeltaWriter.bind(NtPrincipal.NO_ONE, stream, null, TestKey.ENCODER);
    db.show(new NtComplex(1, 2), writer);
    db.show(new NtComplex(3, 4), writer);
    db.show(new NtComplex(3, 4), writer);
    db.show(new NtComplex(3, 4), writer);
    db.hide(writer);
    db.hide(writer);
    db.show(new NtComplex(1, 2), writer);
    db.show(new NtComplex(1, 2), writer);
    Assert.assertEquals("{\"r\":1.0,\"i\":2.0}{\"r\":3.0,\"i\":4.0}null{\"r\":1.0,\"i\":2.0}", stream.toString());
    Assert.assertEquals(48, db.__memory());
    db.clear();
  }
}
