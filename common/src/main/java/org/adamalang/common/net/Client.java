package org.adamalang.common.net;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.ChannelInitializer;
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

import java.util.regex.Pattern;

/** defines a simple wrapper around netty to provide common networking */
public class Client {
  private static final ExceptionLogger EXLOGGER = ExceptionLogger.FOR(Client.class);
  private final NetBase base;
  private final MachineIdentity identity;
  private final SslContext sslContext;

  public Client(NetBase base, MachineIdentity identity) throws Exception {
    this.base = base;
    this.identity = identity;
    this.sslContext = SslContextBuilder.forClient().keyManager(identity.getCert(), identity.getKey()).trustManager(identity.getTrust()).build();
  }

  public void connect(String target, Lifecycle lifecycle) {
    try {
      String[] parts = target.split(Pattern.quote(":"));
      String peerHost = parts[0];
      int peerPort = Integer.parseInt(parts[1]);
      Bootstrap bootstrap = new Bootstrap();
      bootstrap.group(base.workerGroup);
      bootstrap.remoteAddress(peerHost, peerPort);
      bootstrap.channel(NioSocketChannel.class);
      bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
      bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 2500);
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

}
