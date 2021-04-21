/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * which is in the root directory of the repository. This file is part of the 'Adama'
 * project which is a programming language and document store for board games.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2021 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.netty.server;

import org.adamalang.netty.client.ClientRequestBuilder;
import org.adamalang.netty.client.MockClientCallback;
import org.adamalang.netty.contracts.ServerOptions;
import org.junit.Assert;
import org.junit.Test;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

public class WebHandlerTests {
  public static String execute_simple(final ServerOptions options, final String path) {
    final EventLoopGroup clientEventLoop = new NioEventLoopGroup();
    try {
      final var callback = new MockClientCallback(1);
      ClientRequestBuilder.start(clientEventLoop).get(path).server("127.0.0.1", options.port()).execute(callback);
      callback.awaitDone();
      final var results = callback.output();
      Assert.assertEquals(1, results.size());
      return results.get(0);
    } finally {
      clientEventLoop.shutdownGracefully();
    }
  }

  public static String execute_simple_post(final ServerOptions options, final String path, final String data) {
    final EventLoopGroup clientEventLoop = new NioEventLoopGroup();
    try {
      final var callback = new MockClientCallback(1);
      ClientRequestBuilder.start(clientEventLoop).post(path, data).server("127.0.0.1", options.port()).execute(callback);
      callback.awaitDone();
      final var results = callback.output();
      Assert.assertEquals(1, results.size());
      return results.get(0);
    } finally {
      clientEventLoop.shutdownGracefully();
    }
  }

  @Test
  public void test_crash1() throws Exception {
    final var options = new CliServerOptions("--port", "9961");
    final var runnable = new ServerRunnable(ServerRunnableTests.nexus(options));
    final var thread = new Thread(runnable);
    thread.start();
    Assert.assertTrue(runnable.waitForReady(10000));
    Assert.assertTrue(runnable.isAccepting());
    ServerChannelInitializerTests.executeHealthCheck(options);
    final var result = execute_simple_post(options, "/wut", "{\"crash\":true}");
    Assert.assertEquals("DATA:{\"error\":5500}", result);
    runnable.shutdown();
    thread.join();
  }

  @Test
  public void test_crash2() throws Exception {
    final var options = new CliServerOptions("--port", "9964");
    final var runnable = new ServerRunnable(ServerRunnableTests.nexus(options));
    final var thread = new Thread(runnable);
    thread.start();
    Assert.assertTrue(runnable.waitForReady(10000));
    Assert.assertTrue(runnable.isAccepting());
    ServerChannelInitializerTests.executeHealthCheck(options);
    final var result = execute_simple(options, "/crash");
    Assert.assertEquals("DATA:Error:500 Internal Server Error", result);
    runnable.shutdown();
    thread.join();
  }

  @Test
  public void test_error() throws Exception {
    final var options = new CliServerOptions("--port", "9963");
    final var runnable = new ServerRunnable(ServerRunnableTests.nexus(options));
    final var thread = new Thread(runnable);
    thread.start();
    Assert.assertTrue(runnable.waitForReady(10000));
    Assert.assertTrue(runnable.isAccepting());
    ServerChannelInitializerTests.executeHealthCheck(options);
    final var result = execute_simple_post(options, "/wut", "{\"error\":true}");
    Assert.assertEquals("DATA:{\"error\":13}", result);
    runnable.shutdown();
    thread.join();
  }

  @Test
  public void test_not_found() throws Exception {
    final var options = new CliServerOptions("--port", "9962");
    final var runnable = new ServerRunnable(ServerRunnableTests.nexus(options));
    final var thread = new Thread(runnable);
    thread.start();
    Assert.assertTrue(runnable.waitForReady(10000));
    Assert.assertTrue(runnable.isAccepting());
    ServerChannelInitializerTests.executeHealthCheck(options);
    final var result = execute_simple(options, "/404");
    Assert.assertEquals("DATA:Error:404 Not Found", result);
    runnable.shutdown();
    thread.join();
  }

  @Test
  public void test_static() throws Exception {
    final var options = new CliServerOptions("--port", "9960");
    final var runnable = new ServerRunnable(ServerRunnableTests.nexus(options));
    final var thread = new Thread(runnable);
    thread.start();
    Assert.assertTrue(runnable.waitForReady(10000));
    Assert.assertTrue(runnable.isAccepting());
    ServerChannelInitializerTests.executeHealthCheck(options);
    final var result = execute_simple(options, "/hello");
    Assert.assertEquals("DATA:Hello World", result);
    runnable.shutdown();
    thread.join();
  }
}
