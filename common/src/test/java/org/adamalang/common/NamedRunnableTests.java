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

import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

public class NamedRunnableTests {
  @Test
  public void coverageLongName() {
    AtomicInteger x = new AtomicInteger(0);
    NamedRunnable runnable =
        new NamedRunnable("me", "too", "tired") {
          @Override
          public void execute() throws Exception {
            if (x.incrementAndGet() == 3) {
              throw new Exception("huh");
            }
          }
        };
    Assert.assertEquals("me/too/tired", runnable.toString());
    runnable.run();
    runnable.run();
    runnable.run();
    runnable.run();
  }

  @Test
  public void coverageSingle() {
    AtomicInteger x = new AtomicInteger(0);
    NamedRunnable runnable =
        new NamedRunnable("me") {
          @Override
          public void execute() throws Exception {
            if (x.incrementAndGet() == 3) {
              throw new Exception("huh");
            }
          }
        };
    Assert.assertEquals("me", runnable.toString());
    runnable.run();
    runnable.run();
    runnable.run();
    runnable.run();
  }
}
