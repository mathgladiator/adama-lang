package org.adamalang.runtime.threads;

import java.util.concurrent.ThreadFactory;

/** name a thread on creation; this enables executors to be named in a meaningful way */
public class NamedThreadFactory implements ThreadFactory {

    private final String name;

    public NamedThreadFactory(String name) {
        this.name = name;
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread thread = new Thread(r, name);
        thread.setDaemon(true);
        return thread;
    }
}
