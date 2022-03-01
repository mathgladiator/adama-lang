package org.adamalang.common.net;

import io.netty.channel.nio.NioEventLoopGroup;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/** defines the threading base for the common networking library */
public class NetBase {
  public final NioEventLoopGroup bossGroup;
  public final NioEventLoopGroup workerGroup;
  private final AtomicBoolean alive;
  private final CountDownLatch killLatch;
  private ArrayList<CountDownLatch> blockers;

  public NetBase(int bossThreads, int workerThreads) {
    this.bossGroup = new NioEventLoopGroup(bossThreads);
    this.workerGroup = new NioEventLoopGroup(workerThreads);
    this.alive = new AtomicBoolean(true);
    this.killLatch = new CountDownLatch(1);
    this.blockers = new ArrayList<>();
  }

  public void waitForShutdown() throws InterruptedException {
    killLatch.await();
  }

  public synchronized Runnable blocker() {
    CountDownLatch latchToBlock = new CountDownLatch(1);
    blockers.add(latchToBlock);
    return () -> { latchToBlock.countDown(); };
  }

  public void shutdown() {
    alive.set(false);
    killLatch.countDown();
    for (CountDownLatch blocker : blockers) {
      standardBlockerWait(blocker);
    }
    bossGroup.shutdownGracefully();
    workerGroup.shutdownGracefully();
  }

  public static void standardBlockerWait(CountDownLatch blocker) {
    try {
      blocker.await(10000, TimeUnit.MILLISECONDS);
    } catch (InterruptedException ie) {
      // ignore for now
    }
  }
}
