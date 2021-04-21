/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * which is in the root directory of the repository. This file is part of the 'Adama'
 * project which is a programming language and document store for board games.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2021 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.netty.client;

import org.adamalang.netty.server.CliServerOptions;
import org.adamalang.netty.server.ServerChannelInitializerTests;
import org.adamalang.netty.server.ServerRunnable;
import org.adamalang.netty.server.ServerRunnableTests;
import org.junit.Assert;
import org.junit.Test;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

public class ClientRequestBuilderTests {
  @Test
  public void crashCallbackCoverage() throws Exception {
    final var options = new CliServerOptions("--port", "9970");
    final var runnable = new ServerRunnable(ServerRunnableTests.nexus(options));
    final var thread = new Thread(runnable);
    thread.start();
    Assert.assertTrue(runnable.waitForReady(10000));
    Assert.assertTrue(runnable.isAccepting());
    ServerChannelInitializerTests.executeHealthCheck(options);
    final EventLoopGroup clientEventLoop = new NioEventLoopGroup();
    try {
      final MockClientCallback callback = new MockClientCallback(1) {
        @Override
        public void successfulResponse(final String data) {
          throw new UnsupportedOperationException("Yo");
        }
      };
      ClientRequestBuilder.start(clientEventLoop).get("/nooooooooooooooo").server("127.0.0.1", options.port()).execute(callback);
      callback.awaitDone();
      final var results = callback.output();
      Assert.assertEquals(1, results.size());
      Assert.assertEquals("Exception:Yo", results.get(0));
    } finally {
      clientEventLoop.shutdownGracefully();
    }
  }

  @Test
  public void executeAuthAttempt() throws Exception {
    final var options = new CliServerOptions("--port", "9973");
    final var runnable = new ServerRunnable(ServerRunnableTests.nexus(options));
    final var thread = new Thread(runnable);
    thread.start();
    Assert.assertTrue(runnable.waitForReady(10000));
    Assert.assertTrue(runnable.isAccepting());
    ServerChannelInitializerTests.executeHealthCheck(options);
    final EventLoopGroup clientEventLoop = new NioEventLoopGroup();
    try {
      final var callback = new MockClientCallback(1);
      ClientRequestBuilder.start(clientEventLoop).post("/", "{\"auth\":true}").server("127.0.0.1", options.port()).execute(callback);
      callback.awaitDone();
      final var results = callback.output();
      Assert.assertEquals(1, results.size());
      Assert.assertEquals("DATA:{\"ok\":\"auth\"}", results.get(0));
    } finally {
      clientEventLoop.shutdownGracefully();
    }
  }

  @Test
  public void executePostHappy() throws Exception {
    final var options = new CliServerOptions("--port", "9972");
    final var runnable = new ServerRunnable(ServerRunnableTests.nexus(options));
    final var thread = new Thread(runnable);
    thread.start();
    Assert.assertTrue(runnable.waitForReady(10000));
    Assert.assertTrue(runnable.isAccepting());
    ServerChannelInitializerTests.executeHealthCheck(options);
    final EventLoopGroup clientEventLoop = new NioEventLoopGroup();
    try {
      final var callback = new MockClientCallback(1);
      ClientRequestBuilder.start(clientEventLoop).post("/", "{\"success\":true}").server("127.0.0.1", options.port()).execute(callback);
      callback.awaitDone();
      final var results = callback.output();
      Assert.assertEquals(1, results.size());
      Assert.assertEquals("DATA:{\"ok\":\"go\"}", results.get(0));
    } finally {
      clientEventLoop.shutdownGracefully();
    }
  }

  @Test
  public void executePostSad() throws Exception {
    final var options = new CliServerOptions("--port", "9971");
    final var runnable = new ServerRunnable(ServerRunnableTests.nexus(options));
    final var thread = new Thread(runnable);
    thread.start();
    Assert.assertTrue(runnable.waitForReady(10000));
    Assert.assertTrue(runnable.isAccepting());
    ServerChannelInitializerTests.executeHealthCheck(options);
    final EventLoopGroup clientEventLoop = new NioEventLoopGroup();
    try {
      final var callback = new MockClientCallback(1);
      ClientRequestBuilder.start(clientEventLoop).post("/", "{}").server("127.0.0.1", options.port()).execute(callback);
      callback.awaitDone();
      final var results = callback.output();
      Assert.assertEquals(1, results.size());
      Assert.assertEquals("DATA:{\"error\":12}", results.get(0));
    } finally {
      clientEventLoop.shutdownGracefully();
    }
  }

  @Test
  public void executeTokenLookup() throws Exception {
    final var options = new CliServerOptions("--port", "9974");
    final var runnable = new ServerRunnable(ServerRunnableTests.nexus(options));
    final var thread = new Thread(runnable);
    thread.start();
    Assert.assertTrue(runnable.waitForReady(10000));
    Assert.assertTrue(runnable.isAccepting());
    ServerChannelInitializerTests.executeHealthCheck(options);
    final EventLoopGroup clientEventLoop = new NioEventLoopGroup();
    try {
      final var callback = new MockClientCallback(1);
      ClientRequestBuilder.start(clientEventLoop).header("cookie", AdamaCookieCodec.client(AdamaCookieCodec.ADAMA_AUTH_COOKIE_NAME, "XOK")).post("/", "{\"auth\":true}").server("127.0.0.1", options.port()).execute(callback);
      callback.awaitDone();
      final var results = callback.output();
      Assert.assertEquals(1, results.size());
      Assert.assertEquals("DATA:{\"ok\":\"authgood\"}", results.get(0));
    } finally {
      clientEventLoop.shutdownGracefully();
    }
  }

  @Test
  public void failureToConnect() {
    final EventLoopGroup clientEventLoop = new NioEventLoopGroup();
    final var callback = new MockClientCallback(1);
    ClientRequestBuilder.start(clientEventLoop).get("/~health_check_lb").server("localhost", 9999).execute(callback);
    callback.awaitDone();
    final var results = callback.output();
    Assert.assertEquals("FailedToConnect!", results.get(0));
    clientEventLoop.shutdownGracefully();
  }
}
