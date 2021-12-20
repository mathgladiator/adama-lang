package org.adamalang.saas;

import org.adamalang.api.ConnectionNexus;
import org.adamalang.api.ConnectionRouter;
import org.adamalang.impl.RootHandlerImpl;
import org.adamalang.transforms.Authenticator;
import org.adamalang.transforms.SpacePolicyLocator;
import org.adamalang.web.contracts.ServiceBase;
import org.adamalang.web.contracts.ServiceConnection;
import org.adamalang.web.io.Json;
import org.adamalang.web.io.JsonRequest;
import org.adamalang.web.io.JsonResponder;
import org.adamalang.web.service.Config;
import org.adamalang.web.service.ServiceRunnable;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Frontend {
    public static void execute() throws Exception {
        // TODO: search args for --config to pick up a file to use for config
        Config config = new Config(Json.parseJsonObject("{}"));
        ExecutorService executor = Executors.newSingleThreadExecutor();

        // TODO: add the gRPC client here, yay!
        RootHandlerImpl handler = new RootHandlerImpl();

        ServiceBase base = context -> new ServiceConnection() {
            // TODO: pick an executor (randomly? pick two and do the faster of the two?)
            ConnectionNexus nexus = new ConnectionNexus(executor, new Authenticator(), new SpacePolicyLocator());
            ConnectionRouter router = new ConnectionRouter(nexus, handler);
            @Override
            public void execute(JsonRequest request, JsonResponder responder) {
                router.route(request, responder);
            }

            @Override
            public boolean keepalive() {
                return true;
            }

            @Override
            public void kill() {
                router.disconnect();
            }
        };
        final var runnable = new ServiceRunnable(config, base);
        final var thread = new Thread(runnable);
        thread.start();
        runnable.waitForReady(1000);
        thread.join();
    }
}
