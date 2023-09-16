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
package org.adamalang.common.gossip;

import org.junit.Assert;
import org.junit.Test;

public class InstanceSetChainTests extends CommonTest {
  @Test
  public void flow_empty() {
    MockTime time = new MockTime();
    InstanceSetChain chain = new InstanceSetChain(time);
    Assert.assertNull(chain.pick("xyz"));
    Assert.assertNull(chain.find("myhash"));
    InstanceSet set = chain.find(chain.current().hash());
    Assert.assertNotNull(set);
    chain.scan();
    chain.gc();
    Assert.assertEquals(0, chain.missing(set).length);
    Assert.assertEquals(0, chain.all().length);
    Assert.assertEquals(0, chain.recent().length);
    Assert.assertEquals(0, chain.deletes().length);
  }

  @Test
  public void timeproxy() {
    MockTime time = new MockTime();
    InstanceSetChain chain = new InstanceSetChain(time);
    time.currentTime = 10000;
    Assert.assertEquals(10000, chain.now());
    time.currentTime = 20000;
    Assert.assertEquals(20000, chain.now());
  }

  @Test
  public void scan() {
    MockTime time = new MockTime();
    InstanceSetChain chain = new InstanceSetChain(time);
    Assert.assertEquals("1B2M2Y8AsgTpgAmY7PhCfg==", chain.current().hash());
    time.currentTime = 5000;
    chain.ingest(ENDPOINTS(A(), B()), new String[]{}, false);
    chain.current().ingest(counters(1000, 1000), time.nowMilliseconds());
    Assert.assertEquals("ltgPF0P/6UcAj3+6Mnd/sA==", chain.current().hash());
    time.currentTime = 15000;
    Assert.assertEquals(5000, chain.scan());
    Assert.assertEquals("ltgPF0P/6UcAj3+6Mnd/sA==", chain.current().hash());
    time.currentTime = 15000;
    Assert.assertEquals(5000, chain.scan());
    Assert.assertEquals("ltgPF0P/6UcAj3+6Mnd/sA==", chain.current().hash());
    time.currentTime = 15001;
    Assert.assertEquals(15001, chain.scan());
    Assert.assertEquals("1B2M2Y8AsgTpgAmY7PhCfg==", chain.current().hash());
  }

  @Test
  public void scan_local_dont_change() {
    MockTime time = new MockTime();
    InstanceSetChain chain = new InstanceSetChain(time);
    Assert.assertEquals("1B2M2Y8AsgTpgAmY7PhCfg==", chain.current().hash());
    time.currentTime = 5000;
    chain.ingest(ENDPOINTS(A(), B()), new String[]{}, true);
    chain.current().ingest(counters(1000, 1000), time.nowMilliseconds());
    Assert.assertEquals("ltgPF0P/6UcAj3+6Mnd/sA==", chain.current().hash());
    time.currentTime = 15000;
    Assert.assertEquals(5000, chain.scan());
    Assert.assertEquals("ltgPF0P/6UcAj3+6Mnd/sA==", chain.current().hash());
    time.currentTime = 15000;
    Assert.assertEquals(5000, chain.scan());
    Assert.assertEquals("ltgPF0P/6UcAj3+6Mnd/sA==", chain.current().hash());
    time.currentTime = 15001;
    Assert.assertEquals(5000, chain.scan());
    Assert.assertEquals("ltgPF0P/6UcAj3+6Mnd/sA==", chain.current().hash());
  }

  @Test
  public void ingest() {
    MockTime time = new MockTime();
    InstanceSetChain chain = new InstanceSetChain(time);
    Assert.assertEquals("1B2M2Y8AsgTpgAmY7PhCfg==", chain.current().hash());
    chain.ingest(ENDPOINTS(A(), B()), new String[]{}, false);
    time.currentTime = 5000;
    Assert.assertEquals("ltgPF0P/6UcAj3+6Mnd/sA==", chain.current().hash());
    chain.current().ingest(counters(1000, 1000), time.nowMilliseconds());
    Assert.assertEquals("ltgPF0P/6UcAj3+6Mnd/sA==", chain.current().hash());
    time.currentTime = 10000;
    chain.ingest(ENDPOINTS(), new String[]{"id-a"}, false);
    Assert.assertEquals("ltgPF0P/6UcAj3+6Mnd/sA==", chain.current().hash());
    Assert.assertEquals(0, chain.deletes().length);
    time.currentTime = 15000;
    chain.ingest(ENDPOINTS(), new String[]{"id-a"}, false);
    Assert.assertEquals("PlBLj9Ty9gKbLiKc59dLig==", chain.current().hash());
    Assert.assertEquals(1, chain.deletes().length);
    Assert.assertEquals("id-a", chain.deletes()[0]);
    chain.ingest(ENDPOINTS(A(), B()), new String[]{}, false);
    Assert.assertEquals(0, chain.deletes().length);
    time.currentTime = 30001;
    chain.ingest(ENDPOINTS(), new String[]{"id-a"}, false);
    Assert.assertEquals(1, chain.deletes().length);
    chain.gc();
    Assert.assertEquals(1, chain.deletes().length);
    time.currentTime = 90001;
    chain.gc();
    Assert.assertEquals(1, chain.deletes().length);
    time.currentTime = 90002;
    chain.gc();
    Assert.assertEquals(0, chain.deletes().length);
  }
}
