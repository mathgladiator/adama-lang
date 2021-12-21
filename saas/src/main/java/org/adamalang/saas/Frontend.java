package org.adamalang.saas;

import org.adamalang.api.ConnectionNexus;
import org.adamalang.api.ConnectionRouter;
import org.adamalang.extern.ExternNexus;
import org.adamalang.frontend.RootHandlerImpl;
import org.adamalang.transforms.Authenticator;
import org.adamalang.transforms.SpacePolicyLocator;
import org.adamalang.transforms.UserIdResolver;
import org.adamalang.web.contracts.ServiceBase;
import org.adamalang.web.contracts.ServiceConnection;
import org.adamalang.web.io.Json;
import org.adamalang.web.io.JsonRequest;
import org.adamalang.web.io.JsonResponder;
import org.adamalang.web.service.WebConfig;
import org.adamalang.web.service.ServiceRunnable;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Frontend {
    public static ServiceBase makeService(ExternNexus extern) throws Exception {
        // TODO: make multiple of these
        ExecutorService executor = Executors.newSingleThreadExecutor();
        RootHandlerImpl handler = new RootHandlerImpl(extern);
        SpacePolicyLocator spacePolicyLocator = new SpacePolicyLocator(Executors.newSingleThreadExecutor(), extern);
        UserIdResolver userIdResolver = new UserIdResolver(Executors.newSingleThreadExecutor(), extern);

        return context -> new ServiceConnection() {
            // TODO: pick an executor (randomly? pick two and do the faster of the two?)
            ConnectionNexus nexus = new ConnectionNexus(executor, //
                    userIdResolver, //
                    new Authenticator(extern), //
                    spacePolicyLocator); //
            ConnectionRouter router = new ConnectionRouter(nexus, handler);
            @Override
            public void execute(JsonRequest request, JsonResponder responder) {
                router.route(request, responder);
            }

            @Override
            public boolean keepalive() {
                // TODO: check with the nexus for some activity
                // TODO: rule #1: there should be activity within the first 30 seconds of a connection
                // TODO: rule #2: there should be activity within the last 5 minutes
                return true;
            }

            @Override
            public void kill() {
                router.disconnect();
            }
        };
    }

    public static void execute(ExternNexus extern, String config) throws Exception {
        WebConfig webConfig = new WebConfig(Json.parseJsonObject(config));
        ServiceBase serviceBase = makeService(extern);
        final var runnable = new ServiceRunnable(webConfig, serviceBase);
        final var thread = new Thread(runnable);
        thread.start();
        runnable.waitForReady(1000);
        thread.join();
    }
}
