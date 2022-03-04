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

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.ssl.ClientAuth;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import org.adamalang.common.MachineIdentity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

/** a wrapper around netty to provider common network primitives */
public class Server {
  private static final Logger LOGGER = LoggerFactory.getLogger(Server.class);

  public static Runnable start(NetBase base, int port, Handler handler) throws Exception {
    ServerBootstrap bootstrap = new ServerBootstrap();
    bootstrap.group(base.bossGroup, base.workerGroup);
    bootstrap.channel(NioServerSocketChannel.class);
    bootstrap.localAddress(port);
    SslContext sslContext = base.makeServerSslContext();
    bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
      protected void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline().addLast(sslContext.newHandler(ch.alloc()));
        ch.pipeline().addLast(new LengthFieldPrepender(4));
        ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(65535,0, 4, 0, 4));
        ch.pipeline().addLast(new ChannelServer(handler));
      }
    });
    ChannelFuture future = bootstrap.bind();
    Runnable blocker = base.blocker();
    LOGGER.info("started");
    return () -> {
      LOGGER.info("waiting");
      try {
        base.waitForShutdown();
        LOGGER.info("closing");
        future.channel().close().sync();
      } catch (Exception ex) {
        LOGGER.info("failure", ex);
        ex.printStackTrace();
      } finally {
        blocker.run();
      }
      LOGGER.info("finished");
    };
  }
}
