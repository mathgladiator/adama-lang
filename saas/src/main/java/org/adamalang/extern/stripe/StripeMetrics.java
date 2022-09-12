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

import org.adamalang.common.metrics.MetricsFactory;
import org.adamalang.common.metrics.RequestResponseMonitor;

/** stripe metrics */
public class StripeMetrics {
  public final RequestResponseMonitor stripe_create_customer;
  public final RequestResponseMonitor stripe_delete_customer;

  public StripeMetrics(MetricsFactory factory) {
    this.stripe_create_customer = factory.makeRequestResponseMonitor("stripe_create_customer");
    this.stripe_delete_customer = factory.makeRequestResponseMonitor("stripe_delete_customer");
  }
}
