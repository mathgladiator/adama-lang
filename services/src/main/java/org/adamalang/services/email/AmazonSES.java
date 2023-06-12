/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.services.email;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.ErrorCodes;
import org.adamalang.aws.Credential;
import org.adamalang.aws.SignatureV4;
import org.adamalang.common.*;
import org.adamalang.common.metrics.RequestResponseMonitor;
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.runtime.remote.SimpleService;
import org.adamalang.services.FirstPartyMetrics;
import org.adamalang.services.ServiceConfig;
import org.adamalang.web.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

public class AmazonSES extends SimpleService {
  private static final Logger LOGGER = LoggerFactory.getLogger(AmazonSES.class);
  private final FirstPartyMetrics metrics;
  private final ExecutorService executor;
  private final WebClientBase base;
  private final String region;
  private final Credential credential;

  public AmazonSES(FirstPartyMetrics metrics, ServiceConfig config, WebClientBase base, ExecutorService executor) throws ErrorCodeException {
    super("amazonses", new NtPrincipal("amazonses", "service"), true);
    this.metrics = metrics;
    this.executor = executor;
    this.credential = new Credential(config.getDecryptedSecret("access_id"), config.getDecryptedSecret("secret_key"));
    this.region = config.getString("region", "us-east-2");
    this.base = base;
  }

  public static String definition(int uniqueId, String params, HashSet<String> names, Consumer<String> error) {
    StringBuilder sb = new StringBuilder();
    sb.append("message AWSSES_SendRequest_").append(uniqueId).append(" { string from; string replyTo; string to; string subject; string text; string html; }\n");
    sb.append("message AWSSES_SendResponse_").append(uniqueId).append(" { }\n");
    sb.append("service amazonses {\n");
    sb.append("  class=\"amazonses\";\n");
    sb.append("  ").append(params).append("\n");
    if (!names.contains("access_id")) {
      error.accept("amazonses requires an 'access_id' field (and it should be encrypted)");
    }
    if (!names.contains("secret_key")) {
      error.accept("amazonses requires an 'secret_key' field (and it should be encrypted)");
    }
    sb.append("  method<AWSSES_SendRequest_").append(uniqueId).append(", AWSSES_SendResponse_").append(uniqueId).append("> send;\n");
    sb.append("}\n");
    return sb.toString();
  }

  @Override
  public void request(String method, String request, Callback<String> callback) {
    ObjectNode requestNode = Json.parseJsonObject(request);
    switch (method) {
      case "send":
        send(requestNode, callback);
        return;
      default:
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
