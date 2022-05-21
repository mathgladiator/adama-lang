package org.adamalang.runtime.data.managed;

import org.adamalang.common.NamedRunnable;
import org.adamalang.common.SimpleExecutor;
import org.adamalang.runtime.data.ArchivingDataService;
import org.adamalang.runtime.data.FinderService;
import org.adamalang.runtime.data.Key;

import java.util.HashMap;
import java.util.function.Consumer;

/** shared state between the machine and the managed data service */
public class Base {
  public final FinderService finder;
  public final ArchivingDataService data;
  public final String region;
  public final String target;
  public final HashMap<Key, Machine> documents;
  public final SimpleExecutor executor;

  public Base(FinderService finder, ArchivingDataService data, String region, String target, SimpleExecutor executor) {
    this.finder = finder;
    this.data = data;
    this.region = region;
    this.target = target;
    this.documents = new HashMap<>();
    this.executor = executor;
  }

  /** jump into a state machine for a given key */
  public void on(Key key, Consumer<Machine> action) {
    executor.execute(new NamedRunnable("managed-on") {
      @Override
      public void execute() throws Exception {
        Machine machine = documents.get(key);
        if (machine == null) {
          machine = new Machine(key, Base.this);
        }
        action.accept(machine);
      }
    });
  }
}
