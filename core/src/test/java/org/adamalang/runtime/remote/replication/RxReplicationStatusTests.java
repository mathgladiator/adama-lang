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
package org.adamalang.runtime.remote.replication;

import org.adamalang.common.SimpleExecutor;
import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.mocks.MockRxParent;
import org.adamalang.runtime.natives.NtToDynamic;
import org.adamalang.runtime.reactives.RxInt64;
import org.adamalang.runtime.reactives.RxLazy;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicReference;

public class RxReplicationStatusTests {
  @Test
  public void trivial() {
    RxReplicationStatus status = new RxReplicationStatus(null, new RxInt64(null, 10000), "service", "method");
    AtomicReference<NtToDynamic> value = new AtomicReference<>(MockReplicationService.SIMPLE_KEY_OBJECT("key2use"));
    RxLazy<NtToDynamic> lazy = new RxLazy<>(null, () -> value.get(), null);
    lazy.__subscribe(status);
    status.__revert();
    status.__patch(new JsonStreamReader("{}"));
    Assert.assertTrue(status.__raiseInvalid());
    status.__insert(new JsonStreamReader("[1,2,3]"));
    status.__insert(new JsonStreamReader("{\"state\":-12}"));
    Assert.assertEquals(100, status.code());
  }

  @Test
  public void instant_success() {
    MockCallerForReplication caller = new MockCallerForReplication();
    RxReplicationStatus status = new RxReplicationStatus(new MockRxParent(), new RxInt64(null, 10000), "service", "method");
    AtomicReference<NtToDynamic> value = new AtomicReference<>(MockReplicationService.SIMPLE_KEY_OBJECT("key2use"));
    RxLazy<NtToDynamic> lazy = new RxLazy<>(null, () -> value.get(), null);
    lazy.__subscribe(status);
    status.linkToValue(lazy);
    {
      JsonStreamWriter writer = new JsonStreamWriter();
      status.__dump(writer);
      Assert.assertEquals("{\"state\":100}", writer.toString());
      JsonStreamWriter forward = new JsonStreamWriter();
      JsonStreamWriter reverse = new JsonStreamWriter();
      status.__commit("diff", forward, reverse);
      Assert.assertEquals("", forward.toString());
      Assert.assertEquals("", reverse.toString());
    }
    status.progress(caller);
    {
      JsonStreamWriter writer = new JsonStreamWriter();
      status.__dump(writer);
      Assert.assertEquals("{\"state\":110,\"key\":\"key2use\",\"time\":\"10000\"}", writer.toString());
      JsonStreamWriter forward = new JsonStreamWriter();
      JsonStreamWriter reverse = new JsonStreamWriter();
      status.__commit("diff", forward, reverse);
      Assert.assertEquals("\"diff\":{\"state\":110,\"key\":\"key2use\",\"time\":\"10000\"}", forward.toString());
      Assert.assertEquals("", reverse.toString());
    }
    status.commit(SimpleExecutor.NOW);
    {
      JsonStreamWriter writer = new JsonStreamWriter();
      status.__dump(writer);
      Assert.assertEquals("{\"state\":100,\"hash\":\"z1Z7GvHQuBJPpoce5G15xg==\",\"key\":\"key2use\",\"time\":\"10000\"}", writer.toString());
    }
    value.set(MockReplicationService.SIMPLE_KEY_OBJECT("key2useAfterDelete"));
    Assert.assertTrue( lazy.__raiseInvalid() );
    status.progress(caller);
    {
      JsonStreamWriter writer = new JsonStreamWriter();
      status.__dump(writer);
      Assert.assertEquals("{\"state\":310,\"hash\":\"z1Z7GvHQuBJPpoce5G15xg==\",\"key\":\"key2use\",\"time\":\"10000\"}", writer.toString());
    }
    status.commit(SimpleExecutor.NOW);
    {
      JsonStreamWriter writer = new JsonStreamWriter();
      status.__dump(writer);
      Assert.assertEquals("{\"state\":100}", writer.toString());
    }
    status.progress(caller);
    {
      JsonStreamWriter writer = new JsonStreamWriter();
      status.__dump(writer);
      Assert.assertEquals("{\"state\":110,\"key\":\"key2useAfterDelete\",\"time\":\"10000\"}", writer.toString());
    }
    status.commit(SimpleExecutor.NOW);
    {
      JsonStreamWriter writer = new JsonStreamWriter();
      status.__dump(writer);
      Assert.assertEquals("{\"state\":100,\"hash\":\"w7XDgUzLAwUDxOXtiblnbg==\",\"key\":\"key2useAfterDelete\",\"time\":\"10000\"}", writer.toString());
    }
  }

