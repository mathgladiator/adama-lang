/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.cli.commands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.cli.Config;
import org.adamalang.cli.Util;
import org.adamalang.cli.remote.Connection;
import org.adamalang.cli.remote.WebSocketClient;
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

import java.io.File;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class Account {
  private static final Logger LOGGER = LoggerFactory.getLogger(Account.class);
  public static void execute(Config config, String[] args) throws Exception {
    if (args.length == 0) {
      accountHelp();
      return;
    }
    String command = Util.normalize(args[0]);
    String[] next = Util.tail(args);
    switch (command) {
      case "set-password":
        accountSetPassword(config, args);
        return;
      case "test-gtoken":
        testGoogleToken(config, args);
        return;
      case "help":
        accountHelp();
        return;
    }
  }

  public static void testGoogleToken(Config config, String[] args) throws Exception {
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

  public static void accountSetPassword(Config config, String[] args) throws Exception {
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

  private static File ensureFileDoesNotExist(String filename) throws Exception {
    File file = new File(filename);
    if (file.exists()) {
      throw new Exception(filename + " already exists, refusing to create");
    }
    return file;
  }

  public static void accountHelp() {
    System.out.println(Util.prefix("Manage your account within the Platform.", Util.ANSI.Green));
    System.out.println();
    System.out.println(Util.prefix("USAGE:", Util.ANSI.Yellow));
    System.out.println("    " + Util.prefix("adama account", Util.ANSI.Green) + " " + Util.prefix("[ACCOUNTSUBCOMMAND]", Util.ANSI.Magenta));
    System.out.println();
    System.out.println(Util.prefix("FLAGS:", Util.ANSI.Yellow));
    System.out.println("    " + Util.prefix("--config", Util.ANSI.Green) + "          Supplies a config file path other than the default (~/.adama)");
    System.out.println();
    System.out.println(Util.prefix("ACCOUNTSUBCOMMAND:", Util.ANSI.Yellow));
    System.out.println("    " + Util.prefix("set-password", Util.ANSI.Green) + "      Create a password to be used on web");
    System.out.println("    " + Util.prefix("test-gtoken", Util.ANSI.Green) + "       Test a google token converts to an email");

  }
}
