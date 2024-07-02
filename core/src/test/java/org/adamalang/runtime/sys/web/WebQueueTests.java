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
import org.adamalang.common.ErrorCodeException;
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
import org.adamalang.runtime.reactives.RxInt32;
import org.adamalang.runtime.remote.DelayParent;
import org.adamalang.runtime.remote.RemoteResult;
import org.adamalang.runtime.remote.RxCache;
import org.adamalang.runtime.remote.ServiceRegistry;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public class WebQueueTests {
  @Test
  public void coverage_cancel() {
    MockLivingDocument doc = new MockLivingDocument();
    MockDeliverer deliverer = new MockDeliverer();
    ServiceRegistry registry = new ServiceRegistry();
    doc.__lateBind("space", "key", deliverer, registry);
    DelayParent delay = new DelayParent();
    RxCache cache = new RxCache(doc, delay);
    ArrayList<Runnable> tasks = new ArrayList<>();
    BiConsumer<Integer, String> service = (id, s) -> tasks.add(() -> {
      deliverer.deliver(NtPrincipal.NO_ONE, new Key("space", "key"), id, new RemoteResult(s, null, null), true, Callback.DONT_CARE_INTEGER);
    });
    WebContext context = new WebContext(NtPrincipal.NO_ONE, "origin", "ip");
    EphemeralFuture<WebResponse> fut = new EphemeralFuture<>();
    RxInt32 taskId = new RxInt32(doc, 1);
    WebQueue queue = new WebQueue(taskId);
    {
      JsonStreamWriter writer = new JsonStreamWriter();
      queue.dump(writer);
      Assert.assertEquals("\"__webqueue\":{}", writer.toString());
      Json.parseJsonObject("{" + writer.toString() + "}");
    }

    queue.queue(context, new WebPut(context, "/uri", new TreeMap<>(), new NtDynamic("{}"), "{\"body\":123}"), fut, cache, delay);
    queue.dirty();

    {
      JsonStreamWriter writer = new JsonStreamWriter();
      queue.dump(writer);
      Assert.assertEquals("\"__webqueue\":{\"2\":{\"context\":{\"who\":{\"agent\":\"?\",\"authority\":\"?\"},\"origin\":\"origin\",\"ip\":\"ip\"},\"item\":{\"put\":{\"uri\":\"/uri\",\"headers\":{},\"parameters\":{},\"bodyJson\":{\"body\":123}}},\"cache\":{}}}", writer.toString());
      Json.parseJsonObject("{" + writer.toString() + "}");
    }

    AtomicBoolean saw_failure = new AtomicBoolean(false);
    fut.attach(new Callback<WebResponse>() {
      @Override
      public void success(WebResponse value) {

      }

      @Override
      public void failure(ErrorCodeException ex) {
        saw_failure.set(true);
      }
    });
    queue.cancel();
    Assert.assertTrue(saw_failure.get());
  }

  @Test
  public void coverage_delivery_put() {
    MockLivingDocument doc = new MockLivingDocument();
    MockDeliverer deliverer = new MockDeliverer();
    ServiceRegistry registry = new ServiceRegistry();
    doc.__lateBind("space", "key", deliverer, registry);
    DelayParent delay = new DelayParent();
    RxCache cache = new RxCache(doc, delay);
    ArrayList<Runnable> tasks = new ArrayList<>();
    BiFunction<Integer, String, RemoteResult> service = (id, s) -> {
      tasks.add(() -> {
        deliverer.deliver(NtPrincipal.NO_ONE, new Key("space", "key"), id, new RemoteResult(s, null, null), true, Callback.DONT_CARE_INTEGER);
      });
      return null;
    };
    WebContext context = new WebContext(NtPrincipal.NO_ONE, "origin", "ip");
    EphemeralFuture<WebResponse> fut = new EphemeralFuture<>();
    RxInt32 taskId = new RxInt32(doc, 1);
    WebQueue queue = new WebQueue(taskId);
    {
      JsonStreamWriter writer = new JsonStreamWriter();
      queue.dump(writer);
      Assert.assertEquals("\"__webqueue\":{}", writer.toString());
      Json.parseJsonObject("{" + writer.toString() + "}");
      System.err.println("{" + writer.toString() + "}");
    }
    queue.queue(context, new WebPut(context, "/uri", new TreeMap<>(), new NtDynamic("{}"), "{\"body\":123}"), fut, cache, delay);
    queue.dirty();
    {
      JsonStreamWriter writer = new JsonStreamWriter();
      queue.dump(writer);
      Assert.assertEquals("\"__webqueue\":{\"2\":{\"context\":{\"who\":{\"agent\":\"?\",\"authority\":\"?\"},\"origin\":\"origin\",\"ip\":\"ip\"},\"item\":{\"put\":{\"uri\":\"/uri\",\"headers\":{},\"parameters\":{},\"bodyJson\":{\"body\":123}}},\"cache\":{}}}", writer.toString());
      Json.parseJsonObject("{" + writer.toString() + "}");
    }

    {
      JsonStreamWriter writer = new JsonStreamWriter();
      queue.dump(writer);
      Assert.assertEquals("\"__webqueue\":{\"2\":{\"context\":{\"who\":{\"agent\":\"?\",\"authority\":\"?\"},\"origin\":\"origin\",\"ip\":\"ip\"},\"item\":{\"put\":{\"uri\":\"/uri\",\"headers\":{},\"parameters\":{},\"bodyJson\":{\"body\":123}}},\"cache\":{}}}", writer.toString());
      Json.parseJsonObject("{" + writer.toString() + "}");
      System.err.println("{" + writer.toString() + "}");
    }
    {
      JsonStreamWriter forward = new JsonStreamWriter();
      JsonStreamWriter reverse = new JsonStreamWriter();
      queue.commit(forward, reverse);
      Assert.assertEquals("\"__webqueue\":{\"2\":{\"context\":{\"who\":{\"agent\":\"?\",\"authority\":\"?\"},\"origin\":\"origin\",\"ip\":\"ip\"},\"item\":{\"put\":{\"uri\":\"/uri\",\"headers\":{},\"parameters\":{},\"bodyJson\":{\"body\":123}}},\"cache\":{}}}", forward.toString());
      Assert.assertEquals("\"__webqueue\":{\"2\":null}", reverse.toString());
      Json.parseJsonObject("{" + forward + "}");
      Json.parseJsonObject("{" + reverse + "}");
    }
    cache.answer("service", "method", NtPrincipal.NO_ONE, new MockMessage(123, 42), (str) -> new MockMessage(new JsonStreamReader(str)), service);
    {
      JsonStreamWriter forward = new JsonStreamWriter();
      JsonStreamWriter reverse = new JsonStreamWriter();
      queue.commit(forward, reverse);
      Assert.assertEquals("\"__webqueue\":{\"2\":{\"cache\":{\"1\":{\"invoke\":{\"service\":\"service\",\"method\":\"method\",\"who\":{\"agent\":\"?\",\"authority\":\"?\"},\"parameter\":{\"x\":123,\"y\":42}},\"result\":{\"result\":null,\"failure\":null,\"failure_code\":null}}}}}", forward.toString());
      Assert.assertEquals("\"__webqueue\":{\"2\":{\"cache\":{\"1\":null}}}", reverse.toString());
      Json.parseJsonObject("{" + forward + "}");
      Json.parseJsonObject("{" + reverse + "}");
    }
    {
      JsonStreamWriter writer = new JsonStreamWriter();
      queue.dump(writer);
      Assert.assertEquals("\"__webqueue\":{\"2\":{\"context\":{\"who\":{\"agent\":\"?\",\"authority\":\"?\"},\"origin\":\"origin\",\"ip\":\"ip\"},\"item\":{\"put\":{\"uri\":\"/uri\",\"headers\":{},\"parameters\":{},\"bodyJson\":{\"body\":123}}},\"cache\":{\"1\":{\"invoke\":{\"service\":\"service\",\"method\":\"method\",\"who\":{\"agent\":\"?\",\"authority\":\"?\"},\"parameter\":{\"x\":123,\"y\":42}},\"result\":{\"result\":null,\"failure\":null,\"failure_code\":null}}}}}", writer.toString());
      Json.parseJsonObject("{" + writer.toString() + "}");
      System.err.println("{" + writer.toString() + "}");
    }
    for (Runnable task : tasks) {
      task.run();
    }
    deliverer.deliverAllTo(cache);
    {
      JsonStreamWriter forward = new JsonStreamWriter();
      JsonStreamWriter reverse = new JsonStreamWriter();
      queue.commit(forward, reverse);
      Assert.assertEquals("\"__webqueue\":{\"2\":{\"cache\":{\"1\":{\"result\":{\"result\":{\"x\":123,\"y\":42},\"failure\":null,\"failure_code\":null}}}}}", forward.toString());
      Assert.assertEquals("\"__webqueue\":{\"2\":{\"cache\":{\"1\":{\"result\":{\"result\":null,\"failure\":null,\"failure_code\":null}}}}}", reverse.toString());
      Json.parseJsonObject("{" + forward + "}");
      Json.parseJsonObject("{" + reverse + "}");
    }
    {
      JsonStreamWriter writer = new JsonStreamWriter();
      queue.dump(writer);
      Assert.assertEquals("\"__webqueue\":{\"2\":{\"context\":{\"who\":{\"agent\":\"?\",\"authority\":\"?\"},\"origin\":\"origin\",\"ip\":\"ip\"},\"item\":{\"put\":{\"uri\":\"/uri\",\"headers\":{},\"parameters\":{},\"bodyJson\":{\"body\":123}}},\"cache\":{\"1\":{\"invoke\":{\"service\":\"service\",\"method\":\"method\",\"who\":{\"agent\":\"?\",\"authority\":\"?\"},\"parameter\":{\"x\":123,\"y\":42}},\"result\":{\"result\":{\"x\":123,\"y\":42},\"failure\":null,\"failure_code\":null}}}}}", writer.toString());
      Json.parseJsonObject("{" + writer.toString() + "}");
      System.err.println("{" + writer.toString() + "}");
    }
  }

  @Test
  public void coverage_delivery_instant() {
    MockLivingDocument doc = new MockLivingDocument();
    MockDeliverer deliverer = new MockDeliverer();
    ServiceRegistry registry = new ServiceRegistry();
    doc.__lateBind("space", "key", deliverer, registry);
    DelayParent delay = new DelayParent();
    RxCache cache = new RxCache(doc, delay);
    ArrayList<Runnable> tasks = new ArrayList<>();
    BiFunction<Integer, String, RemoteResult> service = (id, s) -> {
      return new RemoteResult(s, null, null);
    };
    WebContext context = new WebContext(NtPrincipal.NO_ONE, "origin", "ip");
    EphemeralFuture<WebResponse> fut = new EphemeralFuture<>();
    RxInt32 taskId = new RxInt32(doc, 1);
    WebQueue queue = new WebQueue(taskId);
    {
      JsonStreamWriter writer = new JsonStreamWriter();
      queue.dump(writer);
      Assert.assertEquals("\"__webqueue\":{}", writer.toString());
      Json.parseJsonObject("{" + writer.toString() + "}");
      System.err.println("{" + writer.toString() + "}");
    }
    queue.queue(context, new WebPut(context, "/uri", new TreeMap<>(), new NtDynamic("{}"), "{\"body\":123}"), fut, cache, delay);
    queue.dirty();
    {
      JsonStreamWriter writer = new JsonStreamWriter();
      queue.dump(writer);
      Assert.assertEquals("\"__webqueue\":{\"2\":{\"context\":{\"who\":{\"agent\":\"?\",\"authority\":\"?\"},\"origin\":\"origin\",\"ip\":\"ip\"},\"item\":{\"put\":{\"uri\":\"/uri\",\"headers\":{},\"parameters\":{},\"bodyJson\":{\"body\":123}}},\"cache\":{}}}", writer.toString());
      Json.parseJsonObject("{" + writer.toString() + "}");
    }

    {
      JsonStreamWriter writer = new JsonStreamWriter();
      queue.dump(writer);
      Assert.assertEquals("\"__webqueue\":{\"2\":{\"context\":{\"who\":{\"agent\":\"?\",\"authority\":\"?\"},\"origin\":\"origin\",\"ip\":\"ip\"},\"item\":{\"put\":{\"uri\":\"/uri\",\"headers\":{},\"parameters\":{},\"bodyJson\":{\"body\":123}}},\"cache\":{}}}", writer.toString());
      Json.parseJsonObject("{" + writer.toString() + "}");
      System.err.println("{" + writer.toString() + "}");
    }
    {
      JsonStreamWriter forward = new JsonStreamWriter();
      JsonStreamWriter reverse = new JsonStreamWriter();
      queue.commit(forward, reverse);
      Assert.assertEquals("\"__webqueue\":{\"2\":{\"context\":{\"who\":{\"agent\":\"?\",\"authority\":\"?\"},\"origin\":\"origin\",\"ip\":\"ip\"},\"item\":{\"put\":{\"uri\":\"/uri\",\"headers\":{},\"parameters\":{},\"bodyJson\":{\"body\":123}}},\"cache\":{}}}", forward.toString());
      Assert.assertEquals("\"__webqueue\":{\"2\":null}", reverse.toString());
      Json.parseJsonObject("{" + forward + "}");
      Json.parseJsonObject("{" + reverse + "}");
    }
    cache.answer("service", "method", NtPrincipal.NO_ONE, new MockMessage(123, 42), (str) -> new MockMessage(new JsonStreamReader(str)), service);
    {
      JsonStreamWriter forward = new JsonStreamWriter();
      JsonStreamWriter reverse = new JsonStreamWriter();
      queue.commit(forward, reverse);
      Assert.assertEquals("\"__webqueue\":{\"2\":{\"cache\":{\"1\":{\"invoke\":{\"service\":\"service\",\"method\":\"method\",\"who\":{\"agent\":\"?\",\"authority\":\"?\"},\"parameter\":{\"x\":123,\"y\":42}},\"result\":{\"result\":{\"x\":123,\"y\":42},\"failure\":null,\"failure_code\":null}}}}}", forward.toString());
      Assert.assertEquals("\"__webqueue\":{\"2\":{\"cache\":{\"1\":null}}}", reverse.toString());
      Json.parseJsonObject("{" + forward + "}");
      Json.parseJsonObject("{" + reverse + "}");
    }
    {
      JsonStreamWriter writer = new JsonStreamWriter();
      queue.dump(writer);
      Assert.assertEquals("\"__webqueue\":{\"2\":{\"context\":{\"who\":{\"agent\":\"?\",\"authority\":\"?\"},\"origin\":\"origin\",\"ip\":\"ip\"},\"item\":{\"put\":{\"uri\":\"/uri\",\"headers\":{},\"parameters\":{},\"bodyJson\":{\"body\":123}}},\"cache\":{\"1\":{\"invoke\":{\"service\":\"service\",\"method\":\"method\",\"who\":{\"agent\":\"?\",\"authority\":\"?\"},\"parameter\":{\"x\":123,\"y\":42}},\"result\":{\"result\":{\"x\":123,\"y\":42},\"failure\":null,\"failure_code\":null}}}}}", writer.toString());
      Json.parseJsonObject("{" + writer.toString() + "}");
      System.err.println("{" + writer.toString() + "}");
    }
  }

  @Test
  public void coverage_delivery_delete() {
    MockLivingDocument doc = new MockLivingDocument();
    MockDeliverer deliverer = new MockDeliverer();
    ServiceRegistry registry = new ServiceRegistry();
    doc.__lateBind("space", "key", deliverer, registry);
    DelayParent delay = new DelayParent();
    RxCache cache = new RxCache(doc, delay);
    ArrayList<Runnable> tasks = new ArrayList<>();
    BiFunction<Integer, String, RemoteResult> service = (id, s) -> {
      tasks.add(() -> {
        deliverer.deliver(NtPrincipal.NO_ONE, new Key("space", "key"), id, new RemoteResult(s, null, null), true, Callback.DONT_CARE_INTEGER);
      });
      return null;
    };
    WebContext context = new WebContext(NtPrincipal.NO_ONE, "origin", "ip");
    EphemeralFuture<WebResponse> fut = new EphemeralFuture<>();
    RxInt32 taskId = new RxInt32(doc, 1);
    WebQueue queue = new WebQueue(taskId);
    {
      JsonStreamWriter writer = new JsonStreamWriter();
      queue.dump(writer);
      Assert.assertEquals("\"__webqueue\":{}", writer.toString());
      Json.parseJsonObject("{" + writer.toString() + "}");
      System.err.println("{" + writer.toString() + "}");
    }
    queue.queue(context, new WebDelete(context, "/uri", new TreeMap<>(), new NtDynamic("{}")), fut, cache, delay);
    queue.dirty();
    {
      JsonStreamWriter writer = new JsonStreamWriter();
      queue.dump(writer);
      Assert.assertEquals("\"__webqueue\":{\"2\":{\"context\":{\"who\":{\"agent\":\"?\",\"authority\":\"?\"},\"origin\":\"origin\",\"ip\":\"ip\"},\"item\":{\"delete\":{\"uri\":\"/uri\",\"headers\":{},\"parameters\":{}}},\"cache\":{}}}", writer.toString());
      Json.parseJsonObject("{" + writer.toString() + "}");
    }

    {
      JsonStreamWriter writer = new JsonStreamWriter();
      queue.dump(writer);
      Assert.assertEquals("\"__webqueue\":{\"2\":{\"context\":{\"who\":{\"agent\":\"?\",\"authority\":\"?\"},\"origin\":\"origin\",\"ip\":\"ip\"},\"item\":{\"delete\":{\"uri\":\"/uri\",\"headers\":{},\"parameters\":{}}},\"cache\":{}}}", writer.toString());
      Json.parseJsonObject("{" + writer.toString() + "}");
      System.err.println("{" + writer.toString() + "}");
    }
    {
      JsonStreamWriter forward = new JsonStreamWriter();
      JsonStreamWriter reverse = new JsonStreamWriter();
      queue.commit(forward, reverse);
      Assert.assertEquals("\"__webqueue\":{\"2\":{\"context\":{\"who\":{\"agent\":\"?\",\"authority\":\"?\"},\"origin\":\"origin\",\"ip\":\"ip\"},\"item\":{\"delete\":{\"uri\":\"/uri\",\"headers\":{},\"parameters\":{}}},\"cache\":{}}}", forward.toString());
      Assert.assertEquals("\"__webqueue\":{\"2\":null}", reverse.toString());
      Json.parseJsonObject("{" + forward + "}");
      Json.parseJsonObject("{" + reverse + "}");
    }
    cache.answer("service", "method", NtPrincipal.NO_ONE, new MockMessage(123, 42), (str) -> new MockMessage(new JsonStreamReader(str)), service);
    {
      JsonStreamWriter forward = new JsonStreamWriter();
      JsonStreamWriter reverse = new JsonStreamWriter();
      queue.commit(forward, reverse);
      Assert.assertEquals("\"__webqueue\":{\"2\":{\"cache\":{\"1\":{\"invoke\":{\"service\":\"service\",\"method\":\"method\",\"who\":{\"agent\":\"?\",\"authority\":\"?\"},\"parameter\":{\"x\":123,\"y\":42}},\"result\":{\"result\":null,\"failure\":null,\"failure_code\":null}}}}}", forward.toString());
      Assert.assertEquals("\"__webqueue\":{\"2\":{\"cache\":{\"1\":null}}}", reverse.toString());
      Json.parseJsonObject("{" + forward + "}");
      Json.parseJsonObject("{" + reverse + "}");
    }
    {
      JsonStreamWriter writer = new JsonStreamWriter();
      queue.dump(writer);
      Assert.assertEquals("\"__webqueue\":{\"2\":{\"context\":{\"who\":{\"agent\":\"?\",\"authority\":\"?\"},\"origin\":\"origin\",\"ip\":\"ip\"},\"item\":{\"delete\":{\"uri\":\"/uri\",\"headers\":{},\"parameters\":{}}},\"cache\":{\"1\":{\"invoke\":{\"service\":\"service\",\"method\":\"method\",\"who\":{\"agent\":\"?\",\"authority\":\"?\"},\"parameter\":{\"x\":123,\"y\":42}},\"result\":{\"result\":null,\"failure\":null,\"failure_code\":null}}}}}", writer.toString());
      Json.parseJsonObject("{" + writer.toString() + "}");
      System.err.println("{" + writer.toString() + "}");
    }
    for (Runnable task : tasks) {
      task.run();
    }
    deliverer.deliverAllTo(cache);
    {
      JsonStreamWriter forward = new JsonStreamWriter();
      JsonStreamWriter reverse = new JsonStreamWriter();
      queue.commit(forward, reverse);
      Assert.assertEquals("\"__webqueue\":{\"2\":{\"cache\":{\"1\":{\"result\":{\"result\":{\"x\":123,\"y\":42},\"failure\":null,\"failure_code\":null}}}}}", forward.toString());
      Assert.assertEquals("\"__webqueue\":{\"2\":{\"cache\":{\"1\":{\"result\":{\"result\":null,\"failure\":null,\"failure_code\":null}}}}}", reverse.toString());
      Json.parseJsonObject("{" + forward + "}");
      Json.parseJsonObject("{" + reverse + "}");
    }
    {
      JsonStreamWriter writer = new JsonStreamWriter();
      queue.dump(writer);
      Assert.assertEquals("\"__webqueue\":{\"2\":{\"context\":{\"who\":{\"agent\":\"?\",\"authority\":\"?\"},\"origin\":\"origin\",\"ip\":\"ip\"},\"item\":{\"delete\":{\"uri\":\"/uri\",\"headers\":{},\"parameters\":{}}},\"cache\":{\"1\":{\"invoke\":{\"service\":\"service\",\"method\":\"method\",\"who\":{\"agent\":\"?\",\"authority\":\"?\"},\"parameter\":{\"x\":123,\"y\":42}},\"result\":{\"result\":{\"x\":123,\"y\":42},\"failure\":null,\"failure_code\":null}}}}}", writer.toString());
      Json.parseJsonObject("{" + writer.toString() + "}");
      System.err.println("{" + writer.toString() + "}");
    }
  }

  @Test
  public void hydrate_empty() {
    MockLivingDocument doc = new MockLivingDocument();
    JsonStreamReader reader = new JsonStreamReader("{}");
    RxInt32 taskId = new RxInt32(doc, 1);
    WebQueue queue = new WebQueue(taskId);
    queue.hydrate(reader, doc);
    JsonStreamWriter writer = new JsonStreamWriter();
    queue.dump(writer);
    Assert.assertEquals("\"__webqueue\":{}", writer.toString());
    Json.parseJsonObject("{" + writer.toString() + "}");
  }

  @Test
  public void hydrate_setup_put() {
    MockLivingDocument doc = new MockLivingDocument();
    JsonStreamReader reader = new JsonStreamReader("{\"2\":{\"context\":{\"who\":{\"agent\":\"?\",\"authority\":\"?\"},\"origin\":\"origin\",\"ip\":\"ip\"},\"item\":{\"put\":{\"uri\":\"/uri\",\"headers\":{},\"parameters\":{},\"bodyJson\":{\"body\":123}}},\"cache\":{}}}");
    RxInt32 taskId = new RxInt32(doc, 1);
    WebQueue queue = new WebQueue(taskId);
    queue.hydrate(reader, doc);
    JsonStreamWriter writer = new JsonStreamWriter();
    queue.dump(writer);
    Assert.assertEquals("\"__webqueue\":{\"2\":{\"context\":{\"who\":{\"agent\":\"?\",\"authority\":\"?\"},\"origin\":\"origin\",\"ip\":\"ip\"},\"item\":{\"put\":{\"uri\":\"/uri\",\"headers\":{},\"parameters\":{},\"bodyJson\":{\"body\":123}}},\"cache\":{}}}", writer.toString());
    Json.parseJsonObject("{" + writer.toString() + "}");
  }


  @Test
  public void hydrate_setup_delete() {
    MockLivingDocument doc = new MockLivingDocument();
    JsonStreamReader reader = new JsonStreamReader("{\"2\":{\"context\":{\"who\":{\"agent\":\"?\",\"authority\":\"?\"},\"origin\":\"origin\",\"ip\":\"ip\"},\"item\":{\"delete\":{\"uri\":\"/uri\",\"headers\":{},\"parameters\":{}}},\"cache\":{}}}");
    RxInt32 taskId = new RxInt32(doc, 1);
    WebQueue queue = new WebQueue(taskId);
    queue.hydrate(reader, doc);
    JsonStreamWriter writer = new JsonStreamWriter();
    queue.dump(writer);
    Assert.assertEquals("\"__webqueue\":{\"2\":{\"context\":{\"who\":{\"agent\":\"?\",\"authority\":\"?\"},\"origin\":\"origin\",\"ip\":\"ip\"},\"item\":{\"delete\":{\"uri\":\"/uri\",\"headers\":{},\"parameters\":{}}},\"cache\":{}}}", writer.toString());
    Json.parseJsonObject("{" + writer.toString() + "}");
  }

  @Test
  public void hydrate_answer_put() {
    MockLivingDocument doc = new MockLivingDocument();
    JsonStreamReader reader = new JsonStreamReader("{\"2\":{\"context\":{\"who\":{\"agent\":\"?\",\"authority\":\"?\"},\"origin\":\"origin\",\"ip\":\"ip\"},\"item\":{\"put\":{\"uri\":\"/uri\",\"headers\":{},\"parameters\":{},\"bodyJson\":{\"body\":123}}},\"cache\":{\"1\":{\"invoke\":{\"service\":\"service\",\"method\":\"method\",\"who\":{\"agent\":\"?\",\"authority\":\"?\"},\"parameter\":{\"x\":123,\"y\":42}},\"result\":{\"result\":null,\"failure\":null,\"failure_code\":null}}}}}");
    RxInt32 taskId = new RxInt32(doc, 1);
    WebQueue queue = new WebQueue(taskId);
    queue.hydrate(reader, doc);
    JsonStreamWriter writer = new JsonStreamWriter();
    queue.dump(writer);
    Assert.assertEquals("\"__webqueue\":{\"2\":{\"context\":{\"who\":{\"agent\":\"?\",\"authority\":\"?\"},\"origin\":\"origin\",\"ip\":\"ip\"},\"item\":{\"put\":{\"uri\":\"/uri\",\"headers\":{},\"parameters\":{},\"bodyJson\":{\"body\":123}}},\"cache\":{\"1\":{\"invoke\":{\"service\":\"service\",\"method\":\"method\",\"who\":{\"agent\":\"?\",\"authority\":\"?\"},\"parameter\":{\"x\":123,\"y\":42}},\"result\":{\"result\":null,\"failure\":null,\"failure_code\":null}}}}}", writer.toString());
    Json.parseJsonObject("{" + writer.toString() + "}");
  }

  @Test
  public void hydrate_answer_delete() {
    MockLivingDocument doc = new MockLivingDocument();
    JsonStreamReader reader = new JsonStreamReader("{\"2\":{\"context\":{\"who\":{\"agent\":\"?\",\"authority\":\"?\"},\"origin\":\"origin\",\"ip\":\"ip\"},\"item\":{\"delete\":{\"uri\":\"/uri\",\"headers\":{},\"parameters\":{},\"junk\":[],\"bodyJson\":{\"body\":123}}},\"cache\":{\"1\":{\"invoke\":{\"service\":\"service\",\"method\":\"method\",\"who\":{\"agent\":\"?\",\"authority\":\"?\"},\"parameter\":{\"x\":123,\"y\":42}},\"result\":{\"result\":null,\"failure\":null,\"failure_code\":null}}}}}");
    RxInt32 taskId = new RxInt32(doc, 1);
    WebQueue queue = new WebQueue(taskId);
    queue.hydrate(reader, doc);
    JsonStreamWriter writer = new JsonStreamWriter();
    queue.dump(writer);
    Assert.assertEquals("\"__webqueue\":{\"2\":{\"context\":{\"who\":{\"agent\":\"?\",\"authority\":\"?\"},\"origin\":\"origin\",\"ip\":\"ip\"},\"item\":{\"delete\":{\"uri\":\"/uri\",\"headers\":{},\"parameters\":{}}},\"cache\":{\"1\":{\"invoke\":{\"service\":\"service\",\"method\":\"method\",\"who\":{\"agent\":\"?\",\"authority\":\"?\"},\"parameter\":{\"x\":123,\"y\":42}},\"result\":{\"result\":null,\"failure\":null,\"failure_code\":null}}}}}", writer.toString());
    Json.parseJsonObject("{" + writer.toString() + "}");
  }

  @Test
  public void hydrate_full() {
    MockLivingDocument doc = new MockLivingDocument();
    JsonStreamReader reader = new JsonStreamReader("{\"7\":null,\"2\":{\"context\":{\"who\":{\"agent\":\"?\",\"authority\":\"?\"},\"origin\":\"origin\",\"ip\":\"ip\"},\"item\":{\"put\":{\"uri\":\"/uri\",\"headers\":{},\"parameters\":{},\"bodyJson\":{\"body\":123}}},\"cache\":{\"1\":{\"invoke\":{\"service\":\"service\",\"method\":\"method\",\"who\":{\"agent\":\"?\",\"authority\":\"?\"},\"parameter\":{\"x\":123,\"y\":42}},\"result\":{\"result\":{\"x\":123,\"y\":42},\"failure\":null,\"failure_code\":null}}}}}");
    RxInt32 taskId = new RxInt32(doc, 1);
    WebQueue queue = new WebQueue(taskId);
    queue.hydrate(reader, doc);
    {
      JsonStreamWriter writer = new JsonStreamWriter();
      queue.dump(writer);
      Assert.assertEquals("\"__webqueue\":{\"2\":{\"context\":{\"who\":{\"agent\":\"?\",\"authority\":\"?\"},\"origin\":\"origin\",\"ip\":\"ip\"},\"item\":{\"put\":{\"uri\":\"/uri\",\"headers\":{},\"parameters\":{},\"bodyJson\":{\"body\":123}}},\"cache\":{\"1\":{\"invoke\":{\"service\":\"service\",\"method\":\"method\",\"who\":{\"agent\":\"?\",\"authority\":\"?\"},\"parameter\":{\"x\":123,\"y\":42}},\"result\":{\"result\":{\"x\":123,\"y\":42},\"failure\":null,\"failure_code\":null}}}}}", writer.toString());
      Json.parseJsonObject("{" + writer.toString() + "}");
    }
    queue.hydrate(new JsonStreamReader("null"), doc);
    {
      JsonStreamWriter writer = new JsonStreamWriter();
      queue.dump(writer);
      Assert.assertEquals("\"__webqueue\":{}", writer.toString());
      Json.parseJsonObject("{" + writer.toString() + "}");
    }
  }
}