  @Test
  public void instant_failure_not_found() {
    MockCallerForReplication caller = new MockCallerForReplication();
    RxReplicationStatus status = new RxReplicationStatus(null, new RxInt64(null, 10000), "none", "method");
    RxLazy<NtToDynamic> lazy = new RxLazy<NtToDynamic>(null, () -> MockReplicationService.SIMPLE_KEY_OBJECT("key2use"), null);
    status.linkToValue(lazy);
    {
      JsonStreamWriter writer = new JsonStreamWriter();
      status.__dump(writer);
      Assert.assertEquals("{\"state\":100}", writer.toString());
    }
    status.progress(caller);
    {
      JsonStreamWriter writer = new JsonStreamWriter();
      status.__dump(writer);
      Assert.assertEquals("{\"state\":50}", writer.toString());
    }
    status.commit(SimpleExecutor.NOW);
    {
      JsonStreamWriter writer = new JsonStreamWriter();
      status.__dump(writer);
      Assert.assertEquals("{\"state\":50}", writer.toString());
    }
  }

  @Test
  public void instant_failure_bad_method() {
    MockCallerForReplication caller = new MockCallerForReplication();
    RxReplicationStatus status = new RxReplicationStatus(null, new RxInt64(null, 10000), "service", "nope");
    RxLazy<NtToDynamic> lazy = new RxLazy<NtToDynamic>(null, () -> MockReplicationService.SIMPLE_KEY_OBJECT("key2use"), null);
    status.linkToValue(lazy);
    {
      JsonStreamWriter writer = new JsonStreamWriter();
      status.__dump(writer);
      Assert.assertEquals("{\"state\":100}", writer.toString());
    }
    status.progress(caller);
    {
      JsonStreamWriter writer = new JsonStreamWriter();
      status.__dump(writer);
      Assert.assertEquals("{\"state\":50}", writer.toString());
    }
    status.commit(SimpleExecutor.NOW);
    {
      JsonStreamWriter writer = new JsonStreamWriter();
      status.__dump(writer);
      Assert.assertEquals("{\"state\":50}", writer.toString());
    }
  }

  @Test
  public void sequence_success_put() {
    MockCallerForReplication caller = new MockCallerForReplication();
    RxReplicationStatus status = new RxReplicationStatus(new MockRxParent(), new RxInt64(null, 10000), "service", "method");
    AtomicReference<NtToDynamic> value = new AtomicReference<>(MockReplicationService.SIMPLE_KEY_OBJECT("key2use"));
    RxLazy<NtToDynamic> lazy = new RxLazy<>(null, () -> value.get(), null);
    lazy.__subscribe(status);
    status.linkToValue(lazy);
    {
      JsonStreamWriter writer = new JsonStreamWriter();
      status.__dump(writer);
      Assert.assertEquals("{\"state\":100}", writer.toString());
      JsonStreamWriter forward = new JsonStreamWriter();
      JsonStreamWriter reverse = new JsonStreamWriter();
      status.__commit("diff", forward, reverse);
      Assert.assertEquals("", forward.toString());
      Assert.assertEquals("", reverse.toString());
    }
    status.progress(caller);
    {
      JsonStreamWriter writer = new JsonStreamWriter();
      status.__dump(writer);
      Assert.assertEquals("{\"state\":110,\"key\":\"key2use\",\"time\":\"10000\"}", writer.toString());
      JsonStreamWriter forward = new JsonStreamWriter();
      JsonStreamWriter reverse = new JsonStreamWriter();
      status.__commit("diff", forward, reverse);
      Assert.assertEquals("\"diff\":{\"state\":110,\"key\":\"key2use\",\"time\":\"10000\"}", forward.toString());
      Assert.assertEquals("", reverse.toString());
    }
    SequencedTestExecutor executor = new SequencedTestExecutor();
    status.commit(executor);
    {
      JsonStreamWriter writer = new JsonStreamWriter();
      status.__dump(writer);
      Assert.assertEquals("{\"state\":110,\"key\":\"key2use\",\"time\":\"10000\"}", writer.toString());
      JsonStreamWriter forward = new JsonStreamWriter();
      JsonStreamWriter reverse = new JsonStreamWriter();
      status.__commit("diff", forward, reverse);
      Assert.assertEquals("", forward.toString());
      Assert.assertEquals("", reverse.toString());
    }
    executor.next();
    {
      JsonStreamWriter writer = new JsonStreamWriter();
      status.__dump(writer);
      Assert.assertEquals("{\"state\":120,\"key\":\"key2use\",\"time\":\"10000\"}", writer.toString());
      JsonStreamWriter forward = new JsonStreamWriter();
      JsonStreamWriter reverse = new JsonStreamWriter();
      status.__commit("diff", forward, reverse);
      Assert.assertEquals("\"diff\":{\"state\":120,\"key\":\"key2use\",\"time\":\"10000\"}", forward.toString());
      Assert.assertEquals("", reverse.toString());
    }
    executor.next();
    {
      JsonStreamWriter writer = new JsonStreamWriter();
      status.__dump(writer);
      Assert.assertEquals("{\"state\":100,\"hash\":\"z1Z7GvHQuBJPpoce5G15xg==\",\"key\":\"key2use\",\"time\":\"10000\"}", writer.toString());
      JsonStreamWriter forward = new JsonStreamWriter();
      JsonStreamWriter reverse = new JsonStreamWriter();
      status.__commit("diff", forward, reverse);
      Assert.assertEquals("\"diff\":{\"state\":100,\"hash\":\"z1Z7GvHQuBJPpoce5G15xg==\",\"key\":\"key2use\",\"time\":\"10000\"}", forward.toString());
      Assert.assertEquals("", reverse.toString());
    }
    executor.assertEmpty();
  }

