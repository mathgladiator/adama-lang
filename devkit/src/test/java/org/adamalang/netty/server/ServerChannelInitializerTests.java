/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.netty.server;

import org.adamalang.netty.client.ClientRequestBuilder;
import org.adamalang.netty.client.MockClientCallback;
import org.adamalang.netty.contracts.ServerOptions;
import org.junit.Assert;
import org.junit.Test;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

public class ServerChannelInitializerTests {
  public static void executeHealthCheck(final ServerOptions options) {
    final EventLoopGroup clientEventLoop = new NioEventLoopGroup();
    try {
      for (var k = 0; k < 10; k++) {
        final var callback = new MockClientCallback(1);
        final var b = ClientRequestBuilder.start(clientEventLoop);
        // TEST: SSL
        b.get(options.healthCheckPath()).server("127.0.0.1", options.port()).execute(callback);
        callback.awaitDone();
        final var results = callback.output();
        if (results.get(0).startsWith("DATA:YES:")) {
          return;
        } else if (k + 1 == 10) {
          Assert.assertTrue(results.get(0).startsWith("DATA:YES:"));
        }
      }
    } finally {
      clientEventLoop.shutdownGracefully();
    }
  }

  @Test
  public void test_health_check() throws Exception {
    final var options = new CliServerOptions("--port", "9998");
    final var runnable = new ServerRunnable(ServerRunnableTests.nexus(options));
    final var thread = new Thread(runnable);
    thread.start();
    Assert.assertTrue(runnable.waitForReady(10000));
    Assert.assertTrue(runnable.isAccepting());
    executeHealthCheck(options);
    runnable.shutdown();
    thread.join();
  }

  @Test
  public void test_health_check_ssl() throws Exception {
    final var options = new CliServerOptions("--port", "9997", "--tls-file", "snakeoil-keystore.jks");
    final var runnable = new ServerRunnable(ServerRunnableTests.nexus(options));
    final var thread = new Thread(runnable);
    thread.start();
    Assert.assertTrue(runnable.waitForReady(10000));
    Assert.assertTrue(runnable.isAccepting());
    executeHealthCheck(options);
    runnable.shutdown();
    thread.join();
  }
}
