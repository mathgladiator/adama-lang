/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.cli.devbox;

import org.adamalang.common.Callback;
import org.adamalang.common.SimpleExecutor;
import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.runtime.remote.ServiceRegistry;
import org.adamalang.runtime.remote.SimpleService;
import org.adamalang.services.FirstPartyServices;
import org.adamalang.services.email.AmazonSES;
import org.adamalang.services.entropy.SafeRandom;

import java.util.HashSet;
import java.util.function.Consumer;

/** services for the devbox; this don't help test the services, but they provide a great experience for developers */
public class DevBoxServices {

  public static class DevBoxAmazonSES extends SimpleService {
    private final String space;
    private final Consumer<String> logger;

    public DevBoxAmazonSES(String space, Consumer<String> logger) {
      super("amazonses", new NtPrincipal("amazonses", "service"), true);
      this.space = space;
      this.logger = logger;
    }

    public static String definition(int uniqueId, String params, HashSet<String> names, Consumer<String> error) {
      return AmazonSES.definition(uniqueId, params, names, error);
    }

    @Override
    public void request(NtPrincipal who, String method, String request, Callback<String> callback) {
      logger.accept("devservices|service[AmazonSES/ " + space+ "]::" + method + "(" + request + ")");
      callback.success("{}");
    }
  }

  public static void install(SimpleExecutor executor, Consumer<String> logger) {
    FirstPartyServices.install(null, new NoOpMetricsFactory(), null, null, null);
    logger.accept("devservices|installing overrides");
    ServiceRegistry.add("amazonses", DevBoxAmazonSES.class, (space, configRaw, keys) -> new DevBoxAmazonSES(space, logger));
    ServiceRegistry.add("saferandom", SafeRandom.class, (space, configRaw, keys) -> new SafeRandom(executor));
    // TODO: Stripe?
  }
}