  private void assertLoadAs(String json, int code) {
    RxReplicationStatus status = new RxReplicationStatus(new MockRxParent(), new RxInt64(null, 10000), "service", "method");
    status.__insert(new JsonStreamReader(json));
    Assert.assertEquals(code, status.code());
  }

  @Test
  public void sequence_1_failure_success() {
    MockCallerForReplication caller = new MockCallerForReplication();
    RxReplicationStatus status = new RxReplicationStatus(new MockRxParent(), new RxInt64(null, 10000), "service", "method");
    AtomicReference<NtToDynamic> value = new AtomicReference<>(MockReplicationService.SIMPLE_KEY_OBJECT("key2use"));
    RxLazy<NtToDynamic> lazy = new RxLazy<>(null, () -> value.get(), null);
    lazy.__subscribe(status);
    status.linkToValue(lazy);
    {
      JsonStreamWriter writer = new JsonStreamWriter();
      status.__dump(writer);
      Assert.assertEquals("{\"state\":100}", writer.toString());
      assertLoadAs(writer.toString(), 100);
      JsonStreamWriter forward = new JsonStreamWriter();
      JsonStreamWriter reverse = new JsonStreamWriter();
      status.__commit("diff", forward, reverse);
      Assert.assertEquals("", forward.toString());
      Assert.assertEquals("", reverse.toString());
      Assert.assertEquals("Nothing;null;null;0", status.toString());
    }
    status.progress(caller);
    {
      JsonStreamWriter writer = new JsonStreamWriter();
      status.__dump(writer);
      Assert.assertEquals("{\"state\":110,\"key\":\"key2use\",\"time\":\"10000\"}", writer.toString());
      assertLoadAs(writer.toString(), 110);
      JsonStreamWriter forward = new JsonStreamWriter();
      JsonStreamWriter reverse = new JsonStreamWriter();
      status.__commit("diff", forward, reverse);
      Assert.assertEquals("\"diff\":{\"state\":110,\"key\":\"key2use\",\"time\":\"10000\"}", forward.toString());
      Assert.assertEquals("", reverse.toString());
    }
    SequencedTestExecutor executor = new SequencedTestExecutor();
    status.commit(executor);
    {
      JsonStreamWriter writer = new JsonStreamWriter();
      status.__dump(writer);
      Assert.assertEquals("{\"state\":110,\"key\":\"key2use\",\"time\":\"10000\"}", writer.toString());
      assertLoadAs(writer.toString(), 110);
      JsonStreamWriter forward = new JsonStreamWriter();
      JsonStreamWriter reverse = new JsonStreamWriter();
      status.__commit("diff", forward, reverse);
      Assert.assertEquals("", forward.toString());
      Assert.assertEquals("", reverse.toString());
    }
    caller.service.raiseFailNextComplete();
    executor.next();
    {
      JsonStreamWriter writer = new JsonStreamWriter();
      status.__dump(writer);
      Assert.assertEquals("{\"state\":120,\"key\":\"key2use\",\"time\":\"10000\"}", writer.toString());
      assertLoadAs(writer.toString(), 110);
      JsonStreamWriter forward = new JsonStreamWriter();
      JsonStreamWriter reverse = new JsonStreamWriter();
      status.__commit("diff", forward, reverse);
      Assert.assertEquals("\"diff\":{\"state\":120,\"key\":\"key2use\",\"time\":\"10000\"}", forward.toString());
      Assert.assertEquals("", reverse.toString());
    }
    executor.next();
    {
      JsonStreamWriter writer = new JsonStreamWriter();
      status.__dump(writer);
      Assert.assertEquals("{\"state\":150,\"key\":\"key2use\",\"time\":\"10000\",\"failures\":1}", writer.toString());
      assertLoadAs(writer.toString(), 110);
      JsonStreamWriter forward = new JsonStreamWriter();
      JsonStreamWriter reverse = new JsonStreamWriter();
      status.__commit("diff", forward, reverse);
      Assert.assertEquals("\"diff\":{\"state\":150,\"key\":\"key2use\",\"time\":\"10000\",\"failures\":1}", forward.toString());
      Assert.assertEquals("", reverse.toString());
    }
    executor.next();
    {
      JsonStreamWriter writer = new JsonStreamWriter();
      status.__dump(writer);
      Assert.assertEquals("{\"state\":110,\"key\":\"key2use\",\"time\":\"10000\",\"failures\":1}", writer.toString());
      assertLoadAs(writer.toString(), 110);
      JsonStreamWriter forward = new JsonStreamWriter();
      JsonStreamWriter reverse = new JsonStreamWriter();
      status.__commit("diff", forward, reverse);
      Assert.assertEquals("\"diff\":{\"state\":110,\"key\":\"key2use\",\"time\":\"10000\",\"failures\":1}", forward.toString());
      Assert.assertEquals("", reverse.toString());
    }
    executor.next();
    {
      JsonStreamWriter writer = new JsonStreamWriter();
      status.__dump(writer);
      Assert.assertEquals("{\"state\":120,\"key\":\"key2use\",\"time\":\"10000\",\"failures\":1}", writer.toString());
      assertLoadAs(writer.toString(), 110);
      JsonStreamWriter forward = new JsonStreamWriter();
      JsonStreamWriter reverse = new JsonStreamWriter();
      status.__commit("diff", forward, reverse);
      Assert.assertEquals("\"diff\":{\"state\":120,\"key\":\"key2use\",\"time\":\"10000\",\"failures\":1}", forward.toString());
      Assert.assertEquals("", reverse.toString());
    }
    executor.next();
    {
      JsonStreamWriter writer = new JsonStreamWriter();
      status.__dump(writer);
      Assert.assertEquals("{\"state\":100,\"hash\":\"z1Z7GvHQuBJPpoce5G15xg==\",\"key\":\"key2use\",\"time\":\"10000\"}", writer.toString());
      assertLoadAs(writer.toString(), 100);
      JsonStreamWriter forward = new JsonStreamWriter();
      JsonStreamWriter reverse = new JsonStreamWriter();
      status.__commit("diff", forward, reverse);
      Assert.assertEquals("\"diff\":{\"state\":100,\"hash\":\"z1Z7GvHQuBJPpoce5G15xg==\",\"key\":\"key2use\",\"time\":\"10000\"}", forward.toString());
      Assert.assertEquals("", reverse.toString());
    }
    executor.assertEmpty();
    status.progress(caller);
    status.commit(executor);
    executor.next();
    {
      JsonStreamWriter writer = new JsonStreamWriter();
      status.__dump(writer);
      Assert.assertEquals("{\"state\":100,\"hash\":\"z1Z7GvHQuBJPpoce5G15xg==\",\"key\":\"key2use\",\"time\":\"10000\"}", writer.toString());
      JsonStreamWriter forward = new JsonStreamWriter();
      JsonStreamWriter reverse = new JsonStreamWriter();
      status.__commit("diff", forward, reverse);
      Assert.assertEquals("", forward.toString());
      Assert.assertEquals("", reverse.toString());
    }
    executor.assertEmpty();
    value.set(MockReplicationService.SIMPLE_KEY_OBJECT("key2useAfterDelete"));
    lazy.__raiseInvalid();
    caller.service.raiseFailNextDelete();
    status.progress(caller);
    {
      JsonStreamWriter writer = new JsonStreamWriter();
      status.__dump(writer);
      Assert.assertEquals("{\"state\":310,\"hash\":\"z1Z7GvHQuBJPpoce5G15xg==\",\"key\":\"key2use\",\"time\":\"10000\"}", writer.toString());
      assertLoadAs(writer.toString(), 310);
      JsonStreamWriter forward = new JsonStreamWriter();
      JsonStreamWriter reverse = new JsonStreamWriter();
      status.__commit("diff", forward, reverse);
      Assert.assertEquals("\"diff\":{\"state\":310,\"hash\":\"z1Z7GvHQuBJPpoce5G15xg==\",\"key\":\"key2use\",\"time\":\"10000\"}", forward.toString());
      Assert.assertEquals("", reverse.toString());
    }
    status.commit(executor);
    executor.next();
    {
      JsonStreamWriter writer = new JsonStreamWriter();
      status.__dump(writer);
      Assert.assertEquals("{\"state\":320,\"hash\":\"z1Z7GvHQuBJPpoce5G15xg==\",\"key\":\"key2use\",\"time\":\"10000\"}", writer.toString());
      assertLoadAs(writer.toString(), 310);
      JsonStreamWriter forward = new JsonStreamWriter();
      JsonStreamWriter reverse = new JsonStreamWriter();
      status.__commit("diff", forward, reverse);
      Assert.assertEquals("\"diff\":{\"state\":320,\"hash\":\"z1Z7GvHQuBJPpoce5G15xg==\",\"key\":\"key2use\",\"time\":\"10000\"}", forward.toString());
      Assert.assertEquals("", reverse.toString());
    }
    executor.next();
    {
      JsonStreamWriter writer = new JsonStreamWriter();
      status.__dump(writer);
      Assert.assertEquals("{\"state\":350,\"hash\":\"z1Z7GvHQuBJPpoce5G15xg==\",\"key\":\"key2use\",\"time\":\"10000\",\"failures\":1}", writer.toString());
      assertLoadAs(writer.toString(), 310);
      JsonStreamWriter forward = new JsonStreamWriter();
      JsonStreamWriter reverse = new JsonStreamWriter();
      status.__commit("diff", forward, reverse);
      Assert.assertEquals("\"diff\":{\"state\":350,\"hash\":\"z1Z7GvHQuBJPpoce5G15xg==\",\"key\":\"key2use\",\"time\":\"10000\",\"failures\":1}", forward.toString());
      Assert.assertEquals("", reverse.toString());
    }
    executor.next();
    {
      JsonStreamWriter writer = new JsonStreamWriter();
      status.__dump(writer);
      Assert.assertEquals("{\"state\":310,\"hash\":\"z1Z7GvHQuBJPpoce5G15xg==\",\"key\":\"key2use\",\"time\":\"10000\",\"failures\":1}", writer.toString());
      assertLoadAs(writer.toString(), 310);
      JsonStreamWriter forward = new JsonStreamWriter();
      JsonStreamWriter reverse = new JsonStreamWriter();
      status.__commit("diff", forward, reverse);
      Assert.assertEquals("\"diff\":{\"state\":310,\"hash\":\"z1Z7GvHQuBJPpoce5G15xg==\",\"key\":\"key2use\",\"time\":\"10000\",\"failures\":1}", forward.toString());
      Assert.assertEquals("", reverse.toString());
    }
    executor.next();
    {
      JsonStreamWriter writer = new JsonStreamWriter();
      status.__dump(writer);
      Assert.assertEquals("{\"state\":320,\"hash\":\"z1Z7GvHQuBJPpoce5G15xg==\",\"key\":\"key2use\",\"time\":\"10000\",\"failures\":1}", writer.toString());
      JsonStreamWriter forward = new JsonStreamWriter();
      JsonStreamWriter reverse = new JsonStreamWriter();
      status.__commit("diff", forward, reverse);
      Assert.assertEquals("\"diff\":{\"state\":320,\"hash\":\"z1Z7GvHQuBJPpoce5G15xg==\",\"key\":\"key2use\",\"time\":\"10000\",\"failures\":1}", forward.toString());
      Assert.assertEquals("", reverse.toString());
    }
    executor.next();
    {
      JsonStreamWriter writer = new JsonStreamWriter();
      status.__dump(writer);
      Assert.assertEquals("{\"state\":100}", writer.toString());
      JsonStreamWriter forward = new JsonStreamWriter();
      JsonStreamWriter reverse = new JsonStreamWriter();
      status.__commit("diff", forward, reverse);
      Assert.assertEquals("\"diff\":{\"state\":100}", forward.toString());
      Assert.assertEquals("", reverse.toString());
    }
    status.progress(caller);
    {
      JsonStreamWriter writer = new JsonStreamWriter();
      status.__dump(writer);
      Assert.assertEquals("{\"state\":110,\"key\":\"key2useAfterDelete\",\"time\":\"10000\"}", writer.toString());
      JsonStreamWriter forward = new JsonStreamWriter();
      JsonStreamWriter reverse = new JsonStreamWriter();
      status.__commit("diff", forward, reverse);
      Assert.assertEquals("\"diff\":{\"state\":110,\"key\":\"key2useAfterDelete\",\"time\":\"10000\"}", forward.toString());
      Assert.assertEquals("", reverse.toString());
    }
    status.commit(executor);
    {
      JsonStreamWriter writer = new JsonStreamWriter();
      status.__dump(writer);
      Assert.assertEquals("{\"state\":110,\"key\":\"key2useAfterDelete\",\"time\":\"10000\"}", writer.toString());
      JsonStreamWriter forward = new JsonStreamWriter();
      JsonStreamWriter reverse = new JsonStreamWriter();
      status.__commit("diff", forward, reverse);
      Assert.assertEquals("", forward.toString());
      Assert.assertEquals("", reverse.toString());
    }
    executor.next();
    {
      JsonStreamWriter writer = new JsonStreamWriter();
      status.__dump(writer);
      Assert.assertEquals("{\"state\":120,\"key\":\"key2useAfterDelete\",\"time\":\"10000\"}", writer.toString());
      JsonStreamWriter forward = new JsonStreamWriter();
      JsonStreamWriter reverse = new JsonStreamWriter();
      status.__commit("diff", forward, reverse);
      Assert.assertEquals("\"diff\":{\"state\":120,\"key\":\"key2useAfterDelete\",\"time\":\"10000\"}", forward.toString());
      Assert.assertEquals("", reverse.toString());
    }
    executor.next();
    {
      JsonStreamWriter writer = new JsonStreamWriter();
      status.__dump(writer);
      Assert.assertEquals("{\"state\":100,\"hash\":\"w7XDgUzLAwUDxOXtiblnbg==\",\"key\":\"key2useAfterDelete\",\"time\":\"10000\"}", writer.toString());
      JsonStreamWriter forward = new JsonStreamWriter();
      JsonStreamWriter reverse = new JsonStreamWriter();
      status.__commit("diff", forward, reverse);
      Assert.assertEquals("\"diff\":{\"state\":100,\"hash\":\"w7XDgUzLAwUDxOXtiblnbg==\",\"key\":\"key2useAfterDelete\",\"time\":\"10000\"}", forward.toString());
      Assert.assertEquals("", reverse.toString());
    }
    executor.assertEmpty();
  }

