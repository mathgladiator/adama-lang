/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.web.client.pool;

import io.netty.channel.EventLoopGroup;
import io.netty.util.concurrent.ScheduledFuture;
import org.adamalang.common.NamedRunnable;
import org.adamalang.common.SimpleExecutor;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/** Convert a netty EventLoopGroup into a SimpleExecutor */
public class EventLoopGroundSimpleExecutor implements SimpleExecutor {
  private final EventLoopGroup group;

  public EventLoopGroundSimpleExecutor(EventLoopGroup group) {
    this.group = group;
  }

  @Override
  public void execute(NamedRunnable command) {
    group.execute(command);
  }

  @Override
  public Runnable schedule(NamedRunnable command, long milliseconds) {
    ScheduledFuture<?> future = group.schedule(command, milliseconds, TimeUnit.MILLISECONDS);
    return () -> future.cancel(false);
  }

  @Override
  public Runnable scheduleNano(NamedRunnable command, long nanoseconds) {
    ScheduledFuture<?> future = group.schedule(command, nanoseconds, TimeUnit.NANOSECONDS);
    return () -> future.cancel(false);
  }

  @Override
  public CountDownLatch shutdown() {
    CountDownLatch latch = new CountDownLatch(1);
    group.execute(() -> {
      latch.countDown();
    });
    group.shutdownGracefully();
    return latch;
  }
}
