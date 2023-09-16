/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.common;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** for complex executor bouncing, this helps understand what is going on */
public abstract class NamedRunnable implements Runnable {
  private static final Logger PERF_LOG = LoggerFactory.getLogger("perf");
  private static final Logger RUNNABLE_LOGGER = LoggerFactory.getLogger("nrex");
  public final String __runnableName;
  private String runningIn = "unknown";
  private long created;
  private long delay;

  public NamedRunnable(String first, String... tail) {
    if (tail == null || tail.length == 0) {
      this.__runnableName = first;
    } else {
      StringBuilder sb = new StringBuilder();
      sb.append(first);
      for (String fragment : tail) {
        sb.append("/");
        sb.append(fragment);
      }
      this.__runnableName = sb.toString();
    }
    this.created = System.currentTimeMillis();
    this.delay = 0;
  }

  public void bind(String executorName) {
    this.runningIn = executorName;
  }

  public void delay(long ms) {
    this.delay = ms;
  }

  @Override
  public void run() {
    long ran = System.currentTimeMillis();
    try {
      execute();
      long now = System.currentTimeMillis();
      long queueTime = now - created - delay;
      long runTime = now - ran;
      this.created = now; // for periodic tasks
      /*
      boolean skip = queueTime <= 1 && runTime <= 1;
      if (!skip) {
        ObjectNode entry = Json.newJsonObject();
        entry.put("type", "executor");
        entry.put("name", __runnableName);
        entry.put("executor", runningIn);
        entry.put("queueTime", queueTime);
        entry.put("queueStatus", (queueTime > 500 ? "delayed" : "quick"));
        entry.put("runTime", runTime);
        entry.put("runStatus", (runTime > 25 ? "slow" : "fast"));
        PERF_LOG.error(entry.toString());
      }
      */
    } catch (Exception ex) {
      boolean noise = noisy(ex);
      if (!noise) {
        RUNNABLE_LOGGER.error(__runnableName, ex);
      }
    }
  }

  public abstract void execute() throws Exception;

  public static boolean noisy(Exception ex) {
    return ex instanceof java.util.concurrent.RejectedExecutionException;
  }

  @Override
  public String toString() {
    return __runnableName;
  }
}
