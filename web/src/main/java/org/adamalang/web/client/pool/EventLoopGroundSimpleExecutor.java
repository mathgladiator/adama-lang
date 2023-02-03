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
