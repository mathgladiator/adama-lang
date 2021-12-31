package org.adamalang.grpc.client.active;

import org.adamalang.runtime.contracts.Key;

import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Consumer;

/** a key binds to a specific thread, and the base holds the executor for that thread along with the memory for that thread */
public class DirectoryBase {
  public final ScheduledExecutorService scheduler;
  private final HashMap<Key, StateMachine> state;

  public DirectoryBase() {
    this.scheduler = Executors.newSingleThreadScheduledExecutor();
    this.state = new HashMap<>();
  }

  /** shutdown all state machines */
  public void shutdown() {
    scheduler.execute(() -> {
      for (StateMachine sm : state.values()) {
        sm.shutdown();
      }
      scheduler.shutdown();
    });
  }

  /** get or create a key in the executor */
  public void getOrCreate(Key key, Consumer<StateMachine> consumer) {
    scheduler.execute(() -> {
      StateMachine sm = state.get(key);
      if (sm == null) {
        sm = new StateMachine(this, key);
        // TODO: register with key tracker to drive the connection management
        state.put(key, sm);
      }
      consumer.accept(sm);
    });
  }
}
