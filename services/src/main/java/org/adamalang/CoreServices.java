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
package org.adamalang;

import org.adamalang.api.SelfClient;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.SimpleExecutor;
import org.adamalang.common.metrics.MetricsFactory;
import org.adamalang.internal.InternalSigner;
import org.adamalang.metrics.FirstPartyMetrics;
import org.adamalang.metrics.ThirdPartyMetrics;
import org.adamalang.runtime.remote.Service;
import org.adamalang.runtime.remote.ServiceRegistry;
import org.adamalang.services.Adama;
import org.adamalang.services.ServiceConfig;
import org.adamalang.services.billing.Stripe;
import org.adamalang.services.email.AmazonSES;
import org.adamalang.services.email.SendGrid;
import org.adamalang.services.entropy.Delay;
import org.adamalang.services.entropy.SafeRandom;
import org.adamalang.services.external.SimpleHttpJson;
import org.adamalang.services.logging.Logzio;
import org.adamalang.services.push.NoOpPusher;
import org.adamalang.services.push.Push;
import org.adamalang.services.security.GoogleValidator;
import org.adamalang.services.security.IdentitySigner;
import org.adamalang.services.sms.Twilio;
import org.adamalang.services.social.Discord;
import org.adamalang.services.video.Jitsi;
import org.adamalang.web.client.WebClientBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** These are the first party services; please keep in sync with org.adamalang.cli.devbox.DevBoxServices */

public class CoreServices {
  private static final Logger LOGGER = LoggerFactory.getLogger(CoreServices.class);

  public static FirstPartyMetrics install(SimpleExecutor executor, SimpleExecutor offload, MetricsFactory factory, WebClientBase webClientBase, SelfClient adamaClientRaw, InternalSigner signer) {
    FirstPartyMetrics fpMetrics = new FirstPartyMetrics(factory);
    ThirdPartyMetrics tpMetrics = new ThirdPartyMetrics(factory);

    final SelfClient adamaClient = adamaClientRaw;
    ServiceRegistry.add("adama", Adama.class, (space, configRaw, keys) -> { // TODO
      ServiceConfig config = new ServiceConfig(space, configRaw, keys);
      try {
        return new Adama(fpMetrics, adamaClient, signer, config);
      } catch (ErrorCodeException ex) {
        LOGGER.error("failed-adama", ex);
        return Service.FAILURE;
      }
    });
    ServiceRegistry.add("twilio", Twilio.class, (space, configRaw, keys) -> {
      ServiceConfig config = new ServiceConfig(space, configRaw, keys);
      try {
        return Twilio.build(fpMetrics, config, webClientBase);
      } catch (ErrorCodeException ex) {
        LOGGER.error("failed-twilio", ex);
        return Service.FAILURE;
      }
    });
    ServiceRegistry.add("stripe", Stripe.class, (space, configRaw, keys) -> {
      ServiceConfig config = new ServiceConfig(space, configRaw, keys);
      try {
        return Stripe.build(fpMetrics, config, webClientBase);
      } catch (ErrorCodeException ex) {
        LOGGER.error("failed-stripe", ex);
        return Service.FAILURE;
      }
    });
    ServiceRegistry.add("amazonses", AmazonSES.class, (space, configRaw, keys) -> {
      ServiceConfig config = new ServiceConfig(space, configRaw, keys);
      try {
        return AmazonSES.build(fpMetrics, config, webClientBase);
      } catch (ErrorCodeException ex) {
        LOGGER.error("failed-amazonses", ex);
        return Service.FAILURE;
      }
    });
    ServiceRegistry.add("sendgrid", SendGrid.class, (space, configRaw, keys) -> {
      ServiceConfig config = new ServiceConfig(space, configRaw, keys);
      try {
        return SendGrid.build(fpMetrics, config, webClientBase);
      } catch (ErrorCodeException ex) {
        LOGGER.error("failed-sendgrid", ex);
        return Service.FAILURE;
      }
    });
    ServiceRegistry.add("discord", Discord.class, (space, configRaw, keys) -> {
      ServiceConfig config = new ServiceConfig(space, configRaw, keys);
      try {
        return Discord.build(fpMetrics, config, webClientBase);
      } catch (ErrorCodeException ex) {
        LOGGER.error("failed-discord", ex);
        return Service.FAILURE;
      }
    });
    ServiceRegistry.add("identitysigner", IdentitySigner.class, (space, configRaw, keys) -> {
      ServiceConfig config = new ServiceConfig(space, configRaw, keys);
      try {
        return IdentitySigner.build(fpMetrics, config, executor);
      } catch (ErrorCodeException ex) {
        LOGGER.error("failed-identitysigner", ex);
        return Service.FAILURE;
      }
    });
    ServiceRegistry.add("logzio", Logzio.class, (space, configRaw, keys) -> {
      ServiceConfig config = new ServiceConfig(space, configRaw, keys);
      try {
        return Logzio.build(fpMetrics, webClientBase, config, executor);
      } catch (ErrorCodeException ex) {
        LOGGER.error("failed-logzio", ex);
        return Service.FAILURE;
      }
    });
    ServiceRegistry.add("jitsi", Jitsi.class, (space, configRaw, keys) -> {
      ServiceConfig config = new ServiceConfig(space, configRaw, keys);
      try {
        return Jitsi.build(fpMetrics, config, webClientBase, offload);
      } catch (Exception ex) {
        LOGGER.error("failed-jitsi", ex);
        return Service.FAILURE;
      }
    });
    ServiceRegistry.add("httpjson", SimpleHttpJson.class, (space, configRaw, keys) -> {
      ServiceConfig config = new ServiceConfig(space, configRaw, keys);
      try {
        return SimpleHttpJson.build(tpMetrics, webClientBase, config);
      } catch (Exception ex) {
        LOGGER.error("failed-http", ex);
        return Service.FAILURE;
      }
    });
    /*
    ServiceRegistry.add("facebookvalidator", FacebookValidator.class, (space, configRaw, keys) -> FacebookValidator.build(metrics, webClientBase));
    ServiceRegistry.add("twittervalidator", TwitterValidator.class, (space, configRaw, keys) -> TwitterValidator.build(metrics, webClientBase));
    ServiceRegistry.add("githubvalidator", GithubValidator.class, (space, configRaw, keys) -> GithubValidator.build(metrics, webClientBase));
     */
    ServiceRegistry.add("googlevalidator", GoogleValidator.class, (space, configRaw, keys) -> GoogleValidator.build(fpMetrics, executor, webClientBase));
    ServiceRegistry.add("saferandom", SafeRandom.class, (space, configRaw, keys) -> new SafeRandom(offload));
    ServiceRegistry.add("push", Push.class, (space, configRaw, keys) -> new Push(fpMetrics, new NoOpPusher()));
    ServiceRegistry.add("delay", Delay.class, (space, configRaw, keys) -> new Delay(fpMetrics, executor));
    return fpMetrics;
  }
}
