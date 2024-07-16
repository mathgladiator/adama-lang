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
import org.adamalang.runtime.mocks.MockRxParent;
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.runtime.reactives.RxInt64;
import org.adamalang.runtime.remote.replication.RxReplicationStatus;
import org.junit.Assert;
import org.junit.Test;

public class DReplicationStatusTests {
  @Test
  public void coverage() {
    DReplicationStatus delta = new DReplicationStatus();
    RxReplicationStatus status = new RxReplicationStatus(new MockRxParent(), new RxInt64(null, 1000), "service", "method");
    {
      final var stream = new JsonStreamWriter();
      final var writer = PrivateLazyDeltaWriter.bind(NtPrincipal.NO_ONE, stream, null, 0);
      delta.show(status, writer);
      Assert.assertEquals(78, delta.__memory());
      delta.hide(writer);
      delta.hide(writer);
      delta.hide(writer);
      Assert.assertEquals(40, delta.__memory());
      delta.show(status, writer);
      delta.show(status, writer);
      delta.show(status, writer);
      delta.show(status, writer);
      delta.clear();
      Assert.assertEquals(40, delta.__memory());
      delta.show(status, writer);
      Assert.assertEquals(78, delta.__memory());
      Assert.assertEquals("\"Nothing;null;null;0\"null\"Nothing;null;null;0\"\"Nothing;null;null;0\"", stream.toString());
    }
  }
}
