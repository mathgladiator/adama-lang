/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.common.net;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.ssl.ClientAuth;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import org.adamalang.ErrorCodes;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.ExceptionLogger;
import org.adamalang.common.MachineIdentity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;

/** defines the threading base for the common networking library */
public class NetBase {
  private static final Logger LOGGER = LoggerFactory.getLogger(NetBase.class);
  private static final ExceptionLogger EXLOGGER = ExceptionLogger.FOR(NetBase.class);

  public final NioEventLoopGroup bossGroup;
  public final NioEventLoopGroup workerGroup;
  public final MachineIdentity identity;
  private final AtomicBoolean alive;
  private final CountDownLatch killLatch;
  private ArrayList<CountDownLatch> blockers;
  private final SslContext sslContext;

  public NetBase(MachineIdentity identity, int bossThreads, int workerThreads) throws Exception {
    this.identity = identity;
    this.sslContext = SslContextBuilder.forClient().keyManager(identity.getCert(), identity.getKey()).trustManager(identity.getTrust()).build();
    this.bossGroup = new NioEventLoopGroup(bossThreads);
    this.workerGroup = new NioEventLoopGroup(workerThreads);
    this.alive = new AtomicBoolean(true);
    this.killLatch = new CountDownLatch(1);
    this.blockers = new ArrayList<>();
  }

  public SslContext makeServerSslContext() throws Exception {
    return SslContextBuilder.forServer(identity.getCert(), identity.getKey()).trustManager(identity.getTrust()).clientAuth(ClientAuth.REQUIRE).build();
  }

  public void connect(String target, Lifecycle lifecycle) {
    if (!alive.get()) {
      lifecycle.failed(new ErrorCodeException(ErrorCodes.NET_SHUTTING_DOWN));
    }
    try {
      String[] parts = target.split(Pattern.quote(":"));
      String peerHost = parts[0];
      int peerPort = Integer.parseInt(parts[1]);
      Bootstrap bootstrap = new Bootstrap();
      bootstrap.group(workerGroup);
      bootstrap.remoteAddress(peerHost, peerPort);
      bootstrap.channel(NioSocketChannel.class);
      bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
      bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 250);
      bootstrap.option(ChannelOption.TCP_NODELAY, true);
      bootstrap.handler(new ChannelInitializer<SocketChannel>() {
        @Override
        protected void initChannel(SocketChannel ch) throws Exception {
          ch.pipeline().addLast(sslContext.newHandler(ch.alloc(), peerHost, peerPort));
          ch.pipeline().addLast(new LengthFieldPrepender(4));
          ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(67108864, 0, 4, 0, 4));
          ch.pipeline().addLast(new ChannelClient(lifecycle));
        }
      });
      bootstrap.connect().addListener(new ChannelFutureListener() {
        @Override
        public void operationComplete(ChannelFuture channelFuture) throws Exception {
          if (!channelFuture.isSuccess()) {
            lifecycle.failed(new ErrorCodeException(ErrorCodes.NET_CONNECT_FAILED_TO_CONNECT));
          }
        }
      });
    } catch (Exception ex) {
      lifecycle.failed(ErrorCodeException.detectOrWrap(ErrorCodes.NET_CONNECT_FAILED_UNKNOWN, ex, EXLOGGER));
    }
  }

  public ServerHandle serve(int port, Handler handler) throws Exception {
    ServerBootstrap bootstrap = new ServerBootstrap();
    bootstrap.group(bossGroup, workerGroup);
    bootstrap.channel(NioServerSocketChannel.class);
    bootstrap.localAddress(port);
    SslContext sslContext = makeServerSslContext();
    bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
      protected void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline().addLast(sslContext.newHandler(ch.alloc()));
        ch.pipeline().addLast(new LengthFieldPrepender(4));
        ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(65535,0, 4, 0, 4));
        ch.pipeline().addLast(new ChannelServer(handler));
      }
    });
    ChannelFuture future = bootstrap.bind();
    Runnable blocker = blocker();
    LOGGER.info("started");
    return new ServerHandle() {
      @Override
      public void waitForEnd() {
        LOGGER.info("waiting");
        try {
          future.channel().closeFuture().sync();
        } catch (Exception ex) {
          LOGGER.info("failure", ex);
          ex.printStackTrace();
        } finally {
          blocker.run();
        }
        LOGGER.info("finished");
      }

      @Override
      public void kill() {
        future.channel().close();
      }
    };
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
    blockers.clear();
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
