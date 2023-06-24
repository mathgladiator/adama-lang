/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
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
  public void coverage_delivery() {
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
  public void hydrate_setup() {
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
  public void hydrate_answer() {
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
