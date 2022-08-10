/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.common.capacity;

import org.adamalang.common.NamedRunnable;
import org.adamalang.common.SimpleExecutor;

import java.util.concurrent.atomic.AtomicBoolean;

/** repeat the last signal given every period */
public class RepeatingSignal implements BoolConsumer {
  private final SimpleExecutor executor;
  private final AtomicBoolean alive;
  private final int freq_ms;
  private final BoolConsumer event;
  private final AtomicBoolean valueToUse;
  private long lastSent;

  public RepeatingSignal(SimpleExecutor executor, AtomicBoolean alive, int freq_ms, BoolConsumer event) {
    this.executor = executor;
    this.alive = alive;
    this.event = event;
    this.freq_ms = freq_ms;
    this.valueToUse = new AtomicBoolean(false);
    this.executor.schedule(new NamedRunnable("repeat-signal") {
      @Override
      public void execute() throws Exception {
        if (alive.get()) {
          if((System.currentTimeMillis() - lastSent) + 1 >= freq_ms) {
            event.accept(valueToUse.get());
          }
          executor.schedule(this, freq_ms);
          lastSent = System.currentTimeMillis();
        }
      }
    }, 1);
  }

  @Override
  public void accept(final boolean value) {
    valueToUse.set(value);
    this.executor.execute(new NamedRunnable("send-now") {
      @Override
      public void execute() throws Exception {
        event.accept(valueToUse.get());
        lastSent = System.currentTimeMillis();
      }
    });
  }
}