  @Test
  public void hydrate_put() {
    MockCallerForReplication caller = new MockCallerForReplication();
    RxInt64 documentTime = new RxInt64(null, 10000);
    RxReplicationStatus status = new RxReplicationStatus(new MockRxParent(), documentTime, "service", "method");
    AtomicReference<NtToDynamic> value = new AtomicReference<>(MockReplicationService.SIMPLE_KEY_OBJECT("key2use"));
    RxLazy<NtToDynamic> lazy = new RxLazy<>(null, () -> value.get(), null);
    lazy.__subscribe(status);
    status.linkToValue(lazy);
    status.__insert(new JsonStreamReader("{\"state\":110,\"key\":\"key2use\",\"time\":\"10000\",\"failures\":1}"));
    {
      JsonStreamWriter writer = new JsonStreamWriter();
      status.__dump(writer);
      Assert.assertEquals("{\"state\":110,\"key\":\"key2use\",\"time\":\"10000\",\"failures\":1}", writer.toString());
      assertLoadAs(writer.toString(), 110);
      JsonStreamWriter forward = new JsonStreamWriter();
      JsonStreamWriter reverse = new JsonStreamWriter();
      status.__commit("diff", forward, reverse);
      Assert.assertEquals("", forward.toString());
      Assert.assertEquals("", reverse.toString());
      Assert.assertEquals("PutRequested;key2use;null;10000", status.toString());
    }
    status.progress(caller);
    SequencedTestExecutor executor = new SequencedTestExecutor();
    status.commit(executor);
    executor.next();
    {
      JsonStreamWriter writer = new JsonStreamWriter();
      status.__dump(writer);
      Assert.assertEquals("{\"state\":110,\"key\":\"key2use\",\"time\":\"10000\",\"failures\":1}", writer.toString());
      assertLoadAs(writer.toString(), 110);
      JsonStreamWriter forward = new JsonStreamWriter();
      JsonStreamWriter reverse = new JsonStreamWriter();
      status.__commit("diff", forward, reverse);
      Assert.assertEquals("", forward.toString());
      Assert.assertEquals("", reverse.toString());
      Assert.assertEquals("PutRequested;key2use;null;10000", status.toString());
    }
    executor.assertEmpty();
    documentTime.set(1000000);
    status.progress(caller);
    status.commit(executor);
    executor.next();
    {
      JsonStreamWriter writer = new JsonStreamWriter();
      status.__dump(writer);
      Assert.assertEquals("{\"state\":120,\"key\":\"key2use\",\"time\":\"10000\",\"failures\":1}", writer.toString());
      assertLoadAs(writer.toString(), 110);
      JsonStreamWriter forward = new JsonStreamWriter();
      JsonStreamWriter reverse = new JsonStreamWriter();
      status.__commit("diff", forward, reverse);
      Assert.assertEquals("\"diff\":{\"state\":120,\"key\":\"key2use\",\"time\":\"10000\",\"failures\":1}", forward.toString());
      Assert.assertEquals("", reverse.toString());
      Assert.assertEquals("PutInflight;key2use;null;10000", status.toString());
    }
    executor.next();
    {
      JsonStreamWriter writer = new JsonStreamWriter();
      status.__dump(writer);
      Assert.assertEquals("{\"state\":100,\"hash\":\"z1Z7GvHQuBJPpoce5G15xg==\",\"key\":\"key2use\",\"time\":\"1000000\"}", writer.toString());
      assertLoadAs(writer.toString(), 100);
      JsonStreamWriter forward = new JsonStreamWriter();
      JsonStreamWriter reverse = new JsonStreamWriter();
      status.__commit("diff", forward, reverse);
      Assert.assertEquals("\"diff\":{\"state\":100,\"hash\":\"z1Z7GvHQuBJPpoce5G15xg==\",\"key\":\"key2use\",\"time\":\"1000000\"}", forward.toString());
      Assert.assertEquals("", reverse.toString());
      Assert.assertEquals("Nothing;key2use;z1Z7GvHQuBJPpoce5G15xg==;1000000", status.toString());
    }
  }

