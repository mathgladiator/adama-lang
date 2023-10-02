/*
* Adama Platform and Language
* Copyright (C) 2021 - 2023 by Adama Platform Initiative, LLC
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as published
* by the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Affero General Public License for more details.
* 
* You should have received a copy of the GNU Affero General Public License
* along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
package org.adamalang.services;

import org.adamalang.api.SelfClient;
import org.adamalang.common.ConfigObject;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.Json;
import org.adamalang.common.SimpleExecutor;
import org.adamalang.common.metrics.MetricsFactory;
import org.adamalang.internal.InternalSigner;
import org.adamalang.mysql.DataBase;
import org.adamalang.runtime.remote.Service;
import org.adamalang.runtime.remote.ServiceRegistry;
import org.adamalang.services.billing.Stripe;
import org.adamalang.services.email.AmazonSES;
import org.adamalang.services.email.SendGrid;
import org.adamalang.services.entropy.SafeRandom;
import org.adamalang.services.security.GoogleValidator;
import org.adamalang.services.security.IdentitySigner;
import org.adamalang.services.sms.Twilio;
import org.adamalang.services.social.Discord;
import org.adamalang.web.client.WebClientBase;
import org.adamalang.web.client.socket.ConnectionReady;
import org.adamalang.web.client.socket.MultiWebClientRetryPool;
import org.adamalang.web.client.socket.MultiWebClientRetryPoolConfig;
import org.adamalang.web.client.socket.MultiWebClientRetryPoolMetrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** These are the first party services; please keep in sync with org.adamalang.cli.devbox.DevBoxServices */
public class FirstPartyServices {
  private static final Logger LOGGER = LoggerFactory.getLogger(FirstPartyServices.class);

  public static void install(SimpleExecutor executor, MetricsFactory factory, WebClientBase webClientBase, SelfClient adamaClientRaw, InternalSigner signer) {
    FirstPartyMetrics metrics = new FirstPartyMetrics(factory);
    final SelfClient adamaClient = adamaClientRaw;
    ServiceRegistry.add("adama", Adama.class, (space, configRaw, keys) -> { // TODO
      ServiceConfig config = new ServiceConfig(space, configRaw, keys);
      try {
        return new Adama(metrics, adamaClient, signer, config);
      } catch (ErrorCodeException ex) {
        LOGGER.error("failed-adama", ex);
        return Service.FAILURE;
      }
    });
    ServiceRegistry.add("twilio", Twilio.class, (space, configRaw, keys) -> {
      ServiceConfig config = new ServiceConfig(space, configRaw, keys);
      try {
        return Twilio.build(metrics, config, webClientBase);
      } catch (ErrorCodeException ex) {
        LOGGER.error("failed-twilio", ex);
        return Service.FAILURE;
      }
    });
    ServiceRegistry.add("stripe", Stripe.class, (space, configRaw, keys) -> {
      ServiceConfig config = new ServiceConfig(space, configRaw, keys);
      try {
        return Stripe.build(metrics, config, webClientBase);
      } catch (ErrorCodeException ex) {
        LOGGER.error("failed-stripe", ex);
        return Service.FAILURE;
      }
    });
    ServiceRegistry.add("amazonses", AmazonSES.class, (space, configRaw, keys) -> {
      ServiceConfig config = new ServiceConfig(space, configRaw, keys);
      try {
        return AmazonSES.build(metrics, config, webClientBase);
      } catch (ErrorCodeException ex) {
        LOGGER.error("failed-amazonses", ex);
        return Service.FAILURE;
      }
    });
    ServiceRegistry.add("sendgrid", SendGrid.class, (space, configRaw, keys) -> {
      ServiceConfig config = new ServiceConfig(space, configRaw, keys);
      try {
        return SendGrid.build(metrics, config, webClientBase);
      } catch (ErrorCodeException ex) {
        LOGGER.error("failed-sendgrid", ex);
        return Service.FAILURE;
      }
    });
    ServiceRegistry.add("discord", Discord.class, (space, configRaw, keys) -> {
      ServiceConfig config = new ServiceConfig(space, configRaw, keys);
      try {
        return Discord.build(metrics, config, webClientBase);
      } catch (ErrorCodeException ex) {
        LOGGER.error("failed-discord", ex);
        return Service.FAILURE;
      }
    });
    ServiceRegistry.add("identitysigner", IdentitySigner.class, (space, configRaw, keys) -> {
      ServiceConfig config = new ServiceConfig(space, configRaw, keys);
      try {
        return IdentitySigner.build(metrics, config, executor);
      } catch (ErrorCodeException ex) {
        LOGGER.error("failed-identitysigner", ex);
        return Service.FAILURE;
      }
    });
    ServiceRegistry.add("googlevalidator", GoogleValidator.class, (space, configRaw, keys) -> GoogleValidator.build(metrics, executor, webClientBase));
    ServiceRegistry.add("saferandom", SafeRandom.class, (space, configRaw, keys) -> new SafeRandom(executor));
  }
}
