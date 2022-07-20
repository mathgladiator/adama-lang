/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.web.io;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.NamedRunnable;
import org.adamalang.common.SimpleExecutor;
import org.adamalang.common.metrics.StreamMonitor;

import java.util.HashMap;

/**
 * a JsonResponder wrapper which will remove the given key from a map on a terminal signal. Note:
 * all mutations to the map are executed in the provided executor
 */
public class JsonResponderHashMapCleanupProxy<T> implements JsonResponder {
  private final StreamMonitor.StreamMonitorInstance metrics;
  private final SimpleExecutor executor;
  private final HashMap<Long, T> map;
  private final long key;
  private final JsonResponder responder;
  private final ObjectNode itemToLog;
  private final JsonLogger logger;

  public JsonResponderHashMapCleanupProxy(StreamMonitor.StreamMonitorInstance metrics, SimpleExecutor executor, HashMap<Long, T> map, long key, JsonResponder responder, ObjectNode itemToLog, JsonLogger logger) {
    this.metrics = metrics;
    this.executor = executor;
    this.map = map;
    this.key = key;
    this.responder = responder;
    this.itemToLog = itemToLog;
    this.logger = logger;
  }

  @Override
  public void stream(String json) {
    metrics.progress();
    responder.stream(json);
  }

  @Override
  public void finish(String json) {
    metrics.finish();
    executor.execute(new NamedRunnable("json-hashmap-cleanup") {
      @Override
      public void execute() throws Exception {
        map.remove(key);
      }
    });
    responder.finish(json);
    itemToLog.put("success", true);
    logger.log(itemToLog);
  }

  @Override
  public void error(ErrorCodeException ex) {
    metrics.failure(ex.code);
    executor.execute(new NamedRunnable("json-hashmap-cleanup") {
      @Override
      public void execute() throws Exception {
        map.remove(key);
      }
    });
    responder.error(ex);
    itemToLog.put("success", false);
    itemToLog.put("failure-reason", ex.code);
    logger.log(itemToLog);
  }
}
