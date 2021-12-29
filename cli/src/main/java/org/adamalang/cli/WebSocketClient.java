/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.cli;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketClientProtocolHandler;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketClientCompressionHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;

import java.net.URI;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class WebSocketClient {
    private final Config config;
    private final EventLoopGroup group;

    public class Connection {
        private final AtomicInteger idgen;
        private final Channel channel;
        private final ConcurrentHashMap<Long, BiConsumer<Object, Boolean>> callbacks;

        public Connection(Channel channel, ConcurrentHashMap<Long, BiConsumer<Object, Boolean>> callbacks) {
            this.idgen = new AtomicInteger(0);
            this.channel = channel;
            this.callbacks = callbacks;
        }

        public void stream(ObjectNode request, Consumer<ObjectNode> stream) throws Exception {
            long id = idgen.incrementAndGet();
            request.put("id", Long.toString(id));
            CountDownLatch latch = new CountDownLatch(1);
            AtomicReference<Exception> value = new AtomicReference<>(null);
            callbacks.put(id, (result, done) -> {
                if (result instanceof ObjectNode) {
                    stream.accept((ObjectNode) result);
                    if (done) {
                        latch.countDown();
                    }
                } else if (result instanceof Exception) {
                    value.set((Exception) result);
                }
            });
            channel.writeAndFlush(new TextWebSocketFrame(request.toString()));
            latch.await();
            if (value.get() != null) {
                throw value.get();
            }
        }

        public long open(ObjectNode request, Consumer<ObjectNode> stream, Consumer<Exception> error) throws Exception {
            long id = idgen.incrementAndGet();
            request.put("id", Long.toString(id));
            callbacks.put(id, (result, done) -> {
                if (result instanceof ObjectNode) {
                    stream.accept((ObjectNode) result);
                } else if (result instanceof Exception) {
                    error.accept((Exception) result);
                }
            });
            channel.writeAndFlush(new TextWebSocketFrame(request.toString()));
            return id;
        }

        public ObjectNode execute(ObjectNode request) throws Exception {
            long id = idgen.incrementAndGet();
            request.put("id", Long.toString(id));
            CountDownLatch latch = new CountDownLatch(1);

            AtomicReference<Object> value = new AtomicReference<>(null);
            try {
                callbacks.put(id, (result, done) -> {
                    value.set(result);
                    latch.countDown();
                });
                channel.writeAndFlush(new TextWebSocketFrame(request.toString()));
                boolean result = latch.await(5000, TimeUnit.MILLISECONDS);
                if (result && value.get() != null) {
                    if (value.get() instanceof ObjectNode) {
                        return (ObjectNode) value.get();
                    } else {
                        throw (Exception) value.get();
                    }
                } else {
                    throw new Exception("timed out");
                }
            } finally {
                callbacks.remove(id);
            }
        }
    }
    public Connection open() throws Exception {
        int maxContentLength = 1048576; // TODO: pull from config
        int timeoutSeconds = 2; // TODO: pull from config
        String url = "ws://localhost:8080/s";
        final var b = new Bootstrap();
        b.group(group);
        ConcurrentHashMap<Long, BiConsumer<Object, Boolean>> callbacks = new ConcurrentHashMap<>();
        b.channel(NioSocketChannel.class);
        CountDownLatch connected = new CountDownLatch(1);
        b.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(final SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new HttpClientCodec());
                ch.pipeline().addLast(new HttpObjectAggregator(maxContentLength));
                ch.pipeline().addLast(new WriteTimeoutHandler(timeoutSeconds));
                ch.pipeline().addLast(new ReadTimeoutHandler(timeoutSeconds));
                ch.pipeline().addLast(WebSocketClientCompressionHandler.INSTANCE);
                ch.pipeline().addLast(new WebSocketClientProtocolHandler( //
                        URI.create(url), //
                        WebSocketVersion.V13, //
                        null, //
                        true, //
                        new DefaultHttpHeaders(), // TODO: put in a user agent
                        50000));
                ch.pipeline().addLast(new SimpleChannelInboundHandler<TextWebSocketFrame>() {

                    @Override
                    public void channelActive(ChannelHandlerContext ctx) throws Exception {
                        System.err.println("connected");
                    }

                    @Override
                    protected void channelRead0(final ChannelHandlerContext ctx, final TextWebSocketFrame frame) throws Exception {
                        System.err.println(frame.text());
                        ObjectNode node = Util.parseJsonObject(frame.text());
                        if (node.has("ping")) {
                            node.put("pong", true);
                            ch.writeAndFlush(new TextWebSocketFrame(node.toString()));
                            return;
                        }

                        if (node.has("status")) {
                            if ("connected".equals(node.get("status").textValue())) {
                                connected.countDown();
                            }
                            return;
                        }

                        if (node.has("failure")) {
                            long id = node.get("failure").asLong();
                            int reason = node.get("reason").asInt();
                            BiConsumer<Object, Boolean> callback = callbacks.remove(id);
                            if (callback != null) {
                                callback.accept(new Exception("failure: " + reason), true);
                                return;
                            }
                        } else if (node.has("deliver")) {
                            long id = node.get("deliver").asLong();
                            boolean done = node.get("done").asBoolean();
                            BiConsumer<Object, Boolean> callback = done ? callbacks.remove(id) : callbacks.get(id);
                            if (callback != null) {
                                callback.accept(node.get("response"), done);
                                return;
                            }
                        }
                    }

                    @Override
                    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                        System.err.println("closed");
                    }

                    @Override
                    public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) throws Exception {
                        System.err.println("failure");
                        cause.printStackTrace();
                    }
                });
            }
        });
        final var future = b.connect("localhost", 8080);
        Channel ch = future.sync().channel();
        if (!connected.await(5000, TimeUnit.MILLISECONDS)) {
            throw new Exception("failed to establish");
        }
        return new Connection(ch, callbacks);
    }

    public WebSocketClient(Config config) {
        this.group = new NioEventLoopGroup();
        this.config = config;
    }
}
