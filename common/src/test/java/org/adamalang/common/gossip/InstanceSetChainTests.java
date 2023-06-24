/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.common.gossip;

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
    Assert.assertTrue(chain.missing(set).length == 0);
    Assert.assertTrue(chain.all().length == 0);
    Assert.assertTrue(chain.recent().length == 0);
    Assert.assertTrue(chain.deletes().length == 0);
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
    chain.ingest(ENDPOINTS(A(), B()), new String[]{});
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
    chain.ingest(ENDPOINTS(A(), B()), new String[]{});
    time.currentTime = 5000;
    Assert.assertEquals("ltgPF0P/6UcAj3+6Mnd/sA==", chain.current().hash());
    chain.current().ingest(counters(1000, 1000), time.nowMilliseconds());
    Assert.assertEquals("ltgPF0P/6UcAj3+6Mnd/sA==", chain.current().hash());
    time.currentTime = 10000;
    chain.ingest(ENDPOINTS(), new String[]{"id-a"});
    Assert.assertEquals("ltgPF0P/6UcAj3+6Mnd/sA==", chain.current().hash());
    Assert.assertEquals(0, chain.deletes().length);
    time.currentTime = 15000;
    chain.ingest(ENDPOINTS(), new String[]{"id-a"});
    Assert.assertEquals("PlBLj9Ty9gKbLiKc59dLig==", chain.current().hash());
    Assert.assertEquals(1, chain.deletes().length);
    Assert.assertEquals("id-a", chain.deletes()[0]);
    chain.ingest(ENDPOINTS(A(), B()), new String[]{});
    Assert.assertEquals(0, chain.deletes().length);
    time.currentTime = 30001;
    chain.ingest(ENDPOINTS(), new String[]{"id-a"});
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
