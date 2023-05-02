/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's Apache2); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.web.io;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.metrics.RequestResponseMonitor;

public class SimpleMetricsProxyResponder implements JsonResponder {
  private final RequestResponseMonitor.RequestResponseMonitorInstance metrics;
  private final JsonResponder responder;
  private final ObjectNode itemToLog;
  private final JsonLogger logger;

  public SimpleMetricsProxyResponder(RequestResponseMonitor.RequestResponseMonitorInstance metrics, JsonResponder responder, ObjectNode itemToLog, JsonLogger logger) {
    this.metrics = metrics;
    this.responder = responder;
    this.itemToLog = itemToLog;
    this.logger = logger;
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
    itemToLog.put("success", true);
    logger.log(itemToLog);
  }

  @Override
  public void error(ErrorCodeException ex) {
    metrics.failure(ex.code);
    responder.error(ex);
    itemToLog.put("success", false);
    itemToLog.put("failure-reason", ex.code);
    logger.log(itemToLog);
  }
}
