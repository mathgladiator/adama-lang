package org.adamalang.gossip;

@FunctionalInterface
public interface ExceptionRunnable {
    public void run() throws Exception;

    public static Runnable TO_RUNTIME(ExceptionRunnable run) {
        return () -> {
            try {
                run.run();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        };
    }
}
