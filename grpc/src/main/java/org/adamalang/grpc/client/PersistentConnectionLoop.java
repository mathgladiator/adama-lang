package org.adamalang.grpc.client;

import java.util.concurrent.ScheduledExecutorService;

public class PersistentConnectionLoop {
    private final ScheduledExecutorService service;
    private boolean alive;
    private int backoffMilliseconds;
    private ClientState state;

    public PersistentConnectionLoop(ScheduledExecutorService service, ClientState state) {
        this.service = service;
        this.alive = false;
        this.backoffMilliseconds = 0;
        this.state = state;
    }

    public void start() {
        service.execute(() -> {
            alive = true;
            attempt();
        });
    }

    public void stop() {
        service.execute(() -> {
            alive = false;
            // TODO: close the active connection
        });
    }

    private void attempt() {
        // try to make a connection
    }
}
