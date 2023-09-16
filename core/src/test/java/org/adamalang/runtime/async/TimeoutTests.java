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
package org.adamalang.runtime.async;

import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.json.JsonStreamWriter;
import org.junit.Assert;
import org.junit.Test;

public class TimeoutTests {
  @Test
  public void write() {
    Timeout x = new Timeout(123, 4.2);
    JsonStreamWriter writer = new JsonStreamWriter();
    x.write(writer);
    Assert.assertEquals("{\"timestamp\":\"123\",\"timeout\":4.2}", writer.toString());
  }

  @Test
  public void readFrom() {
    Timeout x = Timeout.readFrom(new JsonStreamReader("{\"timestamp\":\"123\",\"timeout\":4.2,\"junk\":true}"));
    Assert.assertEquals(123, x.timestamp);
    Assert.assertEquals(4.2, x.timeoutSeconds, 0.1);
  }

  @Test
  public void junk() {
    Timeout x = Timeout.readFrom(new JsonStreamReader("\"timeout\""));
    Assert.assertNull(x);
  }
}
