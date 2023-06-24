/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.web.service;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import io.netty.util.concurrent.ScheduledFuture;
import org.adamalang.web.contracts.WellKnownHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class RedirectAndWellknownServiceRunnable implements Runnable {
  private static final Logger LOGGER = LoggerFactory.getLogger(ServiceRunnable.class);
  private final WebConfig webConfig;
  private final WebMetrics metrics;
  private final CountDownLatch ready;
  private final AtomicBoolean started;
  private final Runnable heartbeat;
  private Channel channel;
  private boolean stopped;
  private final WellKnownHandler wellKnownHandler;

  public RedirectAndWellknownServiceRunnable(final WebConfig webConfig, final WebMetrics metrics, WellKnownHandler wellKnownHandler, Runnable heartbeat) {
    this.webConfig = webConfig;
    this.metrics = metrics;
    this.wellKnownHandler = wellKnownHandler;
    started = new AtomicBoolean();
    channel = null;
    stopped = false;
    ready = new CountDownLatch(1);
    this.heartbeat = heartbeat;
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
          AtomicBoolean alive = new AtomicBoolean(true);
          final EventLoopGroup bossGroup = new NioEventLoopGroup(1);
          final EventLoopGroup workerGroup = new NioEventLoopGroup(webConfig.workerThreads);
          try {
            final var b = new ServerBootstrap();
            b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class).childHandler(new ChannelInitializer<SocketChannel>() {
              @Override
              protected void initChannel(SocketChannel ch) throws Exception {
                final var pipeline = ch.pipeline();
                pipeline.addLast(new HttpServerCodec());
                pipeline.addLast(new ReadTimeoutHandler(webConfig.readTimeoutSeconds));
                pipeline.addLast(new WriteTimeoutHandler(webConfig.writeTimeoutSeconds));
                pipeline.addLast(new IdleStateHandler(webConfig.idleReadSeconds, webConfig.idleWriteSeconds, webConfig.idleAllSeconds, TimeUnit.SECONDS));
                pipeline.addLast(new HttpObjectAggregator(webConfig.maxContentLengthSize));
                pipeline.addLast(new RedirectHandler(webConfig, wellKnownHandler));
              }
            });
            final var ch = b.bind(webConfig.redirectPort).sync().channel();
            channelRegistered(ch);
            LOGGER.info("channel-registered");
            ScheduledFuture<?> future = bossGroup.scheduleAtFixedRate(() -> {
              workerGroup.schedule(() -> {
                if (alive.get()) {
                  metrics.redirect_server_heartbeat.run();
                  heartbeat.run();
                }
              }, (int) (10 + 15 * Math.random()), TimeUnit.MILLISECONDS);
            }, 50, 50, TimeUnit.MILLISECONDS);
            ch.closeFuture().sync();
            LOGGER.info("channel-close-future-syncd");
            future.cancel(false);
          } finally {
            alive.set(false);
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
          }
        } catch (final Exception ie) {
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
