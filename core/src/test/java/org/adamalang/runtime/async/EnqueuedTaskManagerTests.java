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
import org.adamalang.runtime.natives.NtDynamic;
import org.adamalang.runtime.natives.NtPrincipal;
import org.junit.Assert;
import org.junit.Test;

public class EnqueuedTaskManagerTests {
  @Test
  public void flow() {
    EnqueuedTaskManager etm = new EnqueuedTaskManager();
    {
      etm.hydrate(new JsonStreamReader("{}"));
      etm.hydrate(new JsonStreamReader("null"));
      JsonStreamWriter writer = new JsonStreamWriter();
      etm.dump(writer);
      etm.commit(writer, writer);
      Assert.assertEquals("", writer.toString());
    }
    etm.add(new EnqueuedTask(40, new NtPrincipal("agent", "auth"), "ch", new NtDynamic("[1,2]")));
    {
      JsonStreamWriter forward = new JsonStreamWriter();
      JsonStreamWriter reverse = new JsonStreamWriter();
      etm.commit(forward, reverse);
      Assert.assertEquals("\"__enqueued\":{\"40\":{\"who\":{\"agent\":\"agent\",\"authority\":\"auth\"},\"channel\":\"ch\",\"message\":[1,2]}}", forward.toString());
      Assert.assertEquals("\"__enqueued\":{\"40\":null}", reverse.toString());
      JsonStreamWriter dump = new JsonStreamWriter();
      etm.dump(dump);
      Assert.assertEquals("\"__enqueued\":{}", dump.toString());
    }
  }
}
