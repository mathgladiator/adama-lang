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
package org.adamalang.runtime.natives.algo;

import org.adamalang.runtime.natives.*;
import org.junit.Assert;
import org.junit.Test;

import java.time.ZonedDateTime;

public class MessageCSVWriterTests {
  @Test
  public void flow() {
    MessageCSVWriter writer = new MessageCSVWriter();
    writer.write(new NtTime(1, 2));
    writer.write(new NtDateTime(ZonedDateTime.parse("2023-04-24T17:57:19.802528800-05:00[America/Chicago]")));
    writer.write(new NtDate(1,2, 1000));
    writer.write(new NtTimeSpan(42));
    writer.write(new NtPrincipal("agent", "auth"));
    writer.write(new NtComplex(3.14, 2.71));
    Assert.assertEquals("01:02,2023-04-24T17:57:19.802528800-05:00[America/Chicago],1-02-1000,42.0,agent@auth,3.14 2.71i", writer.toString());
  }
}
