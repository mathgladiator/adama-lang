/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.async;

public class OutstandingFutureTrackerTests {
/*
  public class AsyncModel {
    public final ObjectNode node;
    public final TObject root;
    public final OutstandingFutureTracker futures;
    
    public AsyncModel() {
      node = Utility.createObjectNode();
      root = TObject.BindRootTree(node);
      futures = new OutstandingFutureTracker(root);
      Assert.assertEquals("{\"__auto_future_id\":0}", node.toString());
    }
  
    public ObjectNode success() {
      futures.commit();
      ObjectNode delta = Utility.createObjectNode();
      root.inject("delta", delta, InjectMode.Commit);
      delta.put("temporary", false);
      return delta;
    }

    public ObjectNode failure() {
      ObjectNode delta = Utility.createObjectNode();
      root.inject("delta", delta, InjectMode.Restore);
      delta.put("temporary", true);
      ArrayNode outstanding = futures.toArrayNode();
      futures.restore();
      if (outstanding.size() > 0) {
        delta.set("outstanding", outstanding);
      }
      return delta;
    }
  }
  
  public void commit(TObject root, OutstandingFutureTracker factory, String expectedDelta) {
    factory.commit();
    ObjectNode delta = Utility.createObjectNode();
    root.inject("delta", delta, InjectMode.Commit);
    Assert.assertEquals(expectedDelta, delta.toString());
  }

  public void restore(TObject root, OutstandingFutureTracker factory, String expectedDelta) {
    factory.restore();
    ObjectNode delta = Utility.createObjectNode();
    root.inject("delta", delta, InjectMode.Restore);
    Assert.assertEquals(expectedDelta, delta.toString());
  }
  
  private ClientValue clientValueOf(String agent) {
    return new ClientValue(agent, "tests");
  }
  
  @Test
  public void futuresHappySingleFuture() {
    AsyncModel am = new AsyncModel();
    OutstandingFuture a = am.futures.make("chan", clientValueOf("client"), null, 0, 0, false);
    Assert.assertEquals("chan", a.channel);
    Assert.assertEquals("client", a.who.agent);
    Assert.assertEquals("{\"__auto_future_id\":0}", am.node.toString());
    ObjectNode delta = am.success();
    Assert.assertEquals("{\"delta\":{\"__auto_future_id\":1},\"temporary\":false}", delta.toString());
    Assert.assertEquals("{\"__auto_future_id\":1}", am.node.toString());
  }
  
  @Test
  public void futuresHappySingleFutureGetsCancelledThenCommits() {
    AsyncModel am = new AsyncModel();
    OutstandingFuture a = am.futures.make("chan", clientValueOf("client"), null, 0, 0, false);
    Assert.assertEquals("chan", a.channel);
    Assert.assertEquals("client", a.who.agent);
    Assert.assertEquals("{\"__auto_future_id\":0}", am.node.toString());
    ObjectNode delta0 = am.failure();
    Assert.assertEquals("{\"temporary\":true,\"outstanding\":[{\"id\":1,\"channel\":\"chan\",\"client\":{\"agent\":\"client\",\"authority\":\"tests\"}}]}", delta0.toString());
    Assert.assertEquals("{\"__auto_future_id\":0}", am.node.toString());
    ObjectNode delta1 = am.success();
    Assert.assertEquals("{\"delta\":{\"__auto_future_id\":1},\"temporary\":false}", delta1.toString());
    Assert.assertEquals("{\"__auto_future_id\":1}", am.node.toString());
  }
  
  @Test
  public void futuresHappySingleFutureGetsCancelledThenAcquiresThenCommits() {
    AsyncModel am = new AsyncModel();
    OutstandingFuture a = am.futures.make("chan", clientValueOf("client"), null, 0, 0, false);
    Assert.assertEquals("chan", a.channel);
    Assert.assertEquals("client", a.who.agent);
    Assert.assertEquals("{\"__auto_future_id\":0}", am.node.toString());
    ObjectNode delta0 = am.failure();
    Assert.assertEquals("{\"temporary\":true,\"outstanding\":[{\"id\":1,\"channel\":\"chan\",\"client\":{\"agent\":\"client\",\"authority\":\"tests\"}}]}", delta0.toString());
    Assert.assertEquals("{\"__auto_future_id\":0}", am.node.toString());
    OutstandingFuture b = am.futures.make("chan", clientValueOf("client"), null, 0, 0, false);
    Assert.assertEquals(a, b);
    ObjectNode delta1 = am.success();
    Assert.assertEquals("{\"delta\":{\"__auto_future_id\":1},\"temporary\":false}", delta1.toString());
    Assert.assertEquals("{\"__auto_future_id\":1}", am.node.toString());
  }
  
  @Test
  public void futuresRetriesFailLimbo() {
    AsyncModel am = new AsyncModel();
    OutstandingFuture a = am.futures.make("chan", clientValueOf("client"), null, 0, 0, false);
    Assert.assertEquals("chan", a.channel);
    Assert.assertEquals("client", a.who.agent);
    Assert.assertEquals("{\"__auto_future_id\":0}", am.node.toString());
    ObjectNode delta0 = am.failure();
    Assert.assertEquals("{\"temporary\":true,\"outstanding\":[{\"id\":1,\"channel\":\"chan\",\"client\":{\"agent\":\"client\",\"authority\":\"tests\"}}]}", delta0.toString());
    Assert.assertEquals("{\"__auto_future_id\":0}", am.node.toString());
    OutstandingFuture b = am.futures.make("chan", clientValueOf("client"), null, 0, 0, false);
    Assert.assertEquals(a, b);
    ObjectNode delta1 = am.failure();
    Assert.assertEquals("{\"temporary\":true,\"outstanding\":[{\"id\":1,\"channel\":\"chan\",\"client\":{\"agent\":\"client\",\"authority\":\"tests\"}}]}", delta1.toString());
    Assert.assertEquals("{\"__auto_future_id\":0}", am.node.toString());
  }
  
  @Test
  public void futuresRetriesDifferently() {
    AsyncModel am = new AsyncModel();
    OutstandingFuture a = am.futures.make("chan", clientValueOf("client"), null, 0, 0, false);
    Assert.assertEquals("chan", a.channel);
    Assert.assertEquals("client", a.who.agent);
    Assert.assertEquals("{\"__auto_future_id\":0}", am.node.toString());
    ObjectNode delta0 = am.failure();
    Assert.assertEquals("{\"temporary\":true,\"outstanding\":[{\"id\":1,\"channel\":\"chan\",\"client\":{\"agent\":\"client\",\"authority\":\"tests\"}}]}", delta0.toString());
    Assert.assertEquals("{\"__auto_future_id\":0}", am.node.toString());
    am.futures.make("chan2", clientValueOf("client2"), null, 0, 0, false);
    ObjectNode delta1 = am.failure();
    Assert.assertEquals("{\"temporary\":true,\"outstanding\":[{\"id\":2,\"channel\":\"chan2\",\"client\":{\"agent\":\"client2\",\"authority\":\"tests\"}}]}", delta1.toString());
    Assert.assertEquals("{\"__auto_future_id\":0}", am.node.toString());
  }
  
  @Test
  public void futuresRetriesAll() {
    AsyncModel am = new AsyncModel();
    OutstandingFuture a = am.futures.make("chan", clientValueOf("client"), null, 0, 0, false);
    Assert.assertEquals("chan", a.channel);
    Assert.assertEquals("client", a.who.agent);
    Assert.assertEquals("{\"__auto_future_id\":0}", am.node.toString());
    ObjectNode delta0 = am.failure();
    Assert.assertEquals("{\"temporary\":true,\"outstanding\":[{\"id\":1,\"channel\":\"chan\",\"client\":{\"agent\":\"client\",\"authority\":\"tests\"}}]}", delta0.toString());
    Assert.assertEquals("{\"__auto_future_id\":0}", am.node.toString());
    Assert.assertEquals(a, am.futures.make("chan", clientValueOf("client"), null, 0, 0, false));
    am.futures.make("chan2", clientValueOf("client2"), null, 0, 0, false);
    ObjectNode delta1 = am.failure();
    Assert.assertEquals("{\"temporary\":true,\"outstanding\":[{\"id\":1,\"channel\":\"chan\",\"client\":{\"agent\":\"client\",\"authority\":\"tests\"}},{\"id\":2,\"channel\":\"chan2\",\"client\":{\"agent\":\"client2\",\"authority\":\"tests\"}}]}", delta1.toString());
    Assert.assertEquals("{\"__auto_future_id\":0}", am.node.toString());
  }
  
  @Test
  public void futuresASucceedsButBOutstanding() {
    AsyncModel am = new AsyncModel();
    OutstandingFuture a = am.futures.make("chan", clientValueOf("client"), null, 0, 0, false);
    Assert.assertEquals("chan", a.channel);
    Assert.assertEquals("client", a.who.agent);
    Assert.assertEquals("{\"__auto_future_id\":0}", am.node.toString());
    ObjectNode delta0 = am.failure();
    Assert.assertEquals("{\"temporary\":true,\"outstanding\":[{\"id\":1,\"channel\":\"chan\",\"client\":{\"agent\":\"client\",\"authority\":\"tests\"}}]}", delta0.toString());
    Assert.assertEquals("{\"__auto_future_id\":0}", am.node.toString());
    am.futures.make("chan", clientValueOf("client"), null, 0, 0, false).take();
    OutstandingFuture b = am.futures.make("chan2", clientValueOf("client2"), null, 0, 0, false);
    ObjectNode delta1 = am.failure();
    Assert.assertEquals("{\"temporary\":true,\"outstanding\":[{\"id\":2,\"channel\":\"chan2\",\"client\":{\"agent\":\"client2\",\"authority\":\"tests\"}}]}", delta1.toString());
    Assert.assertEquals("{\"__auto_future_id\":0}", am.node.toString());
  }
  
  @Test
  public void futuresWithOptions() {
    AsyncModel am = new AsyncModel();
    ArrayNode an = Utility.createArrayNode();
    an.addObject().put("x", 123);
    an.addObject().put("x", 456);
    an.addObject().put("x", 789);
    OutstandingFuture a = am.futures.make("chan", clientValueOf("client"), an, 1, 1, true);
    Assert.assertEquals("chan", a.channel);
    Assert.assertEquals("client", a.who.agent);
    Assert.assertEquals("{\"__auto_future_id\":0}", am.node.toString());
    ObjectNode delta0 = am.failure();
    Assert.assertEquals("{\"temporary\":true,\"outstanding\":[{\"id\":1,\"channel\":\"chan\",\"options\":[{\"x\":123},{\"x\":456},{\"x\":789}],\"min\":1,\"max\":1,\"distinct\":true,\"client\":{\"agent\":\"client\",\"authority\":\"tests\"}}]}", delta0.toString());
    Assert.assertEquals("{\"__auto_future_id\":0}", am.node.toString());
    ObjectNode delta1 = am.success();
    Assert.assertEquals("{\"delta\":{\"__auto_future_id\":1},\"temporary\":false}", delta1.toString());
    Assert.assertEquals("{\"__auto_future_id\":1}", am.node.toString());
  }
  */
}
