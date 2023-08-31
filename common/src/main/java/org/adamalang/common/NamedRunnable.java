/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** for complex executor bouncing, this helps understand what is going on */
public abstract class NamedRunnable implements Runnable {
  private static final Logger PERF_LOG = LoggerFactory.getLogger("perf");
  private static final Logger RUNNABLE_LOGGER = LoggerFactory.getLogger("nrex");
  public final String __runnableName;
  private String runningIn = "unknown";
  private long created;

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
  }

  public void bind(String executorName) {
    this.runningIn = executorName;
  }

  @Override
  public void run() {
    long ran = System.currentTimeMillis();
    try {
      execute();
      long now = System.currentTimeMillis();
      long queueTime = now - created;
      long runTime = now - ran;
      // TODO: this is for performance measuring (need to automate in a meaningful way)
      boolean skip = queueTime <= 1 && runTime <= 1;
      if (skip) {
        // PERF_LOG.error(__runnableName + "," + runningIn + "," + (queueTime) + "," + (queueTime > 10 ? "delayed" : "quick") + "," + runTime + "," + (runTime > 25 ? "slow" : "fast"));
      }
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
