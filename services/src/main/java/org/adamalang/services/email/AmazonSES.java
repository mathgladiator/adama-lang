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

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.ErrorCodes;
import org.adamalang.aws.Credential;
import org.adamalang.aws.SignatureV4;
import org.adamalang.common.*;
import org.adamalang.common.metrics.RequestResponseMonitor;
import org.adamalang.metrics.FirstPartyMetrics;
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.runtime.remote.ServiceConfig;
import org.adamalang.runtime.remote.SimpleService;
import org.adamalang.web.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.function.Consumer;

public class AmazonSES extends SimpleService {
  private static final Logger LOGGER = LoggerFactory.getLogger(AmazonSES.class);
  private final FirstPartyMetrics metrics;
  private final WebClientBase base;
  private final String region;
  private final Credential credential;

  public AmazonSES(FirstPartyMetrics metrics, WebClientBase base, Credential credential, String region) {
    super("amazonses", new NtPrincipal("amazonses", "service"), true);
    this.metrics = metrics;
    this.credential = credential;
    this.region = region;
    this.base = base;
  }

  public static AmazonSES build(FirstPartyMetrics metrics, ServiceConfig config, WebClientBase base) throws ErrorCodeException {
    Credential credential = new Credential(config.getDecryptedSecret("access_id"), config.getDecryptedSecret("secret_key"));
    String region = config.getString("region", "us-east-2");
    return new AmazonSES(metrics, base, credential, region);
  }

  public static String definition(int uniqueId, String params, HashSet<String> names, Consumer<String> error) {
    StringBuilder sb = new StringBuilder();
    sb.append("message _AWSSES_SendRequest").append(" { string from; string replyTo; string to; string subject; string text; string html; }\n");
    sb.append("message _AWSSES_SendResponse").append(" { }\n");
    sb.append("service amazonses {\n");
    sb.append("  class=\"amazonses\";\n");
    sb.append("  ").append(params).append("\n");
    if (!names.contains("access_id")) {
      error.accept("amazonses requires an 'access_id' field (and it should be encrypted)");
    }
    if (!names.contains("secret_key")) {
      error.accept("amazonses requires an 'secret_key' field (and it should be encrypted)");
    }
    if (!names.contains("region")) {
      error.accept("amazonses requires a 'region' field");
    }
    sb.append("  method<_AWSSES_SendRequest").append(", _AWSSES_SendResponse").append("> send;\n");
    sb.append("}\n");
    return sb.toString();
  }

  @Override
  public void request(NtPrincipal who, String method, String request, Callback<String> callback) {
    ObjectNode requestNode = Json.parseJsonObject(request);
    if ("send".equals(method)) {
      send(requestNode, callback);
      return;
    } else {
      callback.failure(new ErrorCodeException(ErrorCodes.FIRST_PARTY_SERVICES_METHOD_NOT_FOUND));
    }
  }

  private void send(ObjectNode requestNode, Callback<String> callback) {
    RequestResponseMonitor.RequestResponseMonitorInstance instance = metrics.amazon_ses_send.start();
    String from = Json.readString(requestNode, "from");
    String replyTo = Json.readString(requestNode, "replyTo");
    String to = Json.readString(requestNode, "to");
    String subject = Json.readString(requestNode, "subject");
    String text = Json.readString(requestNode, "text");
    String html = Json.readString(requestNode, "html");

    if (from == null || replyTo == null || to == null || subject == null || text == null || html == null) {
      instance.failure(ErrorCodes.FIRST_PARTY_AMAZON_SES_MISSING_ARGS);
      callback.failure(new ErrorCodeException(ErrorCodes.FIRST_PARTY_AMAZON_SES_MISSING_ARGS));
      return;
    }

    String url = "https://email." + region + ".amazonaws.com/v2/email/outbound-emails";
    HashMap<String, String> headers = new HashMap<>();
    final byte[] postBody;
    {
      ObjectNode request = Json.newJsonObject();
      request.put("FromEmailAddress", from);
      request.putArray("ReplyToAddresses").add(replyTo);
      request.putObject("Destination").putArray("ToAddresses").add(to);
      ObjectNode content = request.putObject("Content").putObject("Simple");
      ObjectNode subjectNode = content.putObject("Subject");
      subjectNode.put("Data", subject);
      subjectNode.put("Charset", "UTF-8");
      ObjectNode bodyText = content.putObject("Body").putObject("Text");
      bodyText.put("Data", text);
      bodyText.put("Charset", "UTF-8");
      if (html != null) {
        ObjectNode bodyHTML = content.putObject("Body").putObject("Html");
        bodyHTML.put("Data", html);
        bodyHTML.put("Charset", "UTF-8");
      }
      postBody = request.toString().getBytes(StandardCharsets.UTF_8);
    }
    String sha256 = Hex.of(Hashing.sha256().digest(postBody));

    new SignatureV4(credential, region, "ses", "POST", "email.us-east-2.amazonaws.com", "/v2/email/outbound-emails") //
        .withHeader("Content-Type", "application/json") //
        .withHeader("Content-Length", postBody.length + "") //
        .withContentHashSha256(sha256) //
        .signIntoHeaders(headers);

    base.execute(new SimpleHttpRequest("POST", url, headers, SimpleHttpRequestBody.WRAP(postBody)), new SimpleHttpResponder() {
      boolean responded = false;

      @Override
      public void start(SimpleHttpResponseHeader header) {
        if (!responded) {
          responded = true;
          if (header.status == 200 || header.status == 204) {
            callback.success("{}");
            instance.success();
          } else {
            callback.failure(new ErrorCodeException(ErrorCodes.FIRST_PARTY_AMAZON_SES_FAILURE));
            instance.failure(ErrorCodes.FIRST_PARTY_AMAZON_SES_FAILURE);
          }
        }
      }

      @Override
      public void bodyStart(long size) {
      }

      @Override
      public void bodyFragment(byte[] chunk, int offset, int len) {
      }

      @Override
      public void bodyEnd() {
      }

      @Override
      public void failure(ErrorCodeException ex) {
        if (!responded) {
          responded = true;
          LOGGER.error("failed-sending-email", ex);
          callback.failure(ex);
          instance.failure(ex.code);
        }
      }
    });
  }
}
