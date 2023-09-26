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
package org.adamalang.services.email;

import org.adamalang.ErrorCodes;
import org.adamalang.aws.Credential;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.runtime.remote.SimpleService;
import org.adamalang.services.FirstPartyMetrics;
import org.adamalang.services.ServiceConfig;
import org.adamalang.web.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.TreeMap;
import java.util.function.Consumer;

public class SendGrid extends SimpleService {
  private static final Logger LOGGER = LoggerFactory.getLogger(AmazonSES.class);
  private final FirstPartyMetrics metrics;
  private final WebClientBase base;
  private final String apiKey;

  public SendGrid(FirstPartyMetrics metrics, WebClientBase base, String apiKey) {
    super("sendgrid", new NtPrincipal("sendgrid", "service"), true);
    this.metrics = metrics;
    this.base = base;
    this.apiKey = apiKey;
  }

  public static SendGrid build(FirstPartyMetrics metrics, ServiceConfig config, WebClientBase base) throws ErrorCodeException {
    String apiKey = config.getDecryptedSecret("api_key");
    return new SendGrid(metrics, base, apiKey);
  }

  public static String definition(int uniqueId, String params, HashSet<String> names, Consumer<String> error) {
    StringBuilder sb = new StringBuilder();
    sb.append("service sendgrid {\n");
    sb.append("  class=\"sendgrid\";\n");
    sb.append("  ").append(params).append("\n");
    if (!names.contains("api_key")) {
      error.accept("sendgrid requires an 'api_key' field (and it should be encrypted)");
    }
    sb.append("  method<dynamic, dynamic> sendMail;\n");
    sb.append("}\n");
    return sb.toString();
  }

  @Override
  public void request(NtPrincipal who, String method, String request, Callback<String> callback) {
    switch (method) {
      case "sendMail": {
        TreeMap<String, String> headers = new TreeMap<>();
        headers.put("Authorization", "Bearer " + apiKey);
        headers.put("Content-Type", "application/json");
        SimpleHttpRequest get = new SimpleHttpRequest("POST", "https://api.sendgrid.com/v3/mail/send", headers, SimpleHttpRequestBody.WRAP(request.getBytes(StandardCharsets.UTF_8)));
        base.execute(get, new VoidCallbackHttpResponder(LOGGER, metrics.sendgrid_sendmail.start(), new Callback<>() {
          @Override
          public void success(Void value) {
            callback.success("{}");
          }

          @Override
          public void failure(ErrorCodeException ex) {
            callback.failure(ex);
          }
        }));
        return;
      }
      default:
        callback.failure(new ErrorCodeException(ErrorCodes.FIRST_PARTY_SERVICES_METHOD_NOT_FOUND));
    }
  }
}
