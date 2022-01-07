/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.web.io;

import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.metrics.RequestResponseMonitor;

public class SimpleMetricsProxyResponder implements JsonResponder {
  private final RequestResponseMonitor.RequestResponseMonitorInstance metrics;
  private final JsonResponder responder;

  public SimpleMetricsProxyResponder(RequestResponseMonitor.RequestResponseMonitorInstance metrics, JsonResponder responder) {
    this.metrics = metrics;
    this.responder = responder;
  }

  @Override
  public void stream(String json) {
    metrics.extra();
    responder.stream(json);
  }

  @Override
  public void finish(String json) {
    metrics.success();
    responder.finish(json);
  }

  @Override
  public void error(ErrorCodeException ex) {
    metrics.failure(ex.code);
    responder.error(ex);
  }
}
