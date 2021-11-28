package org.adamalang.runtime.contracts;

import java.util.concurrent.CountDownLatch;

/** wraps Java executor for time and simplifies for Adama */
public interface SimpleExecutor {

    /** execute the given command in the executor */
    void execute(Runnable command);

    /** schedule the given command to run after some milliseconds within the executor */
    void schedule(Key key, Runnable command, long milliseconds);

    /** shutdown the executor */
    CountDownLatch shutdown();

    /** a default instance for doing things NOW */
    public static final SimpleExecutor NOW = new SimpleExecutor() {
        @Override
        public void execute(Runnable command) {
            command.run();
        }

        @Override
        public void schedule(Key key, Runnable command, long milliseconds) {
            // no-op
        }

        @Override
        public CountDownLatch shutdown() {
            return new CountDownLatch(0);
        }
    };
}
