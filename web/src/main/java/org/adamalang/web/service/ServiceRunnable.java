/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.web.service;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.adamalang.web.contracts.ServiceBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class ServiceRunnable implements Runnable {
  private static final Logger LOGGER = LoggerFactory.getLogger(ServiceRunnable.class);
  private final WebConfig webConfig;
  private final WebMetrics metrics;
  private final ServiceBase base;
  private final CountDownLatch ready;
  private final AtomicBoolean started;
  private Channel channel;
  private boolean stopped;

  public ServiceRunnable(final WebConfig webConfig, final WebMetrics metrics, ServiceBase base) {
    this.webConfig = webConfig;
    this.metrics = metrics;
    this.base = base;
    started = new AtomicBoolean();
    channel = null;
    stopped = false;
    ready = new CountDownLatch(1);
  }

  public synchronized boolean isAccepting() {
    return channel != null;
  }

  public boolean waitForReady(final int ms) throws InterruptedException {
    return ready.await(ms, TimeUnit.MILLISECONDS);
  }

  @Override
  public void run() {
    if (!started.compareAndExchange(false, true)) {
      LOGGER.info("starting-web-proxy");
      try {
        try {
          final EventLoopGroup bossGroup = new NioEventLoopGroup(1);
          final EventLoopGroup workerGroup = new NioEventLoopGroup();
          try {
            final var b = new ServerBootstrap();
            b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class).childHandler(new Initializer(webConfig, metrics, base));
            final var ch = b.bind(webConfig.port).sync().channel();
            channelRegistered(ch);
            LOGGER.info("channel-registered");
            ch.closeFuture().sync();
            LOGGER.info("channel-close-futured-syncd");
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

  private synchronized void channelRegistered(final Channel channel) {
    this.channel = channel;
    if (stopped) {
      channel.close();
    }
    ready.countDown();
  }

  public synchronized void shutdown() {
    stopped = true;
    if (channel != null) {
      channel.close();
    }
  }
}
