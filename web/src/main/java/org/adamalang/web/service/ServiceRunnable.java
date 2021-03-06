/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * which is in the root directory of the repository. This file is part of the 'Adama'
 * project which is a programming language and document store for board games.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2021 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.web.service;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class ServiceRunnable implements Runnable {
  private Channel channel;
  private final Nexus nexus;
  private final CountDownLatch ready;
  private final AtomicBoolean started;
  private boolean stopped;

  public ServiceRunnable(final Nexus nexus) {
    this.nexus = nexus;
    started = new AtomicBoolean();
    channel = null;
    stopped = false;
    ready = new CountDownLatch(1);
  }

  private synchronized void channelRegistered(final Channel channel) {
    this.channel = channel;
    if (stopped) {
      channel.close();
    }
    ready.countDown();
  }

  public synchronized boolean isAccepting() {
    return channel != null;
  }

  public synchronized void shutdown() {
    stopped = true;
    if (channel != null) {
      channel.close();
    }
  }

  public boolean waitForReady(final int ms) throws InterruptedException {
    return ready.await(ms, TimeUnit.MILLISECONDS);
  }

  @Override
  public void run() {
    if (!started.compareAndExchange(false, true)) {
      try {
        try {
          final EventLoopGroup bossGroup = new NioEventLoopGroup(1);
          final EventLoopGroup workerGroup = new NioEventLoopGroup();
          try {
            final var b = new ServerBootstrap();
            b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class).childHandler(new Initializer(nexus));
            final var ch = b.bind(nexus.config.port).sync().channel();
            channelRegistered(ch);
            // TODO: log information out
            ch.closeFuture().sync();
          } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
          }
        } catch (final InterruptedException ie) {
          shutdown();
        }
      } finally {
        ready.countDown();
      }
    }
  }
}
