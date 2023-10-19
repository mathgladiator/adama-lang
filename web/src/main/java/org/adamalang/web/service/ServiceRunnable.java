/*
* Adama Platform and Language
* Copyright (C) 2021 - 2023 by Adama Platform Initiative, LLC
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as published
* by the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Affero General Public License for more details.
* 
* You should have received a copy of the GNU Affero General Public License
* along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
package org.adamalang.web.service;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.util.concurrent.ScheduledFuture;
import org.adamalang.common.TimeSource;
import org.adamalang.runtime.sys.domains.DomainFinder;
import org.adamalang.web.assets.cache.WebHandlerAssetCache;
import org.adamalang.web.contracts.CertificateFinder;
import org.adamalang.web.contracts.ServiceBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class ServiceRunnable implements Runnable {
  private static final Logger LOGGER = LoggerFactory.getLogger(ServiceRunnable.class);
  private final WebConfig webConfig;
  private final WebMetrics metrics;
  private final ServiceBase base;
  private final CertificateFinder certificateFinder;
  private final CountDownLatch ready;
  private final AtomicBoolean started;
  private final Runnable heartbeat;
  private final WebHandlerAssetCache cache;
  private Channel channel;
  private boolean stopped;
  private final DomainFinder domainFinder;

  public ServiceRunnable(final WebConfig webConfig, final WebMetrics metrics, ServiceBase base, CertificateFinder certificateFinder, DomainFinder domainFinder, Runnable heartbeat) {
    this.webConfig = webConfig;
    this.metrics = metrics;
    this.base = base;
    this.certificateFinder = certificateFinder;
    this.domainFinder = domainFinder;
    started = new AtomicBoolean();
    channel = null;
    stopped = false;
    ready = new CountDownLatch(1);
    this.heartbeat = heartbeat;
    this.cache = new WebHandlerAssetCache(TimeSource.REAL_TIME, webConfig.cacheRoot);
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
          SslContext context = null;
          File certificate = new File("cert.pem");
          File privateKey = new File("key.pem");
          if (certificate.exists() && privateKey.exists()) {
            context = SslContextBuilder.forServer(certificate, privateKey).build();
            LOGGER.info("found-certificate-and-key");
          }
          final EventLoopGroup bossGroup = new NioEventLoopGroup(1);
          final EventLoopGroup workerGroup = new NioEventLoopGroup(webConfig.workerThreads);
          try {
            final var b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)//
             .channel(NioServerSocketChannel.class) //
             .childHandler(new Initializer(webConfig, metrics, base, certificateFinder, context, cache, domainFinder));
            final var ch = b.bind(webConfig.port).sync().channel();
            channelRegistered(ch);
            LOGGER.info("channel-registered");
            ScheduledFuture<?> future = bossGroup.scheduleAtFixedRate(() -> {
              workerGroup.schedule(() -> {
                if (alive.get()) {
                  metrics.websockets_server_heartbeat.run();
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
        } catch (final Exception ex) {
          LOGGER.error("exception-server", ex);
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
