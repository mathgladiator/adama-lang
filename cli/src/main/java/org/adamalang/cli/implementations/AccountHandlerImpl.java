/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.cli.implementations;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.cli.Config;
import org.adamalang.cli.commands.Account;
import org.adamalang.cli.remote.Connection;
import org.adamalang.cli.remote.WebSocketClient;
import org.adamalang.cli.router.AccountHandler;
import org.adamalang.cli.router.Arguments;
import org.adamalang.cli.runtime.Output;
import org.adamalang.common.Callback;
import org.adamalang.common.ConfigObject;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.Json;
import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.adamalang.web.client.SimpleHttpRequest;
import org.adamalang.web.client.SimpleHttpRequestBody;
import org.adamalang.web.client.StringCallbackHttpResponder;
import org.adamalang.web.client.WebClientBase;
import org.adamalang.web.service.WebConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class AccountHandlerImpl implements AccountHandler {
    @Override
    public void setPassword(Arguments.AccountSetPasswordArgs args, Output.YesOrError output) throws Exception {
        Config config = args.config;
        System.out.print("Password:");
        String password = new String(System.console().readPassword());
        String identity = config.get_string("identity", null);
        try (WebSocketClient client = new WebSocketClient(config)) {
            try (Connection connection = client.open()) {
                ObjectNode request = Json.newJsonObject();
                request.put("method", "account/set-password");
                request.put("identity", identity);
                request.put("password", password);
                ObjectNode response = connection.execute(request);
                System.err.println(response.toPrettyString());
            }
        }
    }

    @Override
    public void testGtoken(Arguments.AccountTestGtokenArgs args, Output.YesOrError output) throws Exception {
        Logger LOGGER = LoggerFactory.getLogger(Account.class);
        System.out.print("Token:");
        String token = new String(System.console().readPassword());
        WebClientBase base = new WebClientBase(new WebConfig(new ConfigObject(Json.newJsonObject())));
        try {
            HashMap<String, String> headers = new HashMap<>();
            headers.put("Authorization", "Bearer " + token);
            CountDownLatch timeout = new CountDownLatch(1);
            SimpleHttpRequest get = new SimpleHttpRequest("GET", "https://www.googleapis.com/oauth2/v1/userinfo", headers, SimpleHttpRequestBody.EMPTY);
            base.execute(get, new StringCallbackHttpResponder(LOGGER, new NoOpMetricsFactory().makeRequestResponseMonitor("x").start(), new Callback<>() {
                @Override
                public void success(String value) {
                    System.err.println("Success:" + value);
                    timeout.countDown();
                }

                @Override
                public void failure(ErrorCodeException ex) {
                    System.err.println("Failure:" + ex.code);
                    System.err.println("       :" + ex.getMessage());
                    if (ex.getCause() != null) {
                        ex.printStackTrace();
                    }
                }
            }));
            timeout.await(1250, TimeUnit.MILLISECONDS);
        } finally {
            base.shutdown();
        }
    }
}
