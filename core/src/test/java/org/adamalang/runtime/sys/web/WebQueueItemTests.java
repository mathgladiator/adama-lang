/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
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
    BiConsumer<Integer, String> service = (id, s) -> tasks.add(() -> {
      deliverer.deliver(NtPrincipal.NO_ONE, new Key("space", "key"), id, new RemoteResult(s, null, null), true, Callback.DONT_CARE_INTEGER);
    });
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
