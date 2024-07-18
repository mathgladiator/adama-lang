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
import org.adamalang.runtime.mocks.MockLivingDocument;
import org.adamalang.runtime.mocks.MockRxParent;
import org.adamalang.runtime.natives.NtToDynamic;
import org.adamalang.runtime.reactives.RxInt64;
import org.adamalang.runtime.reactives.RxLazy;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicReference;

public class ReplicationEngineTests {
  @Test
  public void instant_single() {
    MockLivingDocument doc = new MockLivingDocument();
    ReplicationEngine re = new ReplicationEngine(doc);
    {
      MockRxParent parent = new MockRxParent();
      RxReplicationStatus status = new RxReplicationStatus(parent, new RxInt64(null, 10000), "rservice", "method");
      AtomicReference<NtToDynamic> value = new AtomicReference<>(MockReplicationService.SIMPLE_KEY_OBJECT("key2use"));
      RxLazy<NtToDynamic> lazy = new RxLazy<>(null, () -> value.get(), null);
      lazy.__subscribe(status);
      re.link(status, lazy);
      {
        JsonStreamWriter forward = new JsonStreamWriter();
        JsonStreamWriter reverse = new JsonStreamWriter();
        re.commit(forward, reverse);
        Assert.assertEquals("", forward.toString());
        Assert.assertEquals("", reverse.toString());
        JsonStreamWriter dump = new JsonStreamWriter();
        re.dump(dump);
        Assert.assertEquals("{}", dump.toString());
      }
      re.signalDurableAndExecute(SimpleExecutor.NOW);
      {
        JsonStreamWriter forward = new JsonStreamWriter();
        JsonStreamWriter reverse = new JsonStreamWriter();
        re.commit(forward, reverse);
        Assert.assertEquals("", forward.toString());
        Assert.assertEquals("", reverse.toString());
        JsonStreamWriter dump = new JsonStreamWriter();
        re.dump(dump);
        Assert.assertEquals("{}", dump.toString());
      }
      parent.alive = false;

      {
        JsonStreamWriter forward = new JsonStreamWriter();
        JsonStreamWriter reverse = new JsonStreamWriter();
        re.commit(forward, reverse);
        Assert.assertEquals("\"__replication\":{\"5TtJHVK2lBxrADSFYheExQ==\":{\"s\":\"rservice\",\"m\":\"method\",\"k\":\"key2use\"}}", forward.toString());
        Assert.assertEquals("\"__replication\":{}", reverse.toString());
        JsonStreamWriter dump = new JsonStreamWriter();
        re.dump(dump);
        Assert.assertEquals("{\"5TtJHVK2lBxrADSFYheExQ==\":{\"s\":\"rservice\",\"m\":\"method\",\"k\":\"key2use\"}}", dump.toString());
      }
      re.signalDurableAndExecute(SimpleExecutor.NOW);
      {
        JsonStreamWriter forward = new JsonStreamWriter();
        JsonStreamWriter reverse = new JsonStreamWriter();
        re.commit(forward, reverse);
        Assert.assertEquals("\"__replication\":{\"5TtJHVK2lBxrADSFYheExQ==\":null}", forward.toString());
        Assert.assertEquals("\"__replication\":{}", reverse.toString());
        JsonStreamWriter dump = new JsonStreamWriter();
        re.dump(dump);
        Assert.assertEquals("{}", dump.toString());
      }
      Assert.assertEquals("BEGIN[method]:key2use", doc.rservice.at(0));
      Assert.assertEquals("ASK[method]:key2use", doc.rservice.at(1));
      Assert.assertEquals("SUCCESS[method]:key2use", doc.rservice.at(2));
      Assert.assertEquals("BEGIN[method]:key2use", doc.rservice.at(3));
      Assert.assertEquals("ASK[method]:key2use", doc.rservice.at(4));
      Assert.assertEquals("DELETED[method]:key2use", doc.rservice.at(5));
    }
  }

  @Test
  public void instant_load_resurrect_tombstones() {
    MockLivingDocument doc = new MockLivingDocument();
    ReplicationEngine re = new ReplicationEngine(doc);
    RxInt64 time = new RxInt64(null, 10000);
    re.load(new JsonStreamReader("{\"5TtJHVK2lBxrADSFYheExQ==\":{\"s\":\"rservice\",\"m\":\"method\",\"k\":\"key2use\"}}"), time);
    {
      {
        JsonStreamWriter forward = new JsonStreamWriter();
        JsonStreamWriter reverse = new JsonStreamWriter();
        re.commit(forward, reverse);
        Assert.assertEquals("", forward.toString());
        Assert.assertEquals("", reverse.toString());
        JsonStreamWriter dump = new JsonStreamWriter();
        re.dump(dump);
        Assert.assertEquals("{\"5TtJHVK2lBxrADSFYheExQ==\":{\"s\":\"rservice\",\"m\":\"method\",\"k\":\"key2use\"}}", dump.toString());
      }
      re.signalDurableAndExecute(SimpleExecutor.NOW);
      {
        JsonStreamWriter forward = new JsonStreamWriter();
        JsonStreamWriter reverse = new JsonStreamWriter();
        re.commit(forward, reverse);
        Assert.assertEquals("\"__replication\":{\"5TtJHVK2lBxrADSFYheExQ==\":null}", forward.toString());
        Assert.assertEquals("\"__replication\":{}", reverse.toString());
        JsonStreamWriter dump = new JsonStreamWriter();
        re.dump(dump);
        Assert.assertEquals("{}", dump.toString());
      }
      Assert.assertEquals("DELETED[method]:key2use", doc.rservice.at(0));
    }
  }
}
