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
package org.adamalang.runtime.sys.web;

import org.adamalang.common.Callback;
import org.adamalang.common.Json;
import org.adamalang.runtime.async.EphemeralFuture;
import org.adamalang.runtime.data.Key;
import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.mocks.MockDeliverer;
import org.adamalang.runtime.mocks.MockLivingDocument;
import org.adamalang.runtime.mocks.MockMessage;
import org.adamalang.runtime.mocks.MockRxParent;
import org.adamalang.runtime.natives.NtDynamic;
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.runtime.remote.RemoteResult;
import org.adamalang.runtime.remote.RxCache;
import org.adamalang.runtime.remote.ServiceRegistry;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.TreeMap;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public class WebQueueItemTests {
  @Test
  public void junk() {
    Assert.assertNull(WebQueueItem.from(42, new JsonStreamReader("\"xyz\""), null));
  }

  @Test
  public void coverage_flow() {
    MockLivingDocument doc = new MockLivingDocument();
    MockDeliverer deliverer = new MockDeliverer();
    ServiceRegistry registry = new ServiceRegistry();
    doc.__lateBind("space", "key", deliverer, registry);
    MockRxParent parent = new MockRxParent();
    RxCache cache = new RxCache(doc, parent);
    ArrayList<Runnable> tasks = new ArrayList<>();
    BiFunction<Integer, String, RemoteResult> service = (id, s) -> {
      tasks.add(() -> {
        deliverer.deliver(NtPrincipal.NO_ONE, new Key("space", "key"), id, new RemoteResult(s, null, null), true, Callback.DONT_CARE_INTEGER);
      });
      return null;
    };
    WebContext context = new WebContext(NtPrincipal.NO_ONE, "origin", "ip");
    EphemeralFuture<WebResponse> fut = new EphemeralFuture<>();
    WebQueueItem item = new WebQueueItem(100, context, new WebPut(context, "/uri", new TreeMap<>(), new NtDynamic("{}"), "{\"body\":123}"), cache, fut);
    {
      JsonStreamWriter writer = new JsonStreamWriter();
      item.dump(writer);
      Assert.assertEquals("{\"context\":{\"who\":{\"agent\":\"?\",\"authority\":\"?\"},\"origin\":\"origin\",\"ip\":\"ip\"},\"item\":{\"put\":{\"uri\":\"/uri\",\"headers\":{},\"parameters\":{},\"bodyJson\":{\"body\":123}}},\"cache\":{}}", writer.toString());
      Json.parseJsonObject(writer.toString());
    }
    {
      JsonStreamWriter forward = new JsonStreamWriter();
      JsonStreamWriter reverse = new JsonStreamWriter();
      item.commit(42, forward, reverse);
      Assert.assertEquals("", forward.toString());
      Assert.assertEquals("", reverse.toString());
    }
    cache.answer("service", "method", NtPrincipal.NO_ONE, new MockMessage(123, 42), (str) -> new MockMessage(new JsonStreamReader(str)), service);
    {
      JsonStreamWriter forward = new JsonStreamWriter();
      JsonStreamWriter reverse = new JsonStreamWriter();
      item.commit(42, forward, reverse);
      Assert.assertEquals("\"42\":{\"cache\":{\"1\":{\"invoke\":{\"service\":\"service\",\"method\":\"method\",\"who\":{\"agent\":\"?\",\"authority\":\"?\"},\"parameter\":{\"x\":123,\"y\":42}},\"result\":{\"result\":null,\"failure\":null,\"failure_code\":null}}}}", forward.toString());
      Assert.assertEquals("\"42\":{\"cache\":{\"1\":null}}", reverse.toString());
      Json.parseJsonObject("{" + forward + "}");
      Json.parseJsonObject("{" + reverse + "}");
    }
    item.patch(new JsonStreamReader("{\"junk\":1,\"cache\":{\"1\":{\"result\":{\"failure_code\":42}}}}"));
    item.patch(new JsonStreamReader("\"junk\""));
    {
      JsonStreamWriter writer = new JsonStreamWriter();
      item.dump(writer);
      Assert.assertEquals("{\"context\":{\"who\":{\"agent\":\"?\",\"authority\":\"?\"},\"origin\":\"origin\",\"ip\":\"ip\"},\"item\":{\"put\":{\"uri\":\"/uri\",\"headers\":{},\"parameters\":{},\"bodyJson\":{\"body\":123}}},\"cache\":{\"1\":{\"invoke\":{\"service\":\"service\",\"method\":\"method\",\"who\":{\"agent\":\"?\",\"authority\":\"?\"},\"parameter\":{\"x\":123,\"y\":42}},\"result\":{\"result\":null,\"failure\":null,\"failure_code\":42}}}}", writer.toString());
      Json.parseJsonObject(writer.toString());
    }
    {
      JsonStreamWriter forward = new JsonStreamWriter();
      JsonStreamWriter reverse = new JsonStreamWriter();
      item.commit(42, forward, reverse);
      Assert.assertEquals("\"42\":{\"cache\":{\"1\":{\"result\":{\"result\":null,\"failure\":null,\"failure_code\":42}}}}", forward.toString());
      Assert.assertEquals("\"42\":{\"cache\":{\"1\":{\"result\":{\"result\":null,\"failure\":null,\"failure_code\":null}}}}", reverse.toString());
      Json.parseJsonObject("{" + forward + "}");
      Json.parseJsonObject("{" + reverse + "}");
    }
    for (Runnable task : tasks) {
      task.run();
    }
    deliverer.deliverAllTo(cache);
    {
      JsonStreamWriter forward = new JsonStreamWriter();
      JsonStreamWriter reverse = new JsonStreamWriter();
      item.commit(42, forward, reverse);
      Assert.assertEquals("\"42\":{\"cache\":{\"1\":{\"result\":{\"result\":{\"x\":123,\"y\":42},\"failure\":null,\"failure_code\":null}}}}", forward.toString());
      Assert.assertEquals("\"42\":{\"cache\":{\"1\":{\"result\":{\"result\":null,\"failure\":null,\"failure_code\":42}}}}", reverse.toString());
      Json.parseJsonObject("{" + forward + "}");
      Json.parseJsonObject("{" + reverse + "}");
    }
  }
}
