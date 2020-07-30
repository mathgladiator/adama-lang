/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.netty;

import java.io.File;
import java.util.concurrent.TimeUnit;
import org.adamalang.netty.api.GameSpaceDB;
import org.adamalang.netty.client.AdamaCookieCodec;
import org.adamalang.netty.client.ClientRequestBuilder;
import org.adamalang.netty.client.MockClientCallback;
import org.adamalang.netty.server.CliServerOptions;
import org.adamalang.netty.server.MockAuthenticator;
import org.adamalang.netty.server.MockStaticSite;
import org.adamalang.netty.server.ServerNexus;
import org.adamalang.netty.server.ServerRunnable;
import org.adamalang.runtime.contracts.TimeSource;
import org.adamalang.translator.env.CompilerOptions;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

public class ServiceHandlerTests {
  public static void cleanOnShutdown() {
    wipe(new File("./test_data"));
  }

  public static ServerNexus nexus(final CliServerOptions options) throws Exception {
    final var testData = new File("./test_data");
    testData.mkdir();
    wipe(testData);
    final var db = new GameSpaceDB(new File("./test_code"), testData, CompilerOptions.start().make(), TimeSource.REAL_TIME);
    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      try {
        db.close();
      } catch (final Exception e) {}
      cleanOnShutdown();
    }));
    return new ServerNexus(options, db, new ServiceHandler(db), new MockAuthenticator(), new MockStaticSite());
  }

  private static void wipe(final File root) {
    for (final File file : root.listFiles()) {
      if (file.isDirectory()) {
        wipe(file);
      }
      if (!file.delete()) {
        file.deleteOnExit();
      }
    }
  }

  @After
  public void cleanup() {
    cleanOnShutdown();
  }

  @Test
  public void connectAndSend() throws Exception {
    final var options = new CliServerOptions("--port", "9815");
    final var runnable = new ServerRunnable(ServiceHandlerTests.nexus(options));
    final var thread = new Thread(runnable);
    thread.start();
    Assert.assertTrue(runnable.waitForReady(10000));
    Assert.assertTrue(runnable.isAccepting());
    final EventLoopGroup clientEventLoop = new NioEventLoopGroup();
    final var callback = new MockClientCallback(5);
    final var first = callback.latchAt(1);
    final var b = ClientRequestBuilder.start(clientEventLoop).server("localhost", options.port()).get(options.websocketPath()).header("cookie", AdamaCookieCodec.client(AdamaCookieCodec.ADAMA_AUTH_COOKIE_NAME, "XOK")).withWebSocket();
    b.execute(callback);
    first.await(2000, TimeUnit.MILLISECONDS);
    b.channel().writeAndFlush(new TextWebSocketFrame("{\"id\":1,\"method\":\"create\",\"gamespace\":\"Demo_ServiceHandler_success.a\",\"game\":\"game-3\",\"data\":{}}"));
    b.channel().writeAndFlush(new TextWebSocketFrame("{\"id\":2,\"gamespace\":\"Demo_ServiceHandler_success.a\",\"method\":\"connect\",\"game\":\"game-3\"}"));
    b.channel().writeAndFlush(new TextWebSocketFrame("{\"id\":3,\"gamespace\":\"Demo_ServiceHandler_success.a\",\"method\":\"send\",\"channel\":\"change\",\"message\":{\"dx\":7},\"game\":\"game-3\"}"));
    callback.awaitDone();
    final var output = callback.output();
    Assert.assertEquals(5, output.size());
    Assert.assertEquals("DATA:{\"deliver\":1,\"done\":true,\"response\":{\"game\":\"game-3\"}}", output.get(1));
    Assert.assertEquals("DATA:{\"deliver\":2,\"done\":false,\"response\":{\"data\":{\"x\":123},\"outstanding\":[],\"blockers\":[],\"seq\":3}}", output.get(2));
    Assert.assertEquals("DATA:{\"deliver\":3,\"done\":true,\"response\":{\"success\":4}}", output.get(3));
    Assert.assertEquals("DATA:{\"deliver\":2,\"done\":false,\"response\":{\"data\":{\"x\":130},\"outstanding\":[],\"blockers\":[],\"seq\":5}}", output.get(4));
    cleanup();
  }

  @Test
  public void connectGameThatDoesntExist() throws Exception {
    final var options = new CliServerOptions("--port", "9812");
    final var runnable = new ServerRunnable(ServiceHandlerTests.nexus(options));
    final var thread = new Thread(runnable);
    thread.start();
    Assert.assertTrue(runnable.waitForReady(10000));
    Assert.assertTrue(runnable.isAccepting());
    final EventLoopGroup clientEventLoop = new NioEventLoopGroup();
    final var callback = new MockClientCallback(2);
    final var first = callback.latchAt(1);
    final var b = ClientRequestBuilder.start(clientEventLoop).server("localhost", options.port()).get(options.websocketPath()).header("cookie", AdamaCookieCodec.client(AdamaCookieCodec.ADAMA_AUTH_COOKIE_NAME, "XOK")).withWebSocket();
    b.execute(callback);
    first.await(2000, TimeUnit.MILLISECONDS);
    b.channel().writeAndFlush(new TextWebSocketFrame("{\"id\":1,\"gamespace\":\"Demo_Bomb_success.a\",\"method\":\"connect\",\"game\":\"123\"}"));
    callback.awaitDone();
    final var output = callback.output();
    Assert.assertEquals(2, output.size());
    Assert.assertEquals("DATA:{\"failure\":1,\"reason\":4011}", output.get(1));
  }

  @Test
  public void connectStream() throws Exception {
    final var options = new CliServerOptions("--port", "9813");
    final var runnable = new ServerRunnable(ServiceHandlerTests.nexus(options));
    final var thread = new Thread(runnable);
    thread.start();
    Assert.assertTrue(runnable.waitForReady(10000));
    Assert.assertTrue(runnable.isAccepting());
    final EventLoopGroup clientEventLoop = new NioEventLoopGroup();
    final var callback = new MockClientCallback(3);
    final var first = callback.latchAt(1);
    final var b = ClientRequestBuilder.start(clientEventLoop).server("localhost", options.port()).get(options.websocketPath()).header("cookie", AdamaCookieCodec.client(AdamaCookieCodec.ADAMA_AUTH_COOKIE_NAME, "XOK")).withWebSocket();
    b.execute(callback);
    first.await(2000, TimeUnit.MILLISECONDS);
    b.channel().writeAndFlush(new TextWebSocketFrame("{\"id\":1,\"method\":\"create\",\"gamespace\":\"Demo_Bomb_success.a\",\"game\":\"game-1\",\"data\":{}}"));
    b.channel().writeAndFlush(new TextWebSocketFrame("{\"id\":2,\"gamespace\":\"Demo_Bomb_success.a\",\"method\":\"connect\",\"game\":\"game-1\"}"));
    callback.awaitDone();
    final var output = callback.output();
    Assert.assertEquals(3, output.size());
    Assert.assertEquals("DATA:{\"deliver\":1,\"done\":true,\"response\":{\"game\":\"game-1\"}}", output.get(1));
    Assert.assertEquals("DATA:{\"deliver\":2,\"done\":false,\"response\":{\"data\":{\"x\":\"Tick\"},\"outstanding\":[],\"blockers\":[],\"seq\":4}}", output.get(2));
  }

  @Test
  public void createBadGame() throws Exception {
    final var options = new CliServerOptions("--port", "9816");
    final var runnable = new ServerRunnable(ServiceHandlerTests.nexus(options));
    final var thread = new Thread(runnable);
    thread.start();
    Assert.assertTrue(runnable.waitForReady(10000));
    Assert.assertTrue(runnable.isAccepting());
    final EventLoopGroup clientEventLoop = new NioEventLoopGroup();
    final var callback = new MockClientCallback(3);
    final var first = callback.latchAt(1);
    final var b = ClientRequestBuilder.start(clientEventLoop).server("localhost", options.port()).get(options.websocketPath()).header("cookie", AdamaCookieCodec.client(AdamaCookieCodec.ADAMA_AUTH_COOKIE_NAME, "XOK")).withWebSocket();
    b.execute(callback);
    first.await(2000, TimeUnit.MILLISECONDS);
    b.channel().writeAndFlush(new TextWebSocketFrame("{\"id\":1,\"method\":\"create\",\"gamespace\":\"Operational_Goodwell_failure.a\",\"game\":\"game-4\",\"data\":{}}"));
    callback.awaitDone();
    final var output = callback.output();
    Assert.assertEquals(3, output.size());
    Assert.assertEquals("DATA:{\"deliver\":1,\"done\":true,\"response\":{\"game\":\"game-4\"}}", output.get(1));
    Assert.assertEquals("DATA:{\"failure\":1,\"reason\":5018}", output.get(2)); // read-timeout (trigger the exceptional case)
    cleanup();
  }

  @Test
  public void gamespaceDoesntExist() throws Exception {
    final var options = new CliServerOptions("--port", "9807");
    final var runnable = new ServerRunnable(ServiceHandlerTests.nexus(options));
    final var thread = new Thread(runnable);
    thread.start();
    Assert.assertTrue(runnable.waitForReady(10000));
    Assert.assertTrue(runnable.isAccepting());
    final EventLoopGroup clientEventLoop = new NioEventLoopGroup();
    final var callback = new MockClientCallback(2);
    final var first = callback.latchAt(1);
    final var b = ClientRequestBuilder.start(clientEventLoop).server("localhost", options.port()).get(options.websocketPath()).header("cookie", AdamaCookieCodec.client(AdamaCookieCodec.ADAMA_AUTH_COOKIE_NAME, "XOK")).withWebSocket();
    b.execute(callback);
    first.await(2000, TimeUnit.MILLISECONDS);
    b.channel().writeAndFlush(new TextWebSocketFrame("{\"id\":1,\"gamespace\":\"NOOP.a\",\"method\":\"generate\"}"));
    callback.awaitDone();
    final var output = callback.output();
    Assert.assertEquals(2, output.size());
    Assert.assertEquals("DATA:{\"failure\":1,\"reason\":4001}", output.get(1));
  }

  @Test
  public void invalidMethod() throws Exception {
    final var options = new CliServerOptions("--port", "9806");
    final var runnable = new ServerRunnable(ServiceHandlerTests.nexus(options));
    final var thread = new Thread(runnable);
    thread.start();
    Assert.assertTrue(runnable.waitForReady(10000));
    Assert.assertTrue(runnable.isAccepting());
    final EventLoopGroup clientEventLoop = new NioEventLoopGroup();
    final var callback = new MockClientCallback(2);
    final var first = callback.latchAt(1);
    final var b = ClientRequestBuilder.start(clientEventLoop).server("localhost", options.port()).get(options.websocketPath()).header("cookie", AdamaCookieCodec.client(AdamaCookieCodec.ADAMA_AUTH_COOKIE_NAME, "XOK")).withWebSocket();
    b.execute(callback);
    first.await(2000, TimeUnit.MILLISECONDS);
    b.channel().writeAndFlush(new TextWebSocketFrame("{\"id\":1,\"gamespace\":\"\",\"method\":\"nerd\",\"game\":\"123\"}"));
    callback.awaitDone();
    final var output = callback.output();
    Assert.assertEquals(2, output.size());
    Assert.assertEquals("DATA:{\"failure\":1,\"reason\":4007}", output.get(1));
  }

  @Test
  public void noGameSpace() throws Exception {
    final var options = new CliServerOptions("--port", "9804");
    final var runnable = new ServerRunnable(ServiceHandlerTests.nexus(options));
    final var thread = new Thread(runnable);
    thread.start();
    Assert.assertTrue(runnable.waitForReady(10000));
    Assert.assertTrue(runnable.isAccepting());
    final EventLoopGroup clientEventLoop = new NioEventLoopGroup();
    final var callback = new MockClientCallback(2);
    final var first = callback.latchAt(1);
    final var b = ClientRequestBuilder.start(clientEventLoop).server("localhost", options.port()).get(options.websocketPath()).header("cookie", AdamaCookieCodec.client(AdamaCookieCodec.ADAMA_AUTH_COOKIE_NAME, "XOK")).withWebSocket();
    b.execute(callback);
    first.await(2000, TimeUnit.MILLISECONDS);
    b.channel().writeAndFlush(new TextWebSocketFrame("{\"id\":1}"));
    callback.awaitDone();
    final var output = callback.output();
    Assert.assertEquals(2, output.size());
    Assert.assertEquals("DATA:{\"failure\":1,\"reason\":4003}", output.get(1));
  }

  @Test
  public void noMethod() throws Exception {
    final var options = new CliServerOptions("--port", "9805");
    final var runnable = new ServerRunnable(ServiceHandlerTests.nexus(options));
    final var thread = new Thread(runnable);
    thread.start();
    Assert.assertTrue(runnable.waitForReady(10000));
    Assert.assertTrue(runnable.isAccepting());
    final EventLoopGroup clientEventLoop = new NioEventLoopGroup();
    final var callback = new MockClientCallback(2);
    final var first = callback.latchAt(1);
    final var b = ClientRequestBuilder.start(clientEventLoop).server("localhost", options.port()).get(options.websocketPath()).header("cookie", AdamaCookieCodec.client(AdamaCookieCodec.ADAMA_AUTH_COOKIE_NAME, "XOK")).withWebSocket();
    b.execute(callback);
    first.await(2000, TimeUnit.MILLISECONDS);
    b.channel().writeAndFlush(new TextWebSocketFrame("{\"id\":1,\"gamespace\":\"\"}"));
    callback.awaitDone();
    final var output = callback.output();
    Assert.assertEquals(2, output.size());
    Assert.assertEquals("DATA:{\"failure\":1,\"reason\":4006}", output.get(1));
  }

  @Test
  public void noSession() throws Exception {
    final var options = new CliServerOptions("--port", "9808");
    final var runnable = new ServerRunnable(ServiceHandlerTests.nexus(options));
    final var thread = new Thread(runnable);
    thread.start();
    Assert.assertTrue(runnable.waitForReady(10000));
    Assert.assertTrue(runnable.isAccepting());
    final EventLoopGroup clientEventLoop = new NioEventLoopGroup();
    final var callback = new MockClientCallback(2);
    final var first = callback.latchAt(1);
    final var b = ClientRequestBuilder.start(clientEventLoop).server("localhost", options.port()).get(options.websocketPath()).header("cookie", AdamaCookieCodec.client(AdamaCookieCodec.ADAMA_AUTH_COOKIE_NAME, "Nope")).withWebSocket();
    b.execute(callback);
    first.await(2000, TimeUnit.MILLISECONDS);
    b.channel().writeAndFlush(new TextWebSocketFrame("{\"id\":1,\"gamespace\":\"NOOP.a\",\"method\":\"create\"}"));
    callback.awaitDone();
    final var output = callback.output();
    Assert.assertEquals(2, output.size());
    Assert.assertEquals("DATA:{\"failure\":1,\"reason\":4005}", output.get(1));
  }

  @Test
  public void sendMessageNoGameDoestExist() throws Exception {
    final var options = new CliServerOptions("--port", "9811");
    final var runnable = new ServerRunnable(ServiceHandlerTests.nexus(options));
    final var thread = new Thread(runnable);
    thread.start();
    Assert.assertTrue(runnable.waitForReady(10000));
    Assert.assertTrue(runnable.isAccepting());
    final EventLoopGroup clientEventLoop = new NioEventLoopGroup();
    final var callback = new MockClientCallback(2);
    final var first = callback.latchAt(1);
    final var b = ClientRequestBuilder.start(clientEventLoop).server("localhost", options.port()).get(options.websocketPath()).header("cookie", AdamaCookieCodec.client(AdamaCookieCodec.ADAMA_AUTH_COOKIE_NAME, "XOK")).withWebSocket();
    b.execute(callback);
    first.await(2000, TimeUnit.MILLISECONDS);
    b.channel().writeAndFlush(new TextWebSocketFrame("{\"id\":1,\"gamespace\":\"Demo_Bomb_success.a\",\"method\":\"send\",\"channel\":\"ch\",\"message\":{},\"game\":\"123\"}"));
    callback.awaitDone();
    final var output = callback.output();
    Assert.assertEquals(2, output.size());
    Assert.assertEquals("DATA:{\"failure\":1,\"reason\":4011}", output.get(1));
  }

  @Test
  public void sendNoChannel() throws Exception {
    final var options = new CliServerOptions("--port", "9809");
    final var runnable = new ServerRunnable(ServiceHandlerTests.nexus(options));
    final var thread = new Thread(runnable);
    thread.start();
    Assert.assertTrue(runnable.waitForReady(10000));
    Assert.assertTrue(runnable.isAccepting());
    final EventLoopGroup clientEventLoop = new NioEventLoopGroup();
    final var callback = new MockClientCallback(2);
    final var first = callback.latchAt(1);
    final var b = ClientRequestBuilder.start(clientEventLoop).server("localhost", options.port()).get(options.websocketPath()).header("cookie", AdamaCookieCodec.client(AdamaCookieCodec.ADAMA_AUTH_COOKIE_NAME, "XOK")).withWebSocket();
    b.execute(callback);
    first.await(2000, TimeUnit.MILLISECONDS);
    b.channel().writeAndFlush(new TextWebSocketFrame("{\"id\":1,\"gamespace\":\"Demo_Bomb_success.a\",\"method\":\"send\",\"game\":\"123\"}"));
    callback.awaitDone();
    final var output = callback.output();
    Assert.assertEquals(2, output.size());
    Assert.assertEquals("DATA:{\"failure\":1,\"reason\":4009}", output.get(1));
  }

  @Test
  public void sendNoMessage() throws Exception {
    final var options = new CliServerOptions("--port", "9810");
    final var runnable = new ServerRunnable(ServiceHandlerTests.nexus(options));
    final var thread = new Thread(runnable);
    thread.start();
    Assert.assertTrue(runnable.waitForReady(10000));
    Assert.assertTrue(runnable.isAccepting());
    final EventLoopGroup clientEventLoop = new NioEventLoopGroup();
    final var callback = new MockClientCallback(2);
    final var first = callback.latchAt(1);
    final var b = ClientRequestBuilder.start(clientEventLoop).server("localhost", options.port()).get(options.websocketPath()).header("cookie", AdamaCookieCodec.client(AdamaCookieCodec.ADAMA_AUTH_COOKIE_NAME, "XOK")).withWebSocket();
    b.execute(callback);
    first.await(2000, TimeUnit.MILLISECONDS);
    b.channel().writeAndFlush(new TextWebSocketFrame("{\"id\":1,\"gamespace\":\"Demo_Bomb_success.a\",\"method\":\"send\",\"channel\":\"ch\",\"messagex\":{},\"game\":\"123\"}"));
    callback.awaitDone();
    final var output = callback.output();
    Assert.assertEquals(2, output.size());
    Assert.assertEquals("DATA:{\"failure\":1,\"reason\":4010}", output.get(1));
  }

  @Test
  public void sendToDisconnectedStream() throws Exception {
    final var options = new CliServerOptions("--port", "9814");
    final var runnable = new ServerRunnable(ServiceHandlerTests.nexus(options));
    final var thread = new Thread(runnable);
    thread.start();
    Assert.assertTrue(runnable.waitForReady(10000));
    Assert.assertTrue(runnable.isAccepting());
    final EventLoopGroup clientEventLoop = new NioEventLoopGroup();
    final var callback = new MockClientCallback(3);
    final var first = callback.latchAt(1);
    final var b = ClientRequestBuilder.start(clientEventLoop).server("localhost", options.port()).get(options.websocketPath()).header("cookie", AdamaCookieCodec.client(AdamaCookieCodec.ADAMA_AUTH_COOKIE_NAME, "XOK")).withWebSocket();
    b.execute(callback);
    first.await(2000, TimeUnit.MILLISECONDS);
    b.channel().writeAndFlush(new TextWebSocketFrame("{\"id\":1,\"method\":\"create\",\"gamespace\":\"Demo_Bomb_success.a\",\"game\":\"game-2\",\"data\":{}}"));
    b.channel().writeAndFlush(new TextWebSocketFrame("{\"id\":2,\"gamespace\":\"Demo_Bomb_success.a\",\"method\":\"send\",\"channel\":\"ch\",\"message\":{},\"game\":\"game-2\"}"));
    callback.awaitDone();
    final var output = callback.output();
    Assert.assertEquals(3, output.size());
    Assert.assertEquals("DATA:{\"deliver\":1,\"done\":true,\"response\":{\"game\":\"game-2\"}}", output.get(1));
    Assert.assertEquals("DATA:{\"failure\":2,\"reason\":5015}", output.get(2));
  }

  @Test
  public void startGameAlreadyExists() throws Exception {
    final var options = new CliServerOptions("--port", "9803");
    final var runnable = new ServerRunnable(ServiceHandlerTests.nexus(options));
    final var thread = new Thread(runnable);
    thread.start();
    final EventLoopGroup clientEventLoop = new NioEventLoopGroup();
    {
      Assert.assertTrue(runnable.waitForReady(10000));
      Assert.assertTrue(runnable.isAccepting());
      final var callback = new MockClientCallback(2);
      final var first = callback.latchAt(1);
      final var b = ClientRequestBuilder.start(clientEventLoop).server("localhost", options.port()).get(options.websocketPath()).header("cookie", AdamaCookieCodec.client(AdamaCookieCodec.ADAMA_AUTH_COOKIE_NAME, "XOK")).withWebSocket();
      b.execute(callback);
      first.await(2000, TimeUnit.MILLISECONDS);
      b.channel().writeAndFlush(new TextWebSocketFrame("{\"id\":1,\"method\":\"create\",\"gamespace\":\"Demo_Bomb_success.a\",\"game\":\"myid\",\"data\":{}}"));
      callback.awaitDone();
      final var output = callback.output();
      Assert.assertEquals(2, output.size());
      Assert.assertEquals("DATA:{\"deliver\":1,\"done\":true,\"response\":{\"game\":\"myid\"}}", output.get(1));
    }
    {
      Assert.assertTrue(runnable.waitForReady(10000));
      Assert.assertTrue(runnable.isAccepting());
      final var callback = new MockClientCallback(2);
      final var first = callback.latchAt(1);
      final var b = ClientRequestBuilder.start(clientEventLoop).server("localhost", options.port()).get(options.websocketPath()).header("cookie", AdamaCookieCodec.client(AdamaCookieCodec.ADAMA_AUTH_COOKIE_NAME, "XOK")).withWebSocket();
      b.execute(callback);
      first.await(2000, TimeUnit.MILLISECONDS);
      b.channel().writeAndFlush(new TextWebSocketFrame("{\"id\":1,\"method\":\"create\",\"gamespace\":\"Demo_Bomb_success.a\",\"game\":\"myid\",\"data\":{}}"));
      callback.awaitDone();
      final var output = callback.output();
      Assert.assertEquals(2, output.size());
      Assert.assertEquals("DATA:{\"failure\":1,\"reason\":4008}", output.get(1));
    }
  }

  @Test
  public void startGameInventId() throws Exception {
    final var options = new CliServerOptions("--port", "9801");
    final var nexus = ServiceHandlerTests.nexus(options);
    final var runnable = new ServerRunnable(nexus);
    final var thread = new Thread(runnable);
    thread.start();
    Assert.assertTrue(runnable.waitForReady(10000));
    Assert.assertTrue(runnable.isAccepting());
    final EventLoopGroup clientEventLoop = new NioEventLoopGroup();
    final var callback = new MockClientCallback(2);
    final var first = callback.latchAt(1);
    final var b = ClientRequestBuilder.start(clientEventLoop).server("localhost", options.port()).get(options.websocketPath()).header("cookie", AdamaCookieCodec.client(AdamaCookieCodec.ADAMA_AUTH_COOKIE_NAME, "XOK")).withWebSocket();
    b.execute(callback);
    first.await(2000, TimeUnit.MILLISECONDS);
    b.channel().writeAndFlush(new TextWebSocketFrame("{\"id\":1,\"method\":\"generate\",\"gamespace\":\"Demo_Bomb_success.a\",\"data\":{}}"));
    callback.awaitDone();
    final var output = callback.output();
    Assert.assertEquals(2, output.size());
    Assert.assertTrue(output.get(1).startsWith("DATA:{\"deliver\":1,\"done\":true,\"response\":{\"game\":\""));
    nexus.shutdown();
    cleanup();
  }

  @Test
  public void startGameProvideId() throws Exception {
    final var options = new CliServerOptions("--port", "9802");
    final var runnable = new ServerRunnable(ServiceHandlerTests.nexus(options));
    final var thread = new Thread(runnable);
    thread.start();
    Assert.assertTrue(runnable.waitForReady(10000));
    Assert.assertTrue(runnable.isAccepting());
    final EventLoopGroup clientEventLoop = new NioEventLoopGroup();
    final var callback = new MockClientCallback(2);
    final var first = callback.latchAt(1);
    final var b = ClientRequestBuilder.start(clientEventLoop).server("localhost", options.port()).get(options.websocketPath()).header("cookie", AdamaCookieCodec.client(AdamaCookieCodec.ADAMA_AUTH_COOKIE_NAME, "XOK")).withWebSocket();
    b.execute(callback);
    first.await(2000, TimeUnit.MILLISECONDS);
    b.channel().writeAndFlush(new TextWebSocketFrame("{\"id\":1,\"method\":\"create\",\"gamespace\":\"Demo_Bomb_success.a\",\"game\":\"myid2\",\"data\":{}}"));
    callback.awaitDone();
    final var output = callback.output();
    Assert.assertEquals(2, output.size());
    Assert.assertEquals("DATA:{\"deliver\":1,\"done\":true,\"response\":{\"game\":\"myid2\"}}", output.get(1));
    cleanup();
  }
}
