/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's Apache2); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.common.pool;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.SimpleExecutor;
import org.adamalang.common.TimeSource;
import org.adamalang.common.gossip.MockTime;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class AsyncPoolTests {

  public class FauxConn {
    public int stuff = 0;
  }

  public class ConnManager implements PoolActions<String, FauxConn> {
    public boolean failure = false;

    @Override
    public void create(String request, Callback<FauxConn> created) {
      if (failure) {
        created.failure(new ErrorCodeException(-123));
        return;
      }
      created.success(new FauxConn());
    }

    @Override
    public void destroy(FauxConn item) {
      item.stuff = -1;
    }
  }

  @Test
  public void battery_enforce_various_limits() {
    TimeSource time = new MockTime();
    ConnManager mgr = new ConnManager();
    AsyncPool<String, FauxConn> pool = new AsyncPool<>(SimpleExecutor.NOW, time, 5000, 5, 13, 1234, mgr);

    ArrayList<PoolItem<FauxConn>> values = new ArrayList<>();
    AtomicInteger errors = new AtomicInteger(0);
    for (int j = 0; j < 10; j++) {
      final int _j = j;
      for (int k = 0; k < 20; k++) {
        pool.get("Hello World", new Callback<PoolItem<FauxConn>>() {
          @Override
          public void success(PoolItem<FauxConn> value) {
            int prior = value.item().stuff;
            value.item().stuff++;
            Assert.assertEquals(_j % 5, prior);
            values.add(value);
          }

          @Override
          public void failure(ErrorCodeException ex) {
            errors.incrementAndGet();
          }
        });
      }
      Assert.assertEquals(7 * (1 + j), errors.get());
      for (PoolItem<FauxConn> value : values) {
        value.returnToPool();
      }
      values.clear();
    }
  }

  @Test
  public void failures() {
    TimeSource time = new MockTime();
    ConnManager mgr = new ConnManager();
    AsyncPool<String, FauxConn> pool = new AsyncPool<>(SimpleExecutor.NOW, time, 5000, 5, 13, 1234, mgr);
    AtomicInteger errors = new AtomicInteger(0);
    mgr.failure = true;
    pool.get("Hello World", new Callback<PoolItem<FauxConn>>() {
      @Override
      public void success(PoolItem<FauxConn> value) {
      }

      @Override
      public void failure(ErrorCodeException ex) {
        errors.incrementAndGet();
      }
    });
    Assert.assertEquals(1, errors.get());
    mgr.failure = false;
    pool.get("Hello World", new Callback<PoolItem<FauxConn>>() {
      @Override
      public void success(PoolItem<FauxConn> value) {
        value.item().stuff = 123;
        value.signalFailure();
      }

      @Override
      public void failure(ErrorCodeException ex) {
      }
    });

    pool.get("Hello World", new Callback<PoolItem<FauxConn>>() {
      @Override
      public void success(PoolItem<FauxConn> value) {
        Assert.assertEquals(0, value.item().stuff);
        value.item().stuff = 42;
        value.returnToPool();
      }

      @Override
      public void failure(ErrorCodeException ex) {
      }
    });
    pool.get("Hello World", new Callback<PoolItem<FauxConn>>() {
      @Override
      public void success(PoolItem<FauxConn> value) {
        Assert.assertEquals(42, value.item().stuff);
        value.returnToPool();
      }

      @Override
      public void failure(ErrorCodeException ex) {
      }
    });
  }

  @Test
  public void aging() {
    MockTime time = new MockTime();
    ConnManager mgr = new ConnManager();
    AsyncPool<String, FauxConn> pool = new AsyncPool<>(SimpleExecutor.NOW, time, 5000, 10000, 13, 1234, mgr);
    for (int k = 0; k < 100; k++) {
      pool.get("Hello World", new Callback<PoolItem<FauxConn>>() {
        @Override
        public void success(PoolItem<FauxConn> value) {
          value.item().stuff ++;
          value.returnToPool();
          Assert.assertTrue(value.item().stuff <= 4);
        }

        @Override
        public void failure(ErrorCodeException ex) {
        }
      });
      time.currentTime += 1500;
    }
  }

  @Test
  public void max_usage() {
    MockTime time = new MockTime();
    ConnManager mgr = new ConnManager();
    AsyncPool<String, FauxConn> pool = new AsyncPool<>(SimpleExecutor.NOW, time, 5000, 5, 13, 1234, mgr);
    for (int k = 0; k < 100; k++) {
      pool.get("Hello World", new Callback<PoolItem<FauxConn>>() {
        @Override
        public void success(PoolItem<FauxConn> value) {
          value.item().stuff ++;
          value.returnToPool();
          Assert.assertTrue(value.item().stuff <= 5);
        }
        @Override
        public void failure(ErrorCodeException ex) {
        }
      });
    }
  }

  @Test
  public void sweep() {
    MockTime time = new MockTime();
    ConnManager mgr = new ConnManager();
    AsyncPool<String, FauxConn> pool = new AsyncPool<>(SimpleExecutor.NOW, time, 5000, 5, 13, 1234, mgr);
    pool.get("Hello World", new Callback<PoolItem<FauxConn>>() {
      @Override
      public void success(PoolItem<FauxConn> value) {
        value.returnToPool();
      }

      @Override
      public void failure(ErrorCodeException ex) {

      }
    });
    time.currentTime = 4999;
    Assert.assertEquals(0, pool.sweep());
    time.currentTime = 5000;
    Assert.assertEquals(1, pool.sweep());
    Assert.assertEquals(0, pool.sweep());
  }

  @Test
  public void schedule() throws Exception {
    ConnManager mgr = new ConnManager();
    SimpleExecutor executor = SimpleExecutor.create("test");
    try {
      AsyncPool<String, FauxConn> pool = new AsyncPool<>(executor, TimeSource.REAL_TIME, 5, 5, 13, 1234, mgr);
      pool.get("Hello World", new Callback<PoolItem<FauxConn>>() {
        @Override
        public void success(PoolItem<FauxConn> value) {
          value.returnToPool();
        }

        @Override
        public void failure(ErrorCodeException ex) {
        }
      });
      AtomicBoolean alive = new AtomicBoolean(true);
      pool.scheduleSweeping(alive);
      Thread.sleep(500);
      alive.set(false);
    } finally {
      executor.shutdown();
    }
  }
}