  @Test
  public void hydrate_delete() {
    MockCallerForReplication caller = new MockCallerForReplication();
    RxInt64 documentTime = new RxInt64(null, 10000);
    RxReplicationStatus status = new RxReplicationStatus(new MockRxParent(), documentTime, "service", "method");
    AtomicReference<NtToDynamic> value = new AtomicReference<>(MockReplicationService.SIMPLE_KEY_OBJECT("key2use"));
    RxLazy<NtToDynamic> lazy = new RxLazy<>(null, () -> value.get(), null);
    lazy.__subscribe(status);
    status.linkToValue(lazy);
    status.__insert(new JsonStreamReader("{\"state\":310,\"key\":\"key2use\",\"time\":\"10000\",\"failures\":1}"));
    {
      JsonStreamWriter writer = new JsonStreamWriter();
      status.__dump(writer);
      Assert.assertEquals("{\"state\":310,\"key\":\"key2use\",\"time\":\"10000\",\"failures\":1}", writer.toString());
      assertLoadAs(writer.toString(), 310);
      JsonStreamWriter forward = new JsonStreamWriter();
      JsonStreamWriter reverse = new JsonStreamWriter();
      status.__commit("diff", forward, reverse);
      Assert.assertEquals("", forward.toString());
      Assert.assertEquals("", reverse.toString());
      Assert.assertEquals("DeleteRequested;key2use;null;10000", status.toString());
    }
    status.progress(caller);
    SequencedTestExecutor executor = new SequencedTestExecutor();
    status.commit(executor);
    executor.next();
    {
      JsonStreamWriter writer = new JsonStreamWriter();
      status.__dump(writer);
      Assert.assertEquals("{\"state\":310,\"key\":\"key2use\",\"time\":\"10000\",\"failures\":1}", writer.toString());
      assertLoadAs(writer.toString(), 310);
      JsonStreamWriter forward = new JsonStreamWriter();
      JsonStreamWriter reverse = new JsonStreamWriter();
      status.__commit("diff", forward, reverse);
      Assert.assertEquals("", forward.toString());
      Assert.assertEquals("", reverse.toString());
      Assert.assertEquals("DeleteRequested;key2use;null;10000", status.toString());
    }
    executor.assertEmpty();
    documentTime.set(1000000);
    status.progress(caller);
    status.commit(executor);
    executor.next();
    {
      JsonStreamWriter writer = new JsonStreamWriter();
      status.__dump(writer);
      Assert.assertEquals("{\"state\":320,\"key\":\"key2use\",\"time\":\"10000\",\"failures\":1}", writer.toString());
      assertLoadAs(writer.toString(), 310);
      JsonStreamWriter forward = new JsonStreamWriter();
      JsonStreamWriter reverse = new JsonStreamWriter();
      status.__commit("diff", forward, reverse);
      Assert.assertEquals("\"diff\":{\"state\":320,\"key\":\"key2use\",\"time\":\"10000\",\"failures\":1}", forward.toString());
      Assert.assertEquals("", reverse.toString());
      Assert.assertEquals("DeleteInflight;key2use;null;10000", status.toString());
    }
    executor.next();
    {
      JsonStreamWriter writer = new JsonStreamWriter();
      status.__dump(writer);
      Assert.assertEquals("{\"state\":100}", writer.toString());
      assertLoadAs(writer.toString(), 100);
      JsonStreamWriter forward = new JsonStreamWriter();
      JsonStreamWriter reverse = new JsonStreamWriter();
      status.__commit("diff", forward, reverse);
      Assert.assertEquals("\"diff\":{\"state\":100}", forward.toString());
      Assert.assertEquals("", reverse.toString());
      Assert.assertEquals("Nothing;null;null;0", status.toString());
    }
  }

