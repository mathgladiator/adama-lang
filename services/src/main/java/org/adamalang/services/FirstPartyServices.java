/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.services;

import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.SimpleExecutor;
import org.adamalang.common.metrics.MetricsFactory;
import org.adamalang.mysql.DataBase;
import org.adamalang.runtime.remote.Service;
import org.adamalang.runtime.remote.ServiceRegistry;
import org.adamalang.services.billing.Stripe;
import org.adamalang.services.email.AmazonSES;
import org.adamalang.services.entropy.SafeRandom;
import org.adamalang.services.sms.Twilio;
import org.adamalang.web.client.WebClientBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FirstPartyServices {
  private static final Logger LOGGER = LoggerFactory.getLogger(FirstPartyServices.class);

  public static void install(SimpleExecutor executor, MetricsFactory factory, DataBase dataBase, WebClientBase webClientBase, String masterKey) {
    FirstPartyMetrics metrics = new FirstPartyMetrics(factory);
    /*
    SelfClient adamaClientRaw = null;
    if (executor != null){
      // TODO: sort out a plan for variuos endpoints... OR make it part of the signature
      MultiWebClientRetryPool pool = new MultiWebClientRetryPool(executor, webClientBase, new MultiWebClientRetryPoolMetrics(factory), new MultiWebClientRetryPoolConfig(new ConfigObject(Json.newJsonObject())), "aws-us-east.adama-platform.com");
      adamaClientRaw = new SelfClient(pool);
    }
    final SelfClient adamaClient = adamaClientRaw;
    */
    ServiceRegistry.add("adama", Adama.class, (space, configRaw) -> { // TODO
      ServiceConfig config = new ServiceConfig(dataBase, space, configRaw, masterKey);
      try {
        return new Adama(metrics, config);
      } catch (ErrorCodeException ex) {
        LOGGER.error("failed-adama", ex);
        return Service.FAILURE;
      }
    });
    ServiceRegistry.add("twilio", Twilio.class, (space, configRaw) -> {
      ServiceConfig config = new ServiceConfig(dataBase, space, configRaw, masterKey);
      try {
        return new Twilio(metrics, config, webClientBase);
      } catch (ErrorCodeException ex) {
        LOGGER.error("failed-twilio", ex);
        return Service.FAILURE;
      }
    });
    ServiceRegistry.add("stripe", Stripe.class, (space, configRaw) -> {
      ServiceConfig config = new ServiceConfig(dataBase, space, configRaw, masterKey);
      try {
        return new Stripe(metrics, config, webClientBase);
      } catch (ErrorCodeException ex) {
        LOGGER.error("failed-stripe", ex);
        return Service.FAILURE;
      }
    });
    ServiceRegistry.add("amazonses", AmazonSES.class, (space, configRaw) -> {
      ServiceConfig config = new ServiceConfig(dataBase, space, configRaw, masterKey);
      try {
        return new AmazonSES(metrics, config, webClientBase);
      } catch (ErrorCodeException ex) {
        LOGGER.error("failed-amazonses", ex);
        return Service.FAILURE;
      }
    });
    ServiceRegistry.add("saferandom", SafeRandom.class, (space, configRaw) -> new SafeRandom());
  }
}
