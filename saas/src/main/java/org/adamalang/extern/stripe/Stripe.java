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
import org.adamalang.extern.aws.AWSConfig;
import org.adamalang.extern.aws.AWSMetrics;
import org.adamalang.extern.aws.SQS;
import org.adamalang.web.client.WebClientBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Stripe {
  private final Logger LOGGER = LoggerFactory.getLogger(Stripe.class);
  private final WebClientBase base;
  private final StripeConfig config;
  private final StripeMetrics metrics;

  public Stripe(WebClientBase base, StripeConfig config, StripeMetrics metrics) {
    this.base = base;
    this.config = config;
    this.metrics = metrics;
  }

  public void createCustomer(int userId, String email, Callback<String> callback) {
  }

  // public void createSubscription(String customerId, String priceId, String paymentMethodId) {}
  // public void retryInvoice(String customerId, String invoiceId, String paymentMethodId) {}
  // public void cancelService(STring customerId);
}
