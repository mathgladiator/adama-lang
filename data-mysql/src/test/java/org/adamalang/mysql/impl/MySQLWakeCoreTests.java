package org.adamalang.mysql.impl;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.SimpleExecutor;
import org.adamalang.common.TimeSource;
import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.adamalang.mysql.*;
import org.adamalang.mysql.data.WakeTask;
import org.adamalang.runtime.data.Key;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class MySQLWakeCoreTests {
  @Test
  public void flow() throws Exception {
    DataBaseConfig dataBaseConfig = DataBaseConfigTests.getLocalIntegrationConfig();
    try (DataBase dataBase = new DataBase(dataBaseConfig, new DataBaseMetrics(new NoOpMetricsFactory()))) {
      Installer installer = new Installer(dataBase);
      try {
        installer.install();
        AtomicLong time = new AtomicLong(10000);
        TimeSource ts = new TimeSource() {
          @Override
          public long nowMilliseconds() {
            return time.get();
          }
        };
        MySQLWakeCore core = new MySQLWakeCore(SimpleExecutor.NOW, dataBase, ts);
        Key K = new Key("space", "key");
        AtomicLong idRef1 = new AtomicLong(-1);
        AtomicLong idRef2 = new AtomicLong(-1);

        {
          CountDownLatch wakeSuccess = new CountDownLatch(1);
          core.wake(K, 5000, "r", "m", new Callback<Void>() {
            @Override
            public void success(Void value) {
              wakeSuccess.countDown();
            }

            @Override
            public void failure(ErrorCodeException ex) {
              ex.printStackTrace();
            }
          });
          Assert.assertTrue(wakeSuccess.await(5000, TimeUnit.MILLISECONDS));
          CountDownLatch wakeList1 = new CountDownLatch(1);
          core.list("r", "m", new Callback<List<WakeTask>>() {
            @Override
            public void success(List<WakeTask> tasks) {
              Assert.assertEquals(1, tasks.size());
              idRef1.set(tasks.get(0).id);
              Assert.assertEquals("space", tasks.get(0).key.space);
              Assert.assertEquals("key", tasks.get(0).key.key);
              Assert.assertEquals(15000, tasks.get(0).wake_time);
              wakeList1.countDown();
            }

            @Override
            public void failure(ErrorCodeException ex) {

            }
          });
          Assert.assertTrue(wakeList1.await(5000, TimeUnit.MILLISECONDS));
        }
        {
          CountDownLatch wakeSuccess = new CountDownLatch(1);
          core.wake(K, 27000, "r", "m", new Callback<Void>() {
            @Override
            public void success(Void value) {
              wakeSuccess.countDown();
            }

            @Override
            public void failure(ErrorCodeException ex) {
              ex.printStackTrace();
            }
          });
          Assert.assertTrue(wakeSuccess.await(5000, TimeUnit.MILLISECONDS));
          CountDownLatch wakeList1 = new CountDownLatch(1);
          core.list("r", "m", new Callback<List<WakeTask>>() {
            @Override
            public void success(List<WakeTask> tasks) {
              Assert.assertEquals(1, tasks.size());
              idRef2.set(tasks.get(0).id);
              Assert.assertEquals("space", tasks.get(0).key.space);
              Assert.assertEquals("key", tasks.get(0).key.key);
              Assert.assertEquals(37000, tasks.get(0).wake_time);
              wakeList1.countDown();
            }

            @Override
            public void failure(ErrorCodeException ex) {

            }
          });
          Assert.assertTrue(wakeList1.await(5000, TimeUnit.MILLISECONDS));
        }
        Assert.assertEquals(idRef1.get(), idRef2.get());
        CountDownLatch deleteWorked = new CountDownLatch(1);
        core.delete(idRef1.get(), new Callback<Void>() {
          @Override
          public void success(Void value) {
            deleteWorked.countDown();
          }

          @Override
          public void failure(ErrorCodeException ex) {

          }
        });
        Assert.assertTrue(deleteWorked.await(5000, TimeUnit.MILLISECONDS));
        {
          CountDownLatch wakeList1 = new CountDownLatch(1);
          core.list("r", "m", new Callback<List<WakeTask>>() {
            @Override
            public void success(List<WakeTask> tasks) {
              Assert.assertEquals(0, tasks.size());
              wakeList1.countDown();
            }

            @Override
            public void failure(ErrorCodeException ex) {
            }
          });
          Assert.assertTrue(wakeList1.await(5000, TimeUnit.MILLISECONDS));
        }
      } finally {
        installer.uninstall();
      }
    }
  }
}