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
import org.adamalang.runtime.natives.NtDateTime;
import org.adamalang.runtime.natives.NtPrincipal;
import org.junit.Assert;
import org.junit.Test;

import java.time.ZonedDateTime;

public class DDateTimeTests {
  @Test
  public void flow() {
    final var db = new DDateTime();
    final var stream = new JsonStreamWriter();
    final var writer = PrivateLazyDeltaWriter.bind(NtPrincipal.NO_ONE, stream, null, TestKey.ENCODER, 0);
    db.show(new NtDateTime(ZonedDateTime.parse("2023-04-24T17:57:19.802528800-05:00[America/Chicago]")), writer);
    db.show(new NtDateTime(ZonedDateTime.parse("2021-04-24T17:57:19.802528800-05:00[America/Chicago]")), writer);
    db.hide(writer);
    db.hide(writer);
    db.show(new NtDateTime(ZonedDateTime.parse("2023-04-24T17:57:19.802528800-05:00[America/Chicago]")), writer);
    db.show(new NtDateTime(ZonedDateTime.parse("2021-04-24T17:57:19.802528800-05:00[America/Chicago]")), writer);
    Assert.assertEquals("\"2023-04-24T17:57:19.802528800-05:00[America/Chicago]\"\"2021-04-24T17:57:19.802528800-05:00[America/Chicago]\"null\"2023-04-24T17:57:19.802528800-05:00[America/Chicago]\"\"2021-04-24T17:57:19.802528800-05:00[America/Chicago]\"", stream.toString());
    Assert.assertEquals(96, db.__memory());
    db.clear();
  }
}
