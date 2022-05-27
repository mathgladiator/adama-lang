/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.runtime.data.managed;

import org.adamalang.common.NamedRunnable;
import org.adamalang.common.SimpleExecutor;
import org.adamalang.runtime.data.ArchivingDataService;
import org.adamalang.runtime.data.FinderService;
import org.adamalang.runtime.data.Key;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/** shared state between the machine and the managed data service */
public class Base {
  public final FinderService finder;
  public final ArchivingDataService data;
  public final String region;
  public final String target;
  public final HashMap<Key, Machine> documents;
  public final SimpleExecutor executor;
  public final int archiveTimeMilliseconds;
  private AtomicInteger freeFailureBackoff;

  public Base(FinderService finder, ArchivingDataService data, String region, String target, SimpleExecutor executor, int archiveTimeMilliseconds) {
    this.finder = finder;
    this.data = data;
    this.region = region;
    this.target = target;
    this.documents = new HashMap<>();
    this.executor = executor;
    this.archiveTimeMilliseconds = archiveTimeMilliseconds;
    this.freeFailureBackoff = new AtomicInteger(1);
  }

  /** jump into a state machine for a given key */
  public void on(Key key, Consumer<Machine> action) {
    executor.execute(new NamedRunnable("managed-on") {
      @Override
      public void execute() throws Exception {
        Machine machine = documents.get(key);
        if (machine == null) {
          machine = new Machine(key, Base.this);
          documents.put(key, machine);
        }
        action.accept(machine);
      }
    });
  }

  public void reportFreeSuccess() {
    this.freeFailureBackoff.set(Math.max(1, (int) (freeFailureBackoff.get() * Math.random())));
  }

  public int reportFreeFailureGetRetryBackoff() {
    int prior = freeFailureBackoff.get();
    this.freeFailureBackoff.set(Math.min(5000, (int) (prior * (1.0 + Math.random()))) + 1);
    return prior;
  }

}
