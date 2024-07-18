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
package org.adamalang.runtime.async;

import org.adamalang.common.Json;
import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.reactives.RxInt32;
import org.junit.Assert;
import org.junit.Test;

public class IdHistoryLogTests {
  @Test
  public void simple() {
    IdHistoryLog log = new IdHistoryLog();
    RxInt32 base = new RxInt32(null, 0);
    Assert.assertEquals(1, log.next(base));
    Assert.assertEquals(2, log.next(base));
    Assert.assertEquals(3, log.next(base));
    log.revert();
    Assert.assertEquals(1, log.next(base));
    Assert.assertEquals(2, log.next(base));
    Assert.assertEquals(3, log.next(base));
    log.commit();
    Assert.assertEquals(4, log.next(base));
    Assert.assertEquals(5, log.next(base));
    Assert.assertEquals(6, log.next(base));
    JsonStreamWriter writer = new JsonStreamWriter();
    log.dump(writer);
    Assert.assertEquals("[4,5,6]", writer.toString());
  }

  @Test
  public void recover() {
    IdHistoryLog log = IdHistoryLog.read(new JsonStreamReader("[4,5,6]"));
    RxInt32 base = new RxInt32(null, 0);
    Assert.assertEquals(4, log.next(base));
    Assert.assertEquals(5, log.next(base));
    Assert.assertEquals(6, log.next(base));
    Assert.assertEquals(7, log.next(base)); // questionable
  }

  @Test
  public void junk() {
    Assert.assertNull(IdHistoryLog.read(new JsonStreamReader("{}")));
  }
}
