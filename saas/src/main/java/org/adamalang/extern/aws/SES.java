/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.extern.aws;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.ErrorCodes;
import org.adamalang.aws.SignatureV4;
import org.adamalang.common.*;
import org.adamalang.common.metrics.RequestResponseMonitor;
import org.adamalang.extern.Email;
import org.adamalang.web.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;

/** amazon simple email service */
public class SES implements Email {
  private static final Logger LOGGER = LoggerFactory.getLogger(SES.class);
  private final WebClientBase base;
  private final AWSConfig config;
  private final AWSMetrics metrics;

  public SES(WebClientBase base, AWSConfig config, AWSMetrics metrics) {
    this.base = base;
    this.config = config;
    this.metrics = metrics;
  }

  @Override
  public boolean sendCode(String email, String code) {
    RequestResponseMonitor.RequestResponseMonitorInstance instance = metrics.send_email.start();
    try {
      CountDownLatch latch = new CountDownLatch(1);
      {
        String url = "https://email." + config.region + ".amazonaws.com/v2/email/outbound-emails";
        HashMap<String, String> headers = new HashMap<>();
        final byte[] postBody;
        {
          ObjectNode request = Json.newJsonObject();
          request.put("FromEmailAddress", config.fromEmailAddressForInit);
          request.putArray("ReplyToAddresses").add(config.fromEmailAddressForInit);
          request.putObject("Destination").putArray("ToAddresses").add(email);
          ObjectNode content = request.putObject("Content").putObject("Simple");
          ObjectNode subject = content.putObject("Subject");
          subject.put("Data", "Access code for Adama Platform");
          subject.put("Charset", "UTF-8");
          ObjectNode body = content.putObject("Body").putObject("Text");
          body.put("Data", "This is your code: " + code);
          body.put("Charset", "UTF-8");
          postBody = request.toString().getBytes(StandardCharsets.UTF_8);
        }
        String sha256 = Hex.of(Hashing.sha256().digest(postBody));

        new SignatureV4(config.credential, config.region, "ses", "POST", "email.us-east-2.amazonaws.com", "/v2/email/outbound-emails") //
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
              if (header.status == 200) {
                instance.success();
              } else {
                instance.failure(ErrorCodes.AWS_EMAIL_SEND_FAILURE_NOT_200);
              }
              latch.countDown();
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
              LOGGER.error("failed-sending-code", ex);
              instance.failure(ErrorCodes.AWS_EMAIL_SEND_FAILURE_EXCEPTION);
              latch.countDown();
            }
          }
        });
      }
      boolean result = AwaitHelper.block(latch, 5000);
      if (!result) {
        metrics.alarm_send_failures.up();
      }
      return result;
    } catch (Exception ex) {
      LOGGER.error("failed-sending-code-hard", ex);
      instance.failure(ErrorCodes.AWS_EMAIL_SEND_FAILURE_HARD_EXCEPTION);
      return false;
    }
  }
}
