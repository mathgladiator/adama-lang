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
import org.adamalang.CoreServices;
import org.adamalang.CoreServicesNexus;
import org.adamalang.common.Callback;
import org.adamalang.common.Json;
import org.adamalang.common.SimpleExecutor;
import org.adamalang.common.keys.PrivateKeyBundle;
import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.adamalang.devbox.DevBoxServiceConfig;
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.runtime.remote.*;
import org.adamalang.services.email.AmazonSES;
import org.adamalang.services.email.SendGrid;
import org.adamalang.services.logging.Logzio;
import org.adamalang.web.client.WebClientBase;

import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeMap;
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

  public static class DevBoxServiceConfigFactory implements ServiceConfigFactory {
    private final ObjectNode services;
    private final Consumer<String> logger;

    public DevBoxServiceConfigFactory(ObjectNode services, Consumer<String> logger) {
      this.services = services;
      this.logger = logger;
    }

    @Override
    public ServiceConfig cons(String service, String space, HashMap<String, Object> params, TreeMap<Integer, PrivateKeyBundle> keys) {
      ObjectNode secrets = Json.readObject(services, service);
      if (secrets == null) {
        secrets = Json.newJsonObject();
      }
      return new DevBoxServiceConfig(space, params, secrets, service, logger);
    }
  }

  public static void install(ObjectNode verseDefn, WebClientBase webClientBase, SimpleExecutor executor, Consumer<String> logger) {
    ObjectNode servicesDefn = Json.readObject(verseDefn, "services");
    if (servicesDefn == null) {
      logger.accept("devservices|no service secrets");
      servicesDefn = Json.newJsonObject();
    }
    logger.accept("devservices|installing services");
    CoreServicesNexus coreServicesNexus = new CoreServicesNexus(executor, executor, new NoOpMetricsFactory(), webClientBase, null, null, new DevBoxServiceConfigFactory(servicesDefn, logger));
    CoreServices.install(coreServicesNexus);
    logger.accept("devservices|installing overrides");
    if (!servicesDefn.has("amazonses")) {
      ServiceRegistry.add("amazonses", DevBoxAmazonSES.class, (space, configRaw, keys) -> new DevBoxAmazonSES(space, logger));
    }
    if (!servicesDefn.has("logzio")) {
      ServiceRegistry.add("logzio", Logzio.class, (space, configRaw, keys) -> new DevBoxLogzio(space, logger));
    }
    if (!servicesDefn.has("sendgrid")) {
      ServiceRegistry.add("sendgrid", SendGrid.class, (space, configRaw, keys) -> new DevBoxSendGrid(space, logger));
    }
    logger.accept("devservices|finished installing");
  }
}
