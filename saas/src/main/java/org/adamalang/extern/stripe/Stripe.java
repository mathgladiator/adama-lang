/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.extern.stripe;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.Json;
import org.adamalang.common.URL;
import org.adamalang.extern.aws.AWSConfig;
import org.adamalang.extern.aws.AWSMetrics;
import org.adamalang.extern.aws.SQS;
import org.adamalang.web.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.TreeMap;

public class Stripe {
  private final Logger LOGGER = LoggerFactory.getLogger(Stripe.class);
  private final WebClientBase base;
  private final StripeConfig config;
  private final StripeMetrics metrics;
  private final String authHeader;

  public Stripe(WebClientBase base, StripeConfig config, StripeMetrics metrics) {
    this.base = base;
    this.config = config;
    this.metrics = metrics;
    this.authHeader = "Basic " + new String(Base64.getEncoder().encode((config.secretKey + ":").getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
  }

  public void createCustomer(int userId, String email, Callback<String> callback) {
    TreeMap<String, String> params = new TreeMap<>();
    params.put("email", email);
    params.put("description", "adama-customer:" + userId);

    byte[] body = URL.parameters(params).substring(1).getBytes(StandardCharsets.UTF_8);
    TreeMap<String, String> headers = new TreeMap<>();
    headers.put("Content-Type", "application/x-www-form-urlencoded");
    headers.put("Content-Length", "" + body.length);
    headers.put("Authorization", authHeader);

    SimpleHttpRequest request = new SimpleHttpRequest("POST", "https://api.stripe.com/v1/customers", headers, SimpleHttpRequestBody.WRAP(body));
    base.execute(request, new StringCallbackHttpResponder(LOGGER, metrics.stripe_create_customer.start(), new Callback<String>() {
      @Override
      public void success(String value) {
        try {
          String customerId = Json.parseJsonObject(value).get("id").textValue();
          callback.success(customerId);
        } catch (Exception ex) {
          callback.failure(new ErrorCodeException(123));
        }
      }

      @Override
      public void failure(ErrorCodeException ex) {
        callback.failure(ex);
      }
    }));
  }

  public void deleteCustomer(String customerId, Callback<Void> callback) {
    TreeMap<String, String> headers = new TreeMap<>();
    headers.put("Authorization", authHeader);
    SimpleHttpRequest request = new SimpleHttpRequest("DELETE", "https://api.stripe.com/v1/customers/" + customerId, headers, SimpleHttpRequestBody.EMPTY);
    base.execute(request, new StringCallbackHttpResponder(LOGGER, metrics.stripe_delete_customer.start(), new Callback<String>() {
      @Override
      public void success(String value) {
        callback.success(null);
      }

      @Override
      public void failure(ErrorCodeException ex) {
        callback.failure(ex);
      }
    }));
  }

  // public void createSubscription(String customerId, String priceId, String paymentMethodId) {}
  // public void retryInvoice(String customerId, String invoiceId, String paymentMethodId) {}
  // public void cancelService(STring customerId);
}
