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
package org.adamalang.overlord.roles;

import org.adamalang.caravan.contracts.Cloud;
import org.adamalang.caravan.events.AssetWalker;
import org.adamalang.caravan.events.RestoreLoader;
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
import org.adamalang.runtime.data.ColdAssetSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class GlobalGarbageCollector {
  private static final Logger LOGGER = LoggerFactory.getLogger(GlobalGarbageCollector.class);

  public static void kickOff(OverlordMetrics metrics, DataBase dataBase, ColdAssetSystem lister, Cloud cloud, AtomicBoolean alive) {
    SimpleExecutor executor = SimpleExecutor.create("garbage-man");
    executor.schedule(new NamedRunnable("garbage-man") {
      @Override
      public void execute() throws Exception {
        if (alive.get()) {
          LOGGER.error("garbage-man-start");
          try {
            ArrayList<GCTask> tasks = FinderOperations.produceGCTasks(dataBase);
            CountDownLatch allTasksDone = new CountDownLatch(tasks.size());
            for (GCTask task : tasks) {
              metrics.garbage_collector_found_task.run();
              LOGGER.error("to-collect-for:" + task.seq + "," + task.key);
              final Key key = new Key(task.space, task.key);
              lister.listAssetsOf(key, new Callback<List<String>>() {
                @Override
                public void success(List<String> assets) {
                  try {
                    if (assets.size() == 0) {
                      LOGGER.error("empty-task-detected:" + task);
                      FinderOperations.lowerTask(dataBase, task);
                    } else {
                      LOGGER.error("task-with-assets-detected:" + task + "-asset count:" + assets.size());
                      cloud.restore(key, task.archiveKey, new Callback<File>() {
                        @Override
                        public void success(File archivedFile) {
                          try {
                            ArrayList<byte[]> writes = RestoreLoader.load(archivedFile);
                            HashSet<String> liveIds = AssetWalker.idsOf(writes);
                            ArrayList<String> kill = new ArrayList<>();
                            for (String testId : assets) {
                              if (!liveIds.contains(testId)) {
                                LOGGER.error("task-with-dead-asset:" + testId);
                                kill.add(testId);
                              } else {
                                LOGGER.error("task-with-living-asset:" + testId);
                              }
                            }
                            if (kill.size() == 0) {
                              LOGGER.error("task-with-no-assets");
                              FinderOperations.lowerTask(dataBase, task);
                            } else {
                              if (FinderOperations.validateTask(dataBase, task)) {
                                for (String toKill : kill) {
                                  LOGGER.error("delete-" + key.space + "/" + key.key + "/" + toKill);
                                  lister.deleteAsset(key, toKill, Callback.DONT_CARE_VOID);
                                }
                              }
                              LOGGER.error("task-with-assets");
                              FinderOperations.lowerTask(dataBase, task);
                            }
                          } catch (Exception ex) {
                            LOGGER.error("garbage-man-task-crashed-with-archive:[" + task + "]", ex);
                          } finally {
                            archivedFile.delete();
                          }
                        }

                        @Override
                        public void failure(ErrorCodeException ex) {
                          LOGGER.error("cloud-failed-the-garbage-man", ex);
                        }
                      });
                    }
                  } catch (Exception ex) {
                    LOGGER.error("garbage-man-task-crashed:[" + task + "]", ex);
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
