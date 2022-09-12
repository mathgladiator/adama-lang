/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.extern.stripe;

import org.adamalang.common.ConfigObject;
import org.adamalang.common.Json;
import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.adamalang.web.client.WebClientBase;
import org.adamalang.web.service.WebConfig;
import org.junit.Assume;

import java.io.File;
import java.nio.file.Files;

public class StripeTests {
  @FunctionalInterface
  private static interface StripeFlow {
    void run(Stripe stripe) throws Exception;
  }

  private void flow(StripeFlow body) throws Exception {
    WebClientBase base = new WebClientBase(new WebConfig(new ConfigObject(Json.newJsonObject())));
    try {
      File configFile = new File("./saas/stripe.config.json");
      if (!configFile.exists()) {
        configFile = new File("./stripe.config.json");
      }
      Assume.assumeTrue(configFile.exists());
      ConfigObject co = new ConfigObject(Json.parseJsonObject(Files.readString(configFile.toPath())));
      StripeConfig config = new StripeConfig(co);
      Stripe stripe = new Stripe(base, config, new StripeMetrics(new NoOpMetricsFactory()));
      body.run(stripe);
    } finally {
      base.shutdown();
    }
  }
}
