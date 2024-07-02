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
package org.adamalang.runtime.natives;

import org.junit.Assert;
import org.junit.Test;

import java.time.ZonedDateTime;

public class NtDateTimeTests {
  @Test
  public void coverage() {
    NtDateTime dt = new NtDateTime(ZonedDateTime.parse("2023-04-24T17:57:19.802528800-05:00[America/Chicago]"));
    Assert.assertEquals(dt, dt);
    Assert.assertEquals(dt, new NtDateTime(ZonedDateTime.parse("2023-04-24T17:57:19.802528800-05:00[America/Chicago]")));
    Assert.assertNotEquals(dt, new NtDateTime(ZonedDateTime.parse("2021-04-24T17:57:19.802528800-05:00[America/Chicago]")));
    Assert.assertNotEquals(dt, "");
    Assert.assertNotEquals(dt, null);
    dt.hashCode();
    Assert.assertEquals("2023-04-24T17:57:19.802528800-05:00[America/Chicago]", dt.toString());
    Assert.assertEquals(64, dt.memory());
  }

  @Test
  public void toint() {
    NtDateTime dt = new NtDateTime(ZonedDateTime.parse("2023-04-24T17:57:19.802528800-05:00[America/Chicago]"));
    Assert.assertEquals(28039617, dt.toInt());
  }
}