  @Test
  public void sequence_infinite_failures_put() {
    MockCallerForReplication caller = new MockCallerForReplication();
    RxReplicationStatus status = new RxReplicationStatus(new MockRxParent(), new RxInt64(null, 10000), "service", "failure");
    AtomicReference<NtToDynamic> value = new AtomicReference<>(MockReplicationService.SIMPLE_KEY_OBJECT("key2use"));
    RxLazy<NtToDynamic> lazy = new RxLazy<>(null, () -> value.get(), null);
    lazy.__subscribe(status);
    status.linkToValue(lazy);
    {
      JsonStreamWriter writer = new JsonStreamWriter();
      status.__dump(writer);
      Assert.assertEquals("{\"state\":100}", writer.toString());
      assertLoadAs(writer.toString(), 100);
      JsonStreamWriter forward = new JsonStreamWriter();
      JsonStreamWriter reverse = new JsonStreamWriter();
      status.__commit("diff", forward, reverse);
      Assert.assertEquals("", forward.toString());
      Assert.assertEquals("", reverse.toString());
      Assert.assertEquals("Nothing;null;null;0", status.toString());
    }
    status.progress(caller);
    {
      JsonStreamWriter writer = new JsonStreamWriter();
      status.__dump(writer);
      Assert.assertEquals("{\"state\":110,\"key\":\"key2use\",\"time\":\"10000\"}", writer.toString());
      assertLoadAs(writer.toString(), 110);
      JsonStreamWriter forward = new JsonStreamWriter();
      JsonStreamWriter reverse = new JsonStreamWriter();
      status.__commit("diff", forward, reverse);
      Assert.assertEquals("\"diff\":{\"state\":110,\"key\":\"key2use\",\"time\":\"10000\"}", forward.toString());
      Assert.assertEquals("", reverse.toString());
    }
    SequencedTestExecutor executor = new SequencedTestExecutor();
    status.commit(executor);
    {
      JsonStreamWriter writer = new JsonStreamWriter();
      status.__dump(writer);
      Assert.assertEquals("{\"state\":110,\"key\":\"key2use\",\"time\":\"10000\"}", writer.toString());
      assertLoadAs(writer.toString(), 110);
      JsonStreamWriter forward = new JsonStreamWriter();
      JsonStreamWriter reverse = new JsonStreamWriter();
      status.__commit("diff", forward, reverse);
      Assert.assertEquals("", forward.toString());
      Assert.assertEquals("", reverse.toString());
    }
    executor.next();
    {
      JsonStreamWriter writer = new JsonStreamWriter();
      status.__dump(writer);
      Assert.assertEquals("{\"state\":120,\"key\":\"key2use\",\"time\":\"10000\"}", writer.toString());
      assertLoadAs(writer.toString(), 110);
      JsonStreamWriter forward = new JsonStreamWriter();
      JsonStreamWriter reverse = new JsonStreamWriter();
      status.__commit("diff", forward, reverse);
      Assert.assertEquals("\"diff\":{\"state\":120,\"key\":\"key2use\",\"time\":\"10000\"}", forward.toString());
      Assert.assertEquals("", reverse.toString());
    }
    for (int k = 1; k < 1000; k++) {
      executor.next();
      {
        JsonStreamWriter writer = new JsonStreamWriter();
        status.__dump(writer);
        Assert.assertEquals("{\"state\":150,\"key\":\"key2use\",\"time\":\"10000\",\"failures\":" + k + "}", writer.toString());
        assertLoadAs(writer.toString(), 110);
        JsonStreamWriter forward = new JsonStreamWriter();
        JsonStreamWriter reverse = new JsonStreamWriter();
        status.__commit("diff", forward, reverse);
        Assert.assertEquals("\"diff\":{\"state\":150,\"key\":\"key2use\",\"time\":\"10000\",\"failures\":" + k + "}", forward.toString());
        Assert.assertEquals("", reverse.toString());
      }
      executor.next();
      {
        JsonStreamWriter writer = new JsonStreamWriter();
        status.__dump(writer);
        Assert.assertEquals("{\"state\":110,\"key\":\"key2use\",\"time\":\"10000\",\"failures\":" + k + "}", writer.toString());
        assertLoadAs(writer.toString(), 110);
        JsonStreamWriter forward = new JsonStreamWriter();
        JsonStreamWriter reverse = new JsonStreamWriter();
        status.__commit("diff", forward, reverse);
        Assert.assertEquals("\"diff\":{\"state\":110,\"key\":\"key2use\",\"time\":\"10000\",\"failures\":" + k + "}", forward.toString());
        Assert.assertEquals("", reverse.toString());
      }
      executor.next();
      {
        JsonStreamWriter writer = new JsonStreamWriter();
        status.__dump(writer);
        Assert.assertEquals("{\"state\":120,\"key\":\"key2use\",\"time\":\"10000\",\"failures\":" + k + "}", writer.toString());
        assertLoadAs(writer.toString(), 110);
        JsonStreamWriter forward = new JsonStreamWriter();
        JsonStreamWriter reverse = new JsonStreamWriter();
        status.__commit("diff", forward, reverse);
        Assert.assertEquals("\"diff\":{\"state\":120,\"key\":\"key2use\",\"time\":\"10000\",\"failures\":" + k + "}", forward.toString());
        Assert.assertEquals("", reverse.toString());
      }
    }
    Assert.assertEquals(3600000, status.getBackoff());
  }
}
