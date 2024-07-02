/*
* Adama Platform and Language
* Copyright (C) 2021 - 2024 by Adama Platform Engineering, LLC
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as published
* by the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Affero General Public License for more details.
* 
* You should have received a copy of the GNU Affero General Public License
* along with this program.  If not, see <https://www.gnu.org/licenses/>.
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
    itemToLog.put("failure-code", ex.code);
    logger.log(itemToLog);
  }
}
