/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.services;

import org.adamalang.common.metrics.MetricsFactory;
import org.adamalang.common.metrics.RequestResponseMonitor;

public class FirstPartyMetrics {
  public final RequestResponseMonitor amazon_ses_send;
  public final RequestResponseMonitor stripe_invoke;
  public final RequestResponseMonitor google_validate;

  public FirstPartyMetrics(MetricsFactory factory) {
    amazon_ses_send = factory.makeRequestResponseMonitor("fpm_amazon_ses_send");
    stripe_invoke = factory.makeRequestResponseMonitor("fpm_stripe_invoke");
    google_validate = factory.makeRequestResponseMonitor("fpm_google_validate");
  }
}
