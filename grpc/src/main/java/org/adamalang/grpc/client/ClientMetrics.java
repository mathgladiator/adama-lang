/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.grpc.client;

import org.adamalang.common.metrics.MetricsFactory;

public class ClientMetrics {
  public final Runnable client_notify_deploy_attempt;
  public final Runnable client_notify_deploy_success;
  public final Runnable client_notify_deploy_failure_do;
  public final Runnable client_notify_deploy_failure_find;

  public ClientMetrics(MetricsFactory factory) {
    client_notify_deploy_attempt = factory.counter("client_notify_deploy_attempt");
    client_notify_deploy_success = factory.counter("client_notify_deploy_success");
    client_notify_deploy_failure_do = factory.counter("client_notify_deploy_failure_do");
    client_notify_deploy_failure_find = factory.counter("client_notify_deploy_failure_find");
  }
}
