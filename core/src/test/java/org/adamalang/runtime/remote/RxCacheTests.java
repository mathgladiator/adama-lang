/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.runtime.remote;

import org.adamalang.common.Callback;
import org.adamalang.runtime.data.Key;
import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.mocks.MockDeliverer;
import org.adamalang.runtime.mocks.MockLivingDocument;
import org.adamalang.runtime.mocks.MockMessage;
import org.adamalang.runtime.mocks.MockRxParent;
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.runtime.natives.NtResult;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class RxCacheTests {
  @Test
  public void core_flow() {
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
    AtomicInteger x = new AtomicInteger(100);
    Supplier<String> func = cache.wrap(() -> {
      NtResult<MockMessage> result1 = cache.answer("service", "method", NtPrincipal.NO_ONE, new MockMessage(x.get(), 42), (str) -> new MockMessage(new JsonStreamReader(str)), service);
      NtResult<MockMessage> result2 = cache.answer("service", "method", NtPrincipal.NO_ONE, new MockMessage(x.get(), 50), (str) -> new MockMessage(new JsonStreamReader(str)), service);
      return "X:" + result1.get() + "|" + result2.get();
    });
    {
      JsonStreamWriter redo = new JsonStreamWriter();
      JsonStreamWriter undo = new JsonStreamWriter();
      cache.__commit("X", redo, undo);
      Assert.assertEquals("", redo.toString());
      Assert.assertEquals("", undo.toString());
    }
    Assert.assertEquals("X:null|null", func.get());
    Assert.assertEquals(2, tasks.size());
    {
      JsonStreamWriter redo = new JsonStreamWriter();
      JsonStreamWriter undo = new JsonStreamWriter();
      cache.__commit("X", redo, undo);
      Assert.assertEquals("\"X\":{\"1\":{\"invoke\":{\"service\":\"service\",\"method\":\"method\",\"who\":{\"agent\":\"?\",\"authority\":\"?\"},\"parameter\":{\"x\":100,\"y\":42}},\"result\":{\"result\":null,\"failure\":null,\"failure_code\":null}},\"2\":{\"invoke\":{\"service\":\"service\",\"method\":\"method\",\"who\":{\"agent\":\"?\",\"authority\":\"?\"},\"parameter\":{\"x\":100,\"y\":50}},\"result\":{\"result\":null,\"failure\":null,\"failure_code\":null}}}", redo.toString());
      Assert.assertEquals("\"X\":{\"1\":null,\"2\":null}", undo.toString());
    }

    Assert.assertEquals("X:null|null", func.get());
    Assert.assertEquals(2, tasks.size());
    {
      JsonStreamWriter redo = new JsonStreamWriter();
      JsonStreamWriter undo = new JsonStreamWriter();
      cache.__commit("X", redo, undo);
      Assert.assertEquals("", redo.toString());
      Assert.assertEquals("", undo.toString());
    }
    Assert.assertEquals(0, deliverer.deliveries.size());
    tasks.remove(0).run();
    Assert.assertEquals(1, deliverer.deliveries.size());
    Assert.assertEquals("X:null|null", func.get());
    Assert.assertEquals(1, tasks.size());
    deliverer.deliverAllTo(cache);
    Assert.assertEquals("X:{\"x\":100,\"y\":42}|null", func.get());
    Assert.assertEquals(1, tasks.size());

    Assert.assertEquals(0, deliverer.deliveries.size());
    tasks.remove(0).run();
    Assert.assertEquals(1, deliverer.deliveries.size());
    Assert.assertEquals("X:{\"x\":100,\"y\":42}|null", func.get());
    Assert.assertEquals(0, tasks.size());
    deliverer.deliverAllTo(cache);
    Assert.assertEquals("X:{\"x\":100,\"y\":42}|{\"x\":100,\"y\":50}", func.get());
    Assert.assertEquals(0, tasks.size());

    {
      JsonStreamWriter redo = new JsonStreamWriter();
      JsonStreamWriter undo = new JsonStreamWriter();
      cache.__commit("X", redo, undo);
      Assert.assertEquals("\"X\":{\"1\":{\"result\":{\"result\":{\"x\":100,\"y\":42},\"failure\":null,\"failure_code\":null}},\"2\":{\"result\":{\"result\":{\"x\":100,\"y\":50},\"failure\":null,\"failure_code\":null}}}", redo.toString());
      Assert.assertEquals("\"X\":{\"1\":{\"result\":{\"result\":null,\"failure\":null,\"failure_code\":null}},\"2\":{\"result\":{\"result\":null,\"failure\":null,\"failure_code\":null}}}", undo.toString());
    }

    x.set(250);

    Assert.assertEquals("X:null|null", func.get());
    Assert.assertEquals(2, tasks.size());
    {
      JsonStreamWriter redo = new JsonStreamWriter();
      JsonStreamWriter undo = new JsonStreamWriter();
      cache.__commit("X", redo, undo);
      Assert.assertEquals("\"X\":{\"3\":{\"invoke\":{\"service\":\"service\",\"method\":\"method\",\"who\":{\"agent\":\"?\",\"authority\":\"?\"},\"parameter\":{\"x\":250,\"y\":42}},\"result\":{\"result\":null,\"failure\":null,\"failure_code\":null}},\"4\":{\"invoke\":{\"service\":\"service\",\"method\":\"method\",\"who\":{\"agent\":\"?\",\"authority\":\"?\"},\"parameter\":{\"x\":250,\"y\":50}},\"result\":{\"result\":null,\"failure\":null,\"failure_code\":null}},\"1\":null,\"2\":null}", redo.toString());
      Assert.assertEquals("\"X\":{\"3\":null,\"4\":null,\"1\":{\"invoke\":{\"service\":\"service\",\"method\":\"method\",\"who\":{\"agent\":\"?\",\"authority\":\"?\"},\"parameter\":{\"x\":100,\"y\":42}},\"result\":{\"result\":{\"x\":100,\"y\":42},\"failure\":null,\"failure_code\":null}},\"2\":{\"invoke\":{\"service\":\"service\",\"method\":\"method\",\"who\":{\"agent\":\"?\",\"authority\":\"?\"},\"parameter\":{\"x\":100,\"y\":50}},\"result\":{\"result\":{\"x\":100,\"y\":50},\"failure\":null,\"failure_code\":null}}}", undo.toString());
    }

    tasks.remove(0).run();
    tasks.remove(0).run();
    Assert.assertEquals(2, deliverer.deliveries.size());
    deliverer.deliverAllTo(cache);

    Assert.assertEquals("X:{\"x\":250,\"y\":42}|{\"x\":250,\"y\":50}", func.get());
    {
      JsonStreamWriter redo = new JsonStreamWriter();
      JsonStreamWriter undo = new JsonStreamWriter();
      cache.__commit("X", redo, undo);
      Assert.assertEquals("\"X\":{\"3\":{\"result\":{\"result\":{\"x\":250,\"y\":42},\"failure\":null,\"failure_code\":null}},\"4\":{\"result\":{\"result\":{\"x\":250,\"y\":50},\"failure\":null,\"failure_code\":null}}}", redo.toString());
      Assert.assertEquals("\"X\":{\"3\":{\"result\":{\"result\":null,\"failure\":null,\"failure_code\":null}},\"4\":{\"result\":{\"result\":null,\"failure\":null,\"failure_code\":null}}}", undo.toString());
    }
    // coverage bits
    cache.__kill();
    Assert.assertFalse(cache.deliver(1000, null));
    {
      JsonStreamWriter dump = new JsonStreamWriter();
      cache.__dump(dump);
      Assert.assertEquals("{\"3\":{\"invoke\":{\"service\":\"service\",\"method\":\"method\",\"who\":{\"agent\":\"?\",\"authority\":\"?\"},\"parameter\":{\"x\":250,\"y\":42}},\"result\":{\"result\":{\"x\":250,\"y\":42},\"failure\":null,\"failure_code\":null}},\"4\":{\"invoke\":{\"service\":\"service\",\"method\":\"method\",\"who\":{\"agent\":\"?\",\"authority\":\"?\"},\"parameter\":{\"x\":250,\"y\":50}},\"result\":{\"result\":{\"x\":250,\"y\":50},\"failure\":null,\"failure_code\":null}}}", dump.toString());
      RxCache clone = new RxCache(doc, parent);
      clone.__insert(new JsonStreamReader(dump.toString()));
      JsonStreamWriter dump2 = new JsonStreamWriter();
      clone.__dump(dump2);
      Assert.assertEquals("{\"3\":{\"invoke\":{\"service\":\"service\",\"method\":\"method\",\"who\":{\"agent\":\"?\",\"authority\":\"?\"},\"parameter\":{\"x\":250,\"y\":42}},\"result\":{\"result\":{\"x\":250,\"y\":42},\"failure\":null,\"failure_code\":null}},\"4\":{\"invoke\":{\"service\":\"service\",\"method\":\"method\",\"who\":{\"agent\":\"?\",\"authority\":\"?\"},\"parameter\":{\"x\":250,\"y\":50}},\"result\":{\"result\":{\"x\":250,\"y\":50},\"failure\":null,\"failure_code\":null}}}", dump2.toString());
      RxCache clone2 = new RxCache(doc, parent);
      clone2.__patch(new JsonStreamReader(dump.toString()));
      JsonStreamWriter redo = new JsonStreamWriter();
      JsonStreamWriter undo = new JsonStreamWriter();
      clone2.__commit("X", redo, undo);
      Assert.assertEquals("\"X\":{\"3\":{\"invoke\":{\"service\":\"service\",\"method\":\"method\",\"who\":{\"agent\":\"?\",\"authority\":\"?\"},\"parameter\":{\"x\":250,\"y\":42}},\"result\":{\"result\":{\"x\":250,\"y\":42},\"failure\":null,\"failure_code\":null}},\"4\":{\"invoke\":{\"service\":\"service\",\"method\":\"method\",\"who\":{\"agent\":\"?\",\"authority\":\"?\"},\"parameter\":{\"x\":250,\"y\":50}},\"result\":{\"result\":{\"x\":250,\"y\":50},\"failure\":null,\"failure_code\":null}}}", redo.toString());
      Assert.assertEquals("\"X\":{\"3\":null,\"4\":null}", undo.toString());
      clone2.__patch(new JsonStreamReader(undo.toString().substring(4)));
      JsonStreamWriter redo2 = new JsonStreamWriter();
      JsonStreamWriter undo2 = new JsonStreamWriter();
      clone2.__commit("X", redo2, undo2);
      Assert.assertEquals("\"X\":{\"3\":null,\"4\":null}", redo2.toString());
      Assert.assertEquals("\"X\":{\"3\":{\"invoke\":{\"service\":\"service\",\"method\":\"method\",\"who\":{\"agent\":\"?\",\"authority\":\"?\"},\"parameter\":{\"x\":250,\"y\":42}},\"result\":{\"result\":{\"x\":250,\"y\":42},\"failure\":null,\"failure_code\":null}},\"4\":{\"invoke\":{\"service\":\"service\",\"method\":\"method\",\"who\":{\"agent\":\"?\",\"authority\":\"?\"},\"parameter\":{\"x\":250,\"y\":50}},\"result\":{\"result\":{\"x\":250,\"y\":50},\"failure\":null,\"failure_code\":null}}}", undo2.toString());
    }
    {
      JsonStreamWriter dump = new JsonStreamWriter();
      cache.__dump(dump);
      Assert.assertEquals("{\"3\":{\"invoke\":{\"service\":\"service\",\"method\":\"method\",\"who\":{\"agent\":\"?\",\"authority\":\"?\"},\"parameter\":{\"x\":250,\"y\":42}},\"result\":{\"result\":{\"x\":250,\"y\":42},\"failure\":null,\"failure_code\":null}},\"4\":{\"invoke\":{\"service\":\"service\",\"method\":\"method\",\"who\":{\"agent\":\"?\",\"authority\":\"?\"},\"parameter\":{\"x\":250,\"y\":50}},\"result\":{\"result\":{\"x\":250,\"y\":50},\"failure\":null,\"failure_code\":null}}}", dump.toString());
      RxCache clone = new RxCache(doc, parent);
      clone.__insert(new JsonStreamReader(dump.toString()));
      JsonStreamWriter dump2 = new JsonStreamWriter();
      clone.__dump(dump2);
      Assert.assertEquals("{\"3\":{\"invoke\":{\"service\":\"service\",\"method\":\"method\",\"who\":{\"agent\":\"?\",\"authority\":\"?\"},\"parameter\":{\"x\":250,\"y\":42}},\"result\":{\"result\":{\"x\":250,\"y\":42},\"failure\":null,\"failure_code\":null}},\"4\":{\"invoke\":{\"service\":\"service\",\"method\":\"method\",\"who\":{\"agent\":\"?\",\"authority\":\"?\"},\"parameter\":{\"x\":250,\"y\":50}},\"result\":{\"result\":{\"x\":250,\"y\":50},\"failure\":null,\"failure_code\":null}}}", dump2.toString());
      RxCache clone2 = new RxCache(doc, parent);
      clone2.__patch(new JsonStreamReader(dump.toString()));
      clone2.__revert();
      JsonStreamWriter redo = new JsonStreamWriter();
      JsonStreamWriter undo = new JsonStreamWriter();
      clone2.__commit("X", redo, undo);
      Assert.assertEquals("", redo.toString());
      Assert.assertEquals("", undo.toString());
    }
    {
      cache.clear();
      JsonStreamWriter redo = new JsonStreamWriter();
      JsonStreamWriter undo = new JsonStreamWriter();
      cache.__commit("X", redo, undo);
      Assert.assertEquals("\"X\":{\"3\":null,\"4\":null}", redo.toString());
      Assert.assertEquals("\"X\":{\"3\":{\"invoke\":{\"service\":\"service\",\"method\":\"method\",\"who\":{\"agent\":\"?\",\"authority\":\"?\"},\"parameter\":{\"x\":250,\"y\":42}},\"result\":{\"result\":{\"x\":250,\"y\":42},\"failure\":null,\"failure_code\":null}},\"4\":{\"invoke\":{\"service\":\"service\",\"method\":\"method\",\"who\":{\"agent\":\"?\",\"authority\":\"?\"},\"parameter\":{\"x\":250,\"y\":50}},\"result\":{\"result\":{\"x\":250,\"y\":50},\"failure\":null,\"failure_code\":null}}}", undo.toString());
    }

    Assert.assertEquals("X:null|null", func.get());
    Assert.assertEquals(2, tasks.size());
    {
      JsonStreamWriter redo = new JsonStreamWriter();
      JsonStreamWriter undo = new JsonStreamWriter();
      cache.__commit("X", redo, undo);
      Assert.assertEquals("\"X\":{\"5\":{\"invoke\":{\"service\":\"service\",\"method\":\"method\",\"who\":{\"agent\":\"?\",\"authority\":\"?\"},\"parameter\":{\"x\":250,\"y\":42}},\"result\":{\"result\":null,\"failure\":null,\"failure_code\":null}},\"6\":{\"invoke\":{\"service\":\"service\",\"method\":\"method\",\"who\":{\"agent\":\"?\",\"authority\":\"?\"},\"parameter\":{\"x\":250,\"y\":50}},\"result\":{\"result\":null,\"failure\":null,\"failure_code\":null}}}", redo.toString());
      Assert.assertEquals("\"X\":{\"5\":null,\"6\":null}", undo.toString());
    }
    tasks.remove(0).run();
    tasks.remove(0).run();
    deliverer.deliverAllTo(cache);
    cache.__revert();
    {
      JsonStreamWriter redo = new JsonStreamWriter();
      JsonStreamWriter undo = new JsonStreamWriter();
      cache.__commit("X", redo, undo);
      Assert.assertEquals("", redo.toString());
      Assert.assertEquals("", undo.toString());
    }
    {
      cache.__patch(new JsonStreamReader("{\"1\":{\"invoke\":{\"service\":\"service\",\"method\":\"method\",\"who\":{\"agent\":\"?\",\"authority\":\"?\"},\"parameter\":{\"x\":250,\"y\":42}},\"result\":{\"result\":null,\"failure\":null,\"failure_code\":null}},\"6\":{\"invoke\":{\"service\":\"service\",\"method\":\"method\",\"who\":{\"agent\":\"?\",\"authority\":\"?\"},\"parameter\":{\"x\":250,\"y\":50}},\"result\":{\"result\":null,\"failure\":null,\"failure_code\":null}}}"));
      cache.__patch(new JsonStreamReader("{\"1\":{\"invoke\":{\"service\":\"service\",\"method\":\"method\",\"who\":{\"agent\":\"?\",\"authority\":\"?\"},\"parameter\":{\"x\":250,\"y\":42}},\"result\":{\"result\":null,\"failure\":null,\"failure_code\":null}},\"6\":{\"invoke\":{\"service\":\"service\",\"method\":\"method\",\"who\":{\"agent\":\"?\",\"authority\":\"?\"},\"parameter\":{\"x\":250,\"y\":50}},\"result\":{\"result\":null,\"failure\":null,\"failure_code\":null}}}"));
    }

  }
}
