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
import org.adamalang.runtime.remote.Service;
import org.adamalang.runtime.remote.ServiceConfig;
import org.adamalang.runtime.remote.ServiceRegistry;
import org.adamalang.services.Adama;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** These are the first party services; please keep in sync with org.adamalang.cli.devbox.DevBoxServices */

public class CoreServices {
  private static final Logger LOGGER = LoggerFactory.getLogger(CoreServices.class);

  public static void install(CoreServicesNexus nexus) {
    final SelfClient adamaClient = nexus.adamaClientRaw;
    ServiceRegistry.add("adama", Adama.class, (space, configRaw, keys) -> { // TODO
      try {
        ServiceConfig config = nexus.serviceConfigFactory.cons("adama", space, configRaw, keys);
        return new Adama(nexus.fpMetrics, adamaClient, nexus.signer, config);
      } catch (ErrorCodeException ex) {
        return Service.FAILURE;
      }
    });
    ServiceRegistry.add("twilio", Twilio.class, (space, configRaw, keys) -> {
      try {
        ServiceConfig config = nexus.serviceConfigFactory.cons("twilio", space, configRaw, keys);
        return Twilio.build(nexus.fpMetrics, config, nexus.webClientBase);
      } catch (ErrorCodeException ex) {
        return Service.FAILURE;
      }
    });
    ServiceRegistry.add("stripe", Stripe.class, (space, configRaw, keys) -> {
      try {
        ServiceConfig config = nexus.serviceConfigFactory.cons("stripe", space, configRaw, keys);
        return Stripe.build(nexus.fpMetrics, config, nexus.webClientBase);
      } catch (ErrorCodeException ex) {
        return Service.FAILURE;
      }
    });
    ServiceRegistry.add("amazonses", AmazonSES.class, (space, configRaw, keys) -> {
      try {
        ServiceConfig config = nexus.serviceConfigFactory.cons("amazonses", space, configRaw, keys);
        return AmazonSES.build(nexus.fpMetrics, config, nexus.webClientBase);
      } catch (ErrorCodeException ex) {
        return Service.FAILURE;
      }
    });
    ServiceRegistry.add("sendgrid", SendGrid.class, (space, configRaw, keys) -> {
      try {
        ServiceConfig config = nexus.serviceConfigFactory.cons("sendgrid", space, configRaw, keys);
        return SendGrid.build(nexus.fpMetrics, config, nexus.webClientBase);
      } catch (ErrorCodeException ex) {
        return Service.FAILURE;
      }
    });
    ServiceRegistry.add("discord", Discord.class, (space, configRaw, keys) -> {
      try {
        ServiceConfig config = nexus.serviceConfigFactory.cons("discord", space, configRaw, keys);
        return Discord.build(nexus.fpMetrics, config, nexus.webClientBase);
      } catch (ErrorCodeException ex) {
        return Service.FAILURE;
      }
    });
    ServiceRegistry.add("identitysigner", IdentitySigner.class, (space, configRaw, keys) -> {
      try {
        ServiceConfig config = nexus.serviceConfigFactory.cons("identitysigner", space, configRaw, keys);
        return IdentitySigner.build(nexus.fpMetrics, config, nexus.executor);
      } catch (ErrorCodeException ex) {
        return Service.FAILURE;
      }
    });
    ServiceRegistry.add("logzio", Logzio.class, (space, configRaw, keys) -> {
      try {
        ServiceConfig config = nexus.serviceConfigFactory.cons("logzio", space, configRaw, keys);
        return Logzio.build(nexus.fpMetrics, nexus.webClientBase, config, nexus.executor);
      } catch (ErrorCodeException ex) {
        return Service.FAILURE;
      }
    });
    ServiceRegistry.add("jitsi", Jitsi.class, (space, configRaw, keys) -> {
      try {
        ServiceConfig config = nexus.serviceConfigFactory.cons("jitsi", space, configRaw, keys);
        return Jitsi.build(nexus.fpMetrics, config, nexus.webClientBase, nexus.offload);
      } catch (Exception ex) {
        return Service.FAILURE;
      }
    });
    ServiceRegistry.add("httpjson", SimpleHttpJson.class, (space, configRaw, keys) -> {
      try {
        ServiceConfig config = nexus.serviceConfigFactory.cons("httpjson", space, configRaw, keys);
        return SimpleHttpJson.build(nexus.tpMetrics, nexus.webClientBase, config);
      } catch (Exception ex) {
        return Service.FAILURE;
      }
    });
    /*
    ServiceRegistry.add("facebookvalidator", FacebookValidator.class, (space, configRaw, keys) -> FacebookValidator.build(metrics, webClientBase));
    ServiceRegistry.add("twittervalidator", TwitterValidator.class, (space, configRaw, keys) -> TwitterValidator.build(metrics, webClientBase));
    ServiceRegistry.add("githubvalidator", GithubValidator.class, (space, configRaw, keys) -> GithubValidator.build(metrics, webClientBase));
     */
    ServiceRegistry.add("googlevalidator", GoogleValidator.class, (space, configRaw, keys) -> GoogleValidator.build(nexus.fpMetrics, nexus.executor, nexus.webClientBase));
    ServiceRegistry.add("saferandom", SafeRandom.class, (space, configRaw, keys) -> new SafeRandom(nexus.offload));
    // Push has an expectation that it will installed isolated due to the sheer number of dependencies
    ServiceRegistry.add("push", Push.class, (space, configRaw, keys) -> new Push(nexus.fpMetrics, new NoOpPusher()));
    ServiceRegistry.add("delay", Delay.class, (space, configRaw, keys) -> new Delay(nexus.fpMetrics, nexus.executor));
  }
}
