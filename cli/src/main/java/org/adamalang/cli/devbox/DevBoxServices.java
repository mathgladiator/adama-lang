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
package org.adamalang.cli.devbox;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.common.Callback;
import org.adamalang.common.Json;
import org.adamalang.common.SimpleExecutor;
import org.adamalang.common.keys.RSAPemKey;
import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.runtime.remote.Service;
import org.adamalang.runtime.remote.ServiceRegistry;
import org.adamalang.runtime.remote.SimpleService;
import org.adamalang.services.Adama;
import org.adamalang.metrics.FirstPartyMetrics;
import org.adamalang.services.billing.Stripe;
import org.adamalang.services.email.AmazonSES;
import org.adamalang.services.email.SendGrid;
import org.adamalang.services.entropy.Delay;
import org.adamalang.services.entropy.SafeRandom;
import org.adamalang.services.logging.Logzio;
import org.adamalang.services.security.GoogleValidator;
import org.adamalang.services.security.IdentitySigner;
import org.adamalang.services.sms.Twilio;
import org.adamalang.services.social.Discord;
import org.adamalang.services.video.Jitsi;
import org.adamalang.web.client.WebClientBase;

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


  public static class DevBoxLogzio extends SimpleService {
    private final String space;
    private final Consumer<String> logger;

    public DevBoxLogzio(String space, Consumer<String> logger) {
      super("logzio", new NtPrincipal("logzio", "service"), true);
      this.space = space;
      this.logger = logger;
    }

    public static String definition(int uniqueId, String params, HashSet<String> names, Consumer<String> error) {
      return Logzio.definition(uniqueId, params, names, error);
    }

    @Override
    public void request(NtPrincipal who, String method, String request, Callback<String> callback) {
      logger.accept("devservices|service[logzio/ " + space+ "]::" + method + "(" + request + ")");
      callback.success("{}");
    }
  }

  public static class DevBoxSendGrid extends SimpleService {
    private final String space;
    private final Consumer<String> logger;

    public DevBoxSendGrid(String space, Consumer<String> logger) {
      super("sendgrid", new NtPrincipal("sendgrid", "service"), true);
      this.space = space;
      this.logger = logger;
    }

    public static String definition(int uniqueId, String params, HashSet<String> names, Consumer<String> error) {
      return AmazonSES.definition(uniqueId, params, names, error);
    }

    @Override
    public void request(NtPrincipal who, String method, String request, Callback<String> callback) {
      logger.accept("devservices|service[SendGrid/ " + space+ "]::" + method + "(" + request + ")");
      callback.success("{}");
    }
  }

  public static void install(ObjectNode verseDefn, WebClientBase webClientBase, SimpleExecutor executor, Consumer<String> logger) {
    FirstPartyMetrics firstPartyMetrics = new FirstPartyMetrics(new NoOpMetricsFactory());;
    logger.accept("devservices|installing overrides");
    ServiceRegistry.add("amazonses", DevBoxAmazonSES.class, (space, configRaw, keys) -> new DevBoxAmazonSES(space, logger));
    ServiceRegistry.add("logzio", Logzio.class, (space, configRaw, keys) -> new DevBoxLogzio(space, logger));
    ServiceRegistry.add("saferandom", SafeRandom.class, (space, configRaw, keys) -> new SafeRandom(executor));
    ServiceRegistry.add("delay", Delay.class, (space, configRaw, keys) -> new Delay(firstPartyMetrics, executor));
    ObjectNode servicesDefn = Json.readObject(verseDefn, "services");
    if (servicesDefn != null && servicesDefn.has("adama")) {
      // TODO: define how to do the Adama service (it could be a loop back?)
      ServiceRegistry.add("adama", Adama.class, (space, configRaw, keys) -> Service.FAILURE);
    } else {
      ServiceRegistry.add("adama", Adama.class, (space, configRaw, keys) -> Service.FAILURE);
    }
    if (servicesDefn != null && servicesDefn.has("twilio")) {
      // TODO: read object and build the twilio
      ServiceRegistry.add("twilio", Twilio.class, (space, configRaw, keys) -> Service.FAILURE);
    } else {
      ServiceRegistry.add("twilio", Twilio.class, (space, configRaw, keys) -> Service.FAILURE);
    }
    if (servicesDefn != null && servicesDefn.has("discord")) {
      // TODO: read stuff
      ServiceRegistry.add("discord", Discord.class, (space, configRaw, keys) -> Service.FAILURE);
    } else {
      ServiceRegistry.add("discord", Discord.class, (space, configRaw, keys) -> Service.FAILURE);
    }
    if (servicesDefn != null && servicesDefn.has("stripe")) {
      String apiKeyStripe = servicesDefn.get("stripe").textValue();
      ServiceRegistry.add("stripe", Stripe.class, (space, configRaw, keys) -> new Stripe(firstPartyMetrics, webClientBase, apiKeyStripe));
    } else {
      ServiceRegistry.add("stripe", Stripe.class, (space, configRaw, keys) -> Service.FAILURE);
    }
    if (servicesDefn != null && servicesDefn.has("sendgrid")) {
      String apiKeySendGrid = servicesDefn.get("sendgrid").textValue();
      ServiceRegistry.add("sendgrid", SendGrid.class, (space, configRaw, keys) -> new SendGrid(firstPartyMetrics, webClientBase, apiKeySendGrid));
    } else {
      ServiceRegistry.add("sendgrid", SendGrid.class, (space, configRaw, keys) -> new DevBoxSendGrid(space, logger));
    }
    if (servicesDefn != null && servicesDefn.has("identitysigner")) {
      // TODO: have an option  to pull the parameters to find out which private key should be mapped as could have multiple signers
      ServiceRegistry.add("identitysigner", IdentitySigner.class, (space, configRaw, keys) -> Service.FAILURE);
    } else {
      ServiceRegistry.add("identitysigner", IdentitySigner.class, (space, configRaw, keys) -> Service.FAILURE);
    }
    ServiceRegistry.add("googlevalidator", GoogleValidator.class, (space, configRaw, keys) -> GoogleValidator.build(firstPartyMetrics, executor, webClientBase));

    if (servicesDefn != null && servicesDefn.has("jitsi")) {
      String privateKeyDev = servicesDefn.get("jitsi").textValue();
      ServiceRegistry.add("jitsi", Jitsi.class, (space, configRaw, keys) -> {
        try {
          return new Jitsi(firstPartyMetrics, webClientBase, executor, RSAPemKey.privateFrom(privateKeyDev), configRaw.get("sub").toString());
        } catch (Exception ex) {
          ex.printStackTrace();
          return Service.FAILURE;
        }
      });
    } else {
      ServiceRegistry.add("jitsi", Jitsi.class, (space, configRaw, keys) -> Service.FAILURE);
    }
  }
}
