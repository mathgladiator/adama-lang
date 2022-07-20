/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.gossip;

import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;

public class InstanceSetChainTests extends CommonTest {
  @Test
  public void flow_empty() {
    MockTime time = new MockTime();
    InstanceSetChain chain = new InstanceSetChain(time);
    Assert.assertNull(chain.find("myhash"));
    InstanceSet set = chain.find(chain.current().hash());
    Assert.assertNotNull(set);
    chain.scan();
    chain.gc();
    Assert.assertTrue(chain.missing(set).isEmpty());
    Assert.assertTrue(chain.all().isEmpty());
    Assert.assertTrue(chain.recent().isEmpty());
    Assert.assertTrue(chain.deletes().isEmpty());
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
    chain.ingest(ENDPOINTS(A(), B()), Collections.emptySet());
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
  public void ingest() {
    MockTime time = new MockTime();
    InstanceSetChain chain = new InstanceSetChain(time);
    Assert.assertEquals("1B2M2Y8AsgTpgAmY7PhCfg==", chain.current().hash());
    chain.ingest(ENDPOINTS(A(), B()), Collections.emptySet());
    time.currentTime = 5000;
    Assert.assertEquals("ltgPF0P/6UcAj3+6Mnd/sA==", chain.current().hash());
    chain.current().ingest(counters(1000, 1000), time.nowMilliseconds());
    Assert.assertEquals("ltgPF0P/6UcAj3+6Mnd/sA==", chain.current().hash());
    time.currentTime = 10000;
    chain.ingest(ENDPOINTS(), Collections.singleton("id-a"));
    Assert.assertEquals("ltgPF0P/6UcAj3+6Mnd/sA==", chain.current().hash());
    Assert.assertEquals(0, chain.deletes().size());
    time.currentTime = 15000;
    chain.ingest(ENDPOINTS(), Collections.singleton("id-a"));
    Assert.assertEquals("PlBLj9Ty9gKbLiKc59dLig==", chain.current().hash());
    Assert.assertEquals(1, chain.deletes().size());
    Assert.assertEquals("id-a", chain.deletes().iterator().next());
    chain.ingest(ENDPOINTS(A(), B()), Collections.emptySet());
    Assert.assertEquals(0, chain.deletes().size());
    time.currentTime = 30001;
    chain.ingest(ENDPOINTS(), Collections.singleton("id-a"));
    Assert.assertEquals(1, chain.deletes().size());
    chain.gc();
    Assert.assertEquals(1, chain.deletes().size());
    time.currentTime = 90001;
    chain.gc();
    Assert.assertEquals(1, chain.deletes().size());
    time.currentTime = 90002;
    chain.gc();
    Assert.assertEquals(0, chain.deletes().size());
  }
}
