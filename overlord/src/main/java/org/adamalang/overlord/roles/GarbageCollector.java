package org.adamalang.overlord.roles;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.NamedRunnable;
import org.adamalang.common.SimpleExecutor;
import org.adamalang.mysql.DataBase;
import org.adamalang.mysql.data.GCTask;
import org.adamalang.mysql.model.FinderOperations;
import org.adamalang.mysql.model.Sentinel;
import org.adamalang.overlord.OverlordMetrics;
import org.adamalang.runtime.data.Key;
import org.adamalang.runtime.data.LiveAssetLister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class GarbageCollector {
  private static final Logger LOGGER = LoggerFactory.getLogger(GarbageCollector.class);

  public static void kickOff(OverlordMetrics metrics, DataBase dataBase, LiveAssetLister lister, AtomicBoolean alive) {
    SimpleExecutor executor = SimpleExecutor.create("garbage-man");
    executor.schedule(new NamedRunnable("garbage-man") {
      @Override
      public void execute() throws Exception {
        if (alive.get()) {
          try {
            ArrayList<GCTask> tasks = FinderOperations.produceGCTasks(dataBase);
            CountDownLatch allTasksDone = new CountDownLatch(tasks.size());
            for (GCTask task : tasks) {
              metrics.garbage_collector_found_task.run();
              LOGGER.error("to-collect-for:" + task.seq + "," + task.key);
              lister.list(new Key(task.space, task.key), new Callback<List<String>>() {
                @Override
                public void success(List<String> assets) {
                  try {
                    if (assets.size() == 0) {
                      FinderOperations.lowerTask(dataBase, task);
                    } else {
                      // TODO: download assets from storage
                      if (FinderOperations.validateTask(dataBase, task)) {
                        // TODO: confirm the document is still ready for GC (hasn't been picked up)
                        FinderOperations.lowerTask(dataBase, task);
                      }
                    }
                  } catch (Exception ex) {
                    LOGGER.error("garbage-man-task-crashed:[" + task.space + "-" + task.key + "]", ex);
                  } finally {
                    allTasksDone.countDown();
                  }
                }

                @Override
                public void failure(ErrorCodeException ex) {
                  allTasksDone.countDown();
                }
              });
            }
            if (!allTasksDone.await(5 * 60 * 1000, TimeUnit.MILLISECONDS)) {
              LOGGER.error("garbage-collector-running-too-slow");
              metrics.garbage_collector_behind.set(1);
            } else {
              metrics.garbage_collector_behind.set(0);
            }
            Sentinel.ping(dataBase, "garbage-man", System.currentTimeMillis());
          } catch (Exception ex) {
            LOGGER.error("garbage-man-crashed", ex);
          } finally {
            executor.schedule(this, (int) (60000 + Math.random() * 60000));
          }
        }
      }
    }, 1000);
  }
}
