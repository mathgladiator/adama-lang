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
      Assert.assertEquals(0, etm.size());
    }
    Assert.assertEquals(0, etm.size());
    Assert.assertFalse(etm.readyForTransfer());
    etm.add(new EnqueuedTask(40, new NtPrincipal("agent", "auth"), "ch", -1, new NtDynamic("[1,2]")));
    Assert.assertFalse(etm.readyForTransfer());
    Assert.assertEquals(1, etm.size());
    {
      JsonStreamWriter forward = new JsonStreamWriter();
      JsonStreamWriter reverse = new JsonStreamWriter();
      etm.commit(forward, reverse);
      Assert.assertEquals("\"__enqueued\":{\"40\":{\"who\":{\"agent\":\"agent\",\"authority\":\"auth\"},\"channel\":\"ch\",\"message\":[1,2]}}", forward.toString());
      Assert.assertEquals("\"__enqueued\":{\"40\":null}", reverse.toString());
      JsonStreamWriter dump = new JsonStreamWriter();
      etm.dump(dump);
      Assert.assertEquals("\"__enqueued\":{\"40\":{\"who\":{\"agent\":\"agent\",\"authority\":\"auth\"},\"channel\":\"ch\",\"message\":[1,2]}}", dump.toString());
    }
    Assert.assertTrue(etm.readyForTransfer());
    EnqueuedTask pulled = etm.transfer();
    Assert.assertNotNull(pulled);
    Assert.assertEquals(0, etm.size());
    {
      JsonStreamWriter forward = new JsonStreamWriter();
      JsonStreamWriter reverse = new JsonStreamWriter();
      etm.commit(forward, reverse);
      Assert.assertEquals("\"__enqueued\":{\"40\":null}", forward.toString());
      Assert.assertEquals("\"__enqueued\":{\"40\":{\"who\":{\"agent\":\"agent\",\"authority\":\"auth\"},\"channel\":\"ch\",\"message\":[1,2]}}", reverse.toString());
      JsonStreamWriter dump = new JsonStreamWriter();
      etm.dump(dump);
      Assert.assertEquals("", dump.toString());
    }
    {
      JsonStreamWriter dump = new JsonStreamWriter();
      etm.dump(dump);
      Assert.assertEquals("", dump.toString());
    }
  }

  @Test
  public void flow_w_view_id() {
    EnqueuedTaskManager etm = new EnqueuedTaskManager();
    {
      etm.hydrate(new JsonStreamReader("{}"));
      etm.hydrate(new JsonStreamReader("null"));
      JsonStreamWriter writer = new JsonStreamWriter();
      etm.dump(writer);
      etm.commit(writer, writer);
      Assert.assertEquals("", writer.toString());
      Assert.assertEquals(0, etm.size());
    }
    Assert.assertEquals(0, etm.size());
    Assert.assertFalse(etm.readyForTransfer());
    etm.add(new EnqueuedTask(40, new NtPrincipal("agent", "auth"), "ch", 42, new NtDynamic("[1,2]")));
    Assert.assertFalse(etm.readyForTransfer());
    Assert.assertEquals(1, etm.size());
    {
      JsonStreamWriter forward = new JsonStreamWriter();
      JsonStreamWriter reverse = new JsonStreamWriter();
      etm.commit(forward, reverse);
      Assert.assertEquals("\"__enqueued\":{\"40\":{\"who\":{\"agent\":\"agent\",\"authority\":\"auth\"},\"channel\":\"ch\",\"view_id\":42,\"message\":[1,2]}}", forward.toString());
      Assert.assertEquals("\"__enqueued\":{\"40\":null}", reverse.toString());
      JsonStreamWriter dump = new JsonStreamWriter();
      etm.dump(dump);
      Assert.assertEquals("\"__enqueued\":{\"40\":{\"who\":{\"agent\":\"agent\",\"authority\":\"auth\"},\"channel\":\"ch\",\"view_id\":42,\"message\":[1,2]}}", dump.toString());
    }
    Assert.assertTrue(etm.readyForTransfer());
    EnqueuedTask pulled = etm.transfer();
    Assert.assertNotNull(pulled);
    Assert.assertEquals(0, etm.size());
    {
      JsonStreamWriter forward = new JsonStreamWriter();
      JsonStreamWriter reverse = new JsonStreamWriter();
      etm.commit(forward, reverse);
      Assert.assertEquals("\"__enqueued\":{\"40\":null}", forward.toString());
      Assert.assertEquals("\"__enqueued\":{\"40\":{\"who\":{\"agent\":\"agent\",\"authority\":\"auth\"},\"channel\":\"ch\",\"view_id\":42,\"message\":[1,2]}}", reverse.toString());
      JsonStreamWriter dump = new JsonStreamWriter();
      etm.dump(dump);
      Assert.assertEquals("", dump.toString());
    }
    {
      JsonStreamWriter dump = new JsonStreamWriter();
      etm.dump(dump);
      Assert.assertEquals("", dump.toString());
    }
  }

  @Test
  public void hydration() {
    EnqueuedTaskManager etm = new EnqueuedTaskManager();
    etm.hydrate(new JsonStreamReader("{\"1\":{\"channel\":\"ch\",\"who\":{\"agent\":\"a\",\"authority\":\"b\"},\"message\":{}}}}"));
    etm.hydrate(new JsonStreamReader("{\"1\":{\"x\":true}}"));
    {
      JsonStreamWriter writer = new JsonStreamWriter();
      etm.dump(writer);
      Assert.assertEquals("\"__enqueued\":{\"1\":{\"who\":{\"agent\":\"a\",\"authority\":\"b\"},\"channel\":\"ch\",\"message\":{}}}", writer.toString());
    }
    Assert.assertTrue(etm.readyForTransfer());
    EnqueuedTask et = etm.transfer();
    Assert.assertEquals("ch", et.channel);
    {
      JsonStreamWriter writer = new JsonStreamWriter();
      etm.dump(writer);
      Assert.assertEquals("", writer.toString());
    }
  }

  @Test
  public void hydration_view_id() {
    EnqueuedTaskManager etm = new EnqueuedTaskManager();
    etm.hydrate(new JsonStreamReader("{\"1\":{\"channel\":\"ch\",\"who\":{\"agent\":\"a\",\"authority\":\"b\"},\"view_id\":400,\"message\":{}}}}"));
    etm.hydrate(new JsonStreamReader("{\"1\":{\"x\":true}}"));
    {
      JsonStreamWriter writer = new JsonStreamWriter();
      etm.dump(writer);
      Assert.assertEquals("\"__enqueued\":{\"1\":{\"who\":{\"agent\":\"a\",\"authority\":\"b\"},\"channel\":\"ch\",\"view_id\":400,\"message\":{}}}", writer.toString());
    }
    Assert.assertTrue(etm.readyForTransfer());
    EnqueuedTask et = etm.transfer();
    Assert.assertEquals("ch", et.channel);
    Assert.assertEquals(400, et.viewId);
    {
      JsonStreamWriter writer = new JsonStreamWriter();
      etm.dump(writer);
      Assert.assertEquals("", writer.toString());
    }
  }
}
