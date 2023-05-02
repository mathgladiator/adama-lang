/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's Apache2); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** for complex executor bouncing, this helps understand what is going on */
public abstract class NamedRunnable implements Runnable {
  private static final Logger RUNNABLE_LOGGER = LoggerFactory.getLogger("nrex");
  public final String __runnableName;

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
  }

  @Override
  public void run() {
    try {
      execute();
    } catch (Exception ex) {
      boolean noise = ex instanceof java.util.concurrent.RejectedExecutionException;
      if (noise) {
        RUNNABLE_LOGGER.error("noise:" + __runnableName + ex.getMessage());
      } else {
        RUNNABLE_LOGGER.error(__runnableName, ex);
      }
    }
  }

  public abstract void execute() throws Exception;

  @Override
  public String toString() {
    return __runnableName;
  }
}
