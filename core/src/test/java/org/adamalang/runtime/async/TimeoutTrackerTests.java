/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.runtime.async;

import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.reactives.RxInt64;
import org.junit.Assert;
import org.junit.Test;

public class TimeoutTrackerTests {

  @Test
  public void save_load() {
    RxInt64 time = new RxInt64(null, 0);
    TimeoutTracker tracker = new TimeoutTracker(time);
    {
      JsonStreamWriter writer = new JsonStreamWriter();
      tracker.dump(writer);
      Assert.assertEquals("", writer.toString());
    }
    {
      tracker.timeouts.put(42, new Timeout(124, 52.4));
      JsonStreamWriter writer = new JsonStreamWriter();
      tracker.dump(writer);
      Assert.assertEquals("\"__timeouts\":{\"42\":{\"timestamp\":\"124\",\"timeout\":52.4}}", writer.toString());
    }
    {
      tracker.timeouts.put(100, new Timeout(1, 13.69));
      JsonStreamWriter writer = new JsonStreamWriter();
      tracker.dump(writer);
      Assert.assertEquals("\"__timeouts\":{\"100\":{\"timestamp\":\"1\",\"timeout\":13.69},\"42\":{\"timestamp\":\"124\",\"timeout\":52.4}}", writer.toString());
    }
    Assert.assertEquals(2, tracker.timeouts.size());
    tracker.hydrate(new JsonStreamReader("{\"100\":null}"));
    Assert.assertEquals(1, tracker.timeouts.size());
    Assert.assertFalse(tracker.timeouts.containsKey(100));
    tracker.hydrate(new JsonStreamReader("{\"1000\":{\"timestamp\":\"2\",\"timeout\":3.14}}"));
    Assert.assertEquals(2, tracker.timeouts.size());
    Assert.assertTrue(tracker.timeouts.containsKey(1000));
    Timeout to = tracker.timeouts.get(1000);
    Assert.assertEquals(2, to.timestamp);
    Assert.assertEquals(3.14, to.timeoutSeconds, 0.1);
  }

  @Test
  public void commit() {
    RxInt64 time = new RxInt64(null, 0);
    TimeoutTracker tracker = new TimeoutTracker(time);
    {
      tracker.create(42, 3.13);
      JsonStreamWriter forward = new JsonStreamWriter();
      JsonStreamWriter reverse = new JsonStreamWriter();
      tracker.commit(forward, reverse);
      Assert.assertEquals("\"__timeouts\":{\"42\":{\"timestamp\":\"0\",\"timeout\":3.13}}", forward.toString());
      Assert.assertEquals("\"__timeouts\":{\"42\":null}", reverse.toString());
    }
    {
      tracker.create(111, 4.2);
      JsonStreamWriter forward = new JsonStreamWriter();
      JsonStreamWriter reverse = new JsonStreamWriter();
      tracker.nuke(forward, reverse);
      Assert.assertEquals("\"__timeouts\":{\"42\":null,\"111\":null}", forward.toString());
      Assert.assertEquals("\"__timeouts\":{\"42\":{\"timestamp\":\"0\",\"timeout\":3.13},\"111\":{\"timestamp\":\"0\",\"timeout\":4.2}}", reverse.toString());
    }
    tracker.create(123, 6.9);
    tracker.revert();
  }

  @Test
  public void temporal_logic_first() {
    RxInt64 time = new RxInt64(null, 1000);
    TimeoutTracker tracker = new TimeoutTracker(time);
    RxInt64 next = new RxInt64(null, 0);
    Assert.assertFalse(tracker.needsInvalidationAndUpdateNext(next));
    tracker.create(12, 5.25);
    Assert.assertTrue(tracker.needsInvalidationAndUpdateNext(next));
    Assert.assertEquals(6250L, next.get().longValue());
  }

  @Test
  public void temporal_logic_closest() {
    RxInt64 time = new RxInt64(null, 1000);
    TimeoutTracker tracker = new TimeoutTracker(time);
    RxInt64 next = new RxInt64(null, 1750);
    Assert.assertFalse(tracker.needsInvalidationAndUpdateNext(next));
    tracker.create(12, 5.25);
    Assert.assertTrue(tracker.needsInvalidationAndUpdateNext(next));
    Assert.assertEquals(1750L, next.get().longValue());
  }

  @Test
  public void temporal_logic_first_pick_closest() {
    RxInt64 time = new RxInt64(null, 1000);
    TimeoutTracker tracker = new TimeoutTracker(time);
    RxInt64 next = new RxInt64(null, 0);
    Assert.assertFalse(tracker.needsInvalidationAndUpdateNext(next));
    tracker.create(12, 5.25);
    tracker.create(102, 1.25);
    Assert.assertTrue(tracker.needsInvalidationAndUpdateNext(next));
    Assert.assertEquals(2250L, next.get().longValue());
  }
}
