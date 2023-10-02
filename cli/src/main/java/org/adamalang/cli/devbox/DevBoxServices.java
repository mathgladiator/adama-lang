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
import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.runtime.remote.Service;
import org.adamalang.runtime.remote.ServiceRegistry;
import org.adamalang.runtime.remote.SimpleService;
import org.adamalang.services.FirstPartyMetrics;
import org.adamalang.services.FirstPartyServices;
import org.adamalang.services.billing.Stripe;
import org.adamalang.services.email.AmazonSES;
import org.adamalang.services.email.SendGrid;
import org.adamalang.services.entropy.SafeRandom;
import org.adamalang.services.security.GoogleValidator;
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

  public static void install(ObjectNode verseDefn, WebClientBase webClientBase, SimpleExecutor executor, Consumer<String> logger) {
    // TODO: remove this line and just socially enforce the bindings
    FirstPartyServices.install(null, new NoOpMetricsFactory(), null, null, null);
    logger.accept("devservices|installing overrides");
    ServiceRegistry.add("amazonses", DevBoxAmazonSES.class, (space, configRaw, keys) -> new DevBoxAmazonSES(space, logger));
    ServiceRegistry.add("saferandom", SafeRandom.class, (space, configRaw, keys) -> new SafeRandom(executor));
    ObjectNode servicesDefn = Json.readObject(verseDefn, "services");
    if (servicesDefn != null && servicesDefn.has("stripe")) {
      String apiKeyStripe = servicesDefn.get("stripe").textValue();
      ServiceRegistry.add("stripe", Stripe.class, (space, configRaw, keys) -> new Stripe(new FirstPartyMetrics(new NoOpMetricsFactory()), webClientBase, apiKeyStripe));
    }
    if (servicesDefn != null && servicesDefn.has("sendgrid")) {
      String apiKeySendGrid = servicesDefn.get("sendgrid").textValue();
      ServiceRegistry.add("sendgrid", SendGrid.class, (space, configRaw, keys) -> new SendGrid(new FirstPartyMetrics(new NoOpMetricsFactory()), webClientBase, apiKeySendGrid));
    }
    ServiceRegistry.add("googlevalidator", GoogleValidator.class, (space, configRaw, keys) -> GoogleValidator.build(new FirstPartyMetrics(new NoOpMetricsFactory()), executor, webClientBase));
  }
}
