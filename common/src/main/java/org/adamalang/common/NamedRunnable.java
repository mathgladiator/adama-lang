/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** for complex executor bouncing, this helps understand what is going on */
public abstract class NamedRunnable implements Runnable {
  private static final Logger LOGGER = LoggerFactory.getLogger("nrex");
  public final String name;

  public NamedRunnable(String first, String... tail) {
    if (tail == null || tail.length == 0) {
      this.name = first;
    } else {
      StringBuilder sb = new StringBuilder();
      sb.append(first);
      for (String fragment : tail) {
        sb.append("/");
        sb.append(fragment);
      }
      this.name = sb.toString();
    }
  }

  @Override
  public void run() {
    try {
      execute();
    } catch (Exception ex) {
      LOGGER.error(name, ex);
    }
  }

  public abstract void execute() throws Exception;

  @Override
  public String toString() {
    return name;
  }
}
