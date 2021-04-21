/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * which is in the root directory of the repository. This file is part of the 'Adama'
 * project which is a programming language and document store for board games.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2021 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.netty;

import java.io.File;
import java.util.concurrent.TimeUnit;
import org.adamalang.netty.api.GameSpaceDB;
import org.adamalang.netty.client.AdamaCookieCodec;
import org.adamalang.netty.client.ClientRequestBuilder;
import org.adamalang.netty.client.MockClient;
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
    return new ServerNexus(options, db, new ServiceHandler(db), new MockAuthenticator(), new MockStaticSite(), null);
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
  public void forceClean() {
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
    final var callback = new MockClient();
    final var b = ClientRequestBuilder.start(clientEventLoop).server("localhost", options.port()).get(options.websocketPath()).header("cookie", AdamaCookieCodec.client(AdamaCookieCodec.ADAMA_AUTH_COOKIE_NAME, "XOK")).withWebSocket();
    b.execute(callback);
    callback.awaitFirst();
    MockClient.IdAccum id1 = callback.camp(1);
    MockClient.IdAccum id2 = callback.camp(2);
    MockClient.IdAccum id3 = callback.camp(3);
    MockClient.IdAccum id4 = callback.camp(4);
    b.channel().writeAndFlush(new TextWebSocketFrame("{\"id\":1,\"method\":\"create\",\"space\":\"Demo_ServiceHandler_success\",\"key\":\"3\",\"arg\":{}}"));
    id1.done.await(1000, TimeUnit.MILLISECONDS);
    id1.assertOnce("{\"deliver\":1,\"done\":true,\"response\":{\"key\":\"3\",\"seq\":2}}");
    b.channel().writeAndFlush(new TextWebSocketFrame("{\"id\":2,\"method\":\"connect\",\"space\":\"Demo_ServiceHandler_success\",\"key\":\"3\"}"));
    id2.first.await(1000, TimeUnit.MILLISECONDS);
    id2.assertOnce("{\"deliver\":2,\"done\":false,\"response\":{\"data\":{\"x\":123},\"outstanding\":[],\"blockers\":[],\"seq\":5}}");
    b.channel().writeAndFlush(new TextWebSocketFrame("{\"id\":3,\"space\":\"Demo_ServiceHandler_success\",\"method\":\"send\",\"marker\":\"moo\",\"channel\":\"change\",\"message\":{\"dx\":7},\"key\":\"3\"}"));
    id3.done.await(1000, TimeUnit.MILLISECONDS);
    id3.assertOnce("{\"deliver\":3,\"done\":true,\"response\":{\"seq\":7}}");
    id2.done.await(1000, TimeUnit.MILLISECONDS);
    id2.assertLast(2, "{\"deliver\":2,\"done\":false,\"response\":{\"data\":{\"x\":130},\"outstanding\":[],\"blockers\":[],\"seq\":7}}");
    b.channel().writeAndFlush(new TextWebSocketFrame("{\"id\":4,\"space\":\"Demo_ServiceHandler_success\",\"method\":\"reflect\"}"));
    id4.done.await(1000, TimeUnit.MILLISECONDS);
    id4.assertOnce("{\"deliver\":4,\"done\":true,\"response\":{\"result\":{\"types\":{\"#root\":{\"nature\":\"reactive_record\",\"name\":\"Root\",\"fields\":{\"x\":{\"type\":{\"nature\":\"reactive_value\",\"type\":\"int\"},\"privacy\":\"public\"}}},\"__ViewerType\":{\"nature\":\"native_message\",\"name\":\"__ViewerType\",\"anonymous\":true,\"fields\":{}},\"M\":{\"nature\":\"native_message\",\"name\":\"M\",\"anonymous\":false,\"fields\":{\"dx\":{\"type\":{\"nature\":\"native_value\",\"type\":\"int\"},\"privacy\":\"public\"}}}},\"channels\":{\"change\":\"M\"},\"constructors\":[],\"labels\":[]}}}");
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
    b.channel().writeAndFlush(new TextWebSocketFrame("{\"id\":1,\"space\":\"Demo_Bomb_success\",\"method\":\"connect\",\"key\":\"123\"}"));
    callback.awaitDone();
    final var output = callback.output();
    Assert.assertEquals(2, output.size());
    Assert.assertEquals("DATA:{\"failure\":1,\"reason\":4005}", output.get(1));
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
    b.channel().writeAndFlush(new TextWebSocketFrame("{\"id\":1,\"method\":\"create\",\"space\":\"Demo_Bomb_success\",\"key\":\"1\",\"arg\":{}}"));
    b.channel().writeAndFlush(new TextWebSocketFrame("{\"id\":2,\"space\":\"Demo_Bomb_success\",\"method\":\"connect\",\"key\":\"1\"}"));
    callback.awaitDone();
    final var output = callback.output();
    Assert.assertEquals(3, output.size());
    Assert.assertEquals("DATA:{\"deliver\":1,\"done\":true,\"response\":{\"key\":\"1\",\"seq\":3}}", output.get(1));
    Assert.assertEquals("DATA:{\"deliver\":2,\"done\":false,\"response\":{\"data\":{\"x\":\"Tick\"},\"outstanding\":[],\"blockers\":[],\"seq\":6}}", output.get(2));
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
    final var callback = new MockClientCallback(2);
    final var first = callback.latchAt(1);
    final var b = ClientRequestBuilder.start(clientEventLoop).server("localhost", options.port()).get(options.websocketPath()).header("cookie", AdamaCookieCodec.client(AdamaCookieCodec.ADAMA_AUTH_COOKIE_NAME, "XOK")).withWebSocket();
    b.execute(callback);
    first.await(2000, TimeUnit.MILLISECONDS);
    b.channel().writeAndFlush(new TextWebSocketFrame("{\"id\":1,\"method\":\"create\",\"space\":\"Operational_Goodwill_failure\",\"key\":\"5\",\"arg\":{}}"));
    callback.awaitDone();
    final var output = callback.output();
    Assert.assertEquals(2, output.size());
    Assert.assertEquals("DATA:{\"deliver\":1,\"done\":true,\"response\":{\"key\":\"5\",\"seq\":2}}", output.get(1));
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
    b.channel().writeAndFlush(new TextWebSocketFrame("{\"id\":1,\"space\":\"NOOP\",\"method\":\"reserve\"}"));
    callback.awaitDone();
    final var output = callback.output();
    Assert.assertEquals(2, output.size());
    Assert.assertEquals("DATA:{\"failure\":1,\"reason\":40001}", output.get(1));
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
    b.channel().writeAndFlush(new TextWebSocketFrame("{\"id\":1,\"space\":\"\",\"method\":\"nerd\",\"key\":\"123\"}"));
    callback.awaitDone();
    final var output = callback.output();
    Assert.assertEquals(2, output.size());
    Assert.assertEquals("DATA:{\"failure\":1,\"reason\":40103}", output.get(1));
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
    Assert.assertEquals("DATA:{\"failure\":1,\"reason\":40100}", output.get(1));
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
    b.channel().writeAndFlush(new TextWebSocketFrame("{\"id\":1,\"space\":\"\"}"));
    callback.awaitDone();
    final var output = callback.output();
    Assert.assertEquals(2, output.size());
    Assert.assertEquals("DATA:{\"failure\":1,\"reason\":40102}", output.get(1));
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
    b.channel().writeAndFlush(new TextWebSocketFrame("{\"id\":1,\"space\":\"NOOP\",\"method\":\"create\"}"));
    callback.awaitDone();
    final var output = callback.output();
    Assert.assertEquals(2, output.size());
    Assert.assertEquals("DATA:{\"failure\":1,\"reason\":40000}", output.get(1));
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
    b.channel().writeAndFlush(new TextWebSocketFrame("{\"id\":1,\"space\":\"Demo_Bomb_success\",\"method\":\"send\",\"marker\":\"moo\",\"channel\":\"ch\",\"message\":{},\"key\":\"123\"}"));
    callback.awaitDone();
    final var output = callback.output();
    Assert.assertEquals(2, output.size());
    Assert.assertEquals("DATA:{\"failure\":1,\"reason\":4005}", output.get(1));
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
    b.channel().writeAndFlush(new TextWebSocketFrame("{\"id\":1,\"space\":\"Demo_Bomb_success\",\"method\":\"send\",\"marker\":\"moo\",\"key\":\"123\"}"));
    callback.awaitDone();
    final var output = callback.output();
    Assert.assertEquals(2, output.size());
    Assert.assertEquals("DATA:{\"failure\":1,\"reason\":40104}", output.get(1));
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
    b.channel().writeAndFlush(new TextWebSocketFrame("{\"id\":1,\"space\":\"Demo_Bomb_success\",\"method\":\"send\",\"channel\":\"ch\",\"marker\":\"moo\",\"messagex\":{},\"key\":\"123\"}"));
    callback.awaitDone();
    final var output = callback.output();
    Assert.assertEquals(2, output.size());
    Assert.assertEquals("DATA:{\"failure\":1,\"reason\":40105}", output.get(1));
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
    b.channel().writeAndFlush(new TextWebSocketFrame("{\"id\":1,\"method\":\"create\",\"space\":\"Demo_Bomb_success\",\"key\":\"4\",\"arg\":{}}"));
    b.channel().writeAndFlush(new TextWebSocketFrame("{\"id\":2,\"space\":\"Demo_Bomb_success\",\"marker\":\"moo\",\"method\":\"send\",\"channel\":\"ch\",\"message\":{},\"key\":\"4\"}"));
    callback.awaitDone();
    final var output = callback.output();
    Assert.assertEquals(3, output.size());
    Assert.assertEquals("DATA:{\"deliver\":1,\"done\":true,\"response\":{\"key\":\"4\",\"seq\":3}}", output.get(1));
    Assert.assertEquals("DATA:{\"failure\":2,\"reason\":2060}", output.get(2));
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
      b.channel().writeAndFlush(new TextWebSocketFrame("{\"id\":1,\"method\":\"create\",\"space\":\"Demo_Bomb_success\",\"key\":\"42\",\"arg\":{}}"));
      callback.awaitDone();
      final var output = callback.output();
      Assert.assertEquals(2, output.size());
      Assert.assertEquals("DATA:{\"deliver\":1,\"done\":true,\"response\":{\"key\":\"42\",\"seq\":3}}", output.get(1));
    }
    {
      Assert.assertTrue(runnable.waitForReady(10000));
      Assert.assertTrue(runnable.isAccepting());
      final var callback = new MockClientCallback(2);
      final var first = callback.latchAt(1);
      final var b = ClientRequestBuilder.start(clientEventLoop).server("localhost", options.port()).get(options.websocketPath()).header("cookie", AdamaCookieCodec.client(AdamaCookieCodec.ADAMA_AUTH_COOKIE_NAME, "XOK")).withWebSocket();
      b.execute(callback);
      first.await(2000, TimeUnit.MILLISECONDS);
      b.channel().writeAndFlush(new TextWebSocketFrame("{\"id\":1,\"method\":\"create\",\"space\":\"Demo_Bomb_success\",\"key\":\"42\",\"arg\":{}}"));
      callback.awaitDone();
      final var output = callback.output();
      Assert.assertEquals(2, output.size());
      Assert.assertEquals("DATA:{\"failure\":1,\"reason\":4010}", output.get(1));
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
    b.channel().writeAndFlush(new TextWebSocketFrame("{\"id\":1,\"method\":\"reserve\",\"space\":\"Demo_Bomb_success\"}"));
    callback.awaitDone();
    final var output = callback.output();
    Assert.assertEquals(2, output.size());
    Assert.assertTrue(output.get(1).startsWith("DATA:{\"deliver\":1,\"done\":true,\"response\":{\"key\":\""));
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
    b.channel().writeAndFlush(new TextWebSocketFrame("{\"id\":1,\"method\":\"create\",\"space\":\"Demo_Bomb_success\",\"key\":\"2\",\"arg\":{}}"));
    callback.awaitDone();
    final var output = callback.output();
    Assert.assertEquals(2, output.size());
    Assert.assertEquals("DATA:{\"deliver\":1,\"done\":true,\"response\":{\"key\":\"2\",\"seq\":3}}", output.get(1));
    cleanup();
  }

  @Test
  public void connectAndLeave() throws Exception {
    final var options = new CliServerOptions("--port", "9817");
    final var runnable = new ServerRunnable(ServiceHandlerTests.nexus(options));
    final var thread = new Thread(runnable);
    thread.start();
    Assert.assertTrue(runnable.waitForReady(10000));
    Assert.assertTrue(runnable.isAccepting());
    final EventLoopGroup clientEventLoop = new NioEventLoopGroup();
    final var callback = new MockClient();
    final var b = ClientRequestBuilder.start(clientEventLoop).server("localhost", options.port()).get(options.websocketPath()).header("cookie", AdamaCookieCodec.client(AdamaCookieCodec.ADAMA_AUTH_COOKIE_NAME, "XOK")).withWebSocket();
    b.execute(callback);
    callback.awaitFirst();
    MockClient.IdAccum id1 = callback.camp(1);
    MockClient.IdAccum id2 = callback.camp(2);
    MockClient.IdAccum id3 = callback.camp(3);
    b.channel().writeAndFlush(new TextWebSocketFrame("{\"id\":1,\"method\":\"create\",\"space\":\"Demo_Bomb_success\",\"key\":\"7\",\"arg\":{}}"));
    id1.done.await(1000, TimeUnit.MILLISECONDS);
    id1.assertOnce("{\"deliver\":1,\"done\":true,\"response\":{\"key\":\"7\",\"seq\":3}}");
    b.channel().writeAndFlush(new TextWebSocketFrame("{\"id\":2,\"method\":\"connect\",\"space\":\"Demo_Bomb_success\",\"key\":\"7\"}"));
    id2.first.await(1000, TimeUnit.MILLISECONDS);
    id2.assertOnce("{\"deliver\":2,\"done\":false,\"response\":{\"data\":{\"x\":\"Tick\"},\"outstanding\":[],\"blockers\":[],\"seq\":6}}");
    b.channel().writeAndFlush(new TextWebSocketFrame("{\"id\":3,\"method\":\"disconnect\",\"stream\":2,\"space\":\"Demo_Bomb_success\",\"key\":\"7\"}"));
    id3.done.await(1000, TimeUnit.MILLISECONDS);
    id3.assertOnce("{\"deliver\":3,\"done\":true,\"response\":{\"result\":true}}");
    id2.done.await(2000, TimeUnit.MILLISECONDS);
    id2.assertLast(2, "{\"deliver\":2,\"done\":true,\"response\":{}}");
    cleanup();
  }

  @Test
  public void idErrors() throws Exception {
    final var options = new CliServerOptions("--port", "9819");
    final var runnable = new ServerRunnable(ServiceHandlerTests.nexus(options));
    final var thread = new Thread(runnable);
    thread.start();
    Assert.assertTrue(runnable.waitForReady(10000));
    Assert.assertTrue(runnable.isAccepting());
    final EventLoopGroup clientEventLoop = new NioEventLoopGroup();
    final var callback = new MockClientCallback(4);
    final var first = callback.latchAt(1);
    final var b = ClientRequestBuilder.start(clientEventLoop).server("localhost", options.port()).get(options.websocketPath()).header("cookie", AdamaCookieCodec.client(AdamaCookieCodec.ADAMA_AUTH_COOKIE_NAME, "XOK")).withWebSocket();
    b.execute(callback);
    first.await(2000, TimeUnit.MILLISECONDS);
    b.channel().writeAndFlush(new TextWebSocketFrame("{\"id\":1,\"method\":\"connect\",\"space\":\"Demo_Bomb_success\",\"data\":{}}"));
    b.channel().writeAndFlush(new TextWebSocketFrame("{\"id\":2,\"method\":\"connect\",\"space\":\"Demo_Bomb_success\",\"key\":\"x\",\"data\":{}}"));
    b.channel().writeAndFlush(new TextWebSocketFrame("{\"id\":3,\"method\":\"connect\",\"space\":\"Demo_Bomb_success\",\"key\":true,\"data\":{}}"));
    callback.awaitDone();
    final var output = callback.output();
    Assert.assertEquals(4, output.size());
    Assert.assertEquals("DATA:{\"failure\":1,\"reason\":40101}", output.get(1));
    Assert.assertEquals("DATA:{\"failure\":2,\"reason\":40101}", output.get(2));
    Assert.assertEquals("DATA:{\"failure\":3,\"reason\":40101}", output.get(3));
    cleanup();
  }

  @Test
  public void doubleConnectViaDevKitWho() throws Exception {
    final var options = new CliServerOptions("--port", "9820");
    final var runnable = new ServerRunnable(ServiceHandlerTests.nexus(options));
    final var thread = new Thread(runnable);
    thread.start();
    Assert.assertTrue(runnable.waitForReady(10000));
    Assert.assertTrue(runnable.isAccepting());
    final EventLoopGroup clientEventLoop = new NioEventLoopGroup();
    final var callback = new MockClientCallback(6);
    final var first = callback.latchAt(1);
    final var b = ClientRequestBuilder.start(clientEventLoop).server("localhost", options.port()).get(options.websocketPath()).header("cookie", AdamaCookieCodec.client(AdamaCookieCodec.ADAMA_AUTH_COOKIE_NAME, "XOK")).withWebSocket();
    b.execute(callback);
    first.await(2000, TimeUnit.MILLISECONDS);
    b.channel().writeAndFlush(new TextWebSocketFrame("{\"id\":1,\"method\":\"create\",\"space\":\"Demo_Simple_success\",\"key\":\"8\",\"arg\":{}}"));
    b.channel().writeAndFlush(new TextWebSocketFrame("{\"id\":2,\"method\":\"connect\",\"space\":\"Demo_Simple_success\",\"key\":8}"));
    b.channel().writeAndFlush(new TextWebSocketFrame("{\"id\":3,\"method\":\"connect\",\"devkit_who\":{\"agent\":\"boss\",\"authority\":\"devkit\"},\"space\":\"Demo_Simple_success\",\"key\":\"8\"}"));
    callback.awaitDone();
    final var output = callback.output();
    Assert.assertEquals(6, output.size());
    Assert.assertEquals("DATA:{\"deliver\":1,\"done\":true,\"response\":{\"key\":\"8\",\"seq\":2}}", output.get(1));
    Assert.assertEquals("DATA:{\"deliver\":2,\"done\":false,\"response\":{\"data\":{\"k\":1},\"outstanding\":[],\"blockers\":[],\"seq\":5}}", output.get(2));
    Assert.assertEquals("DATA:{\"deliver\":2,\"done\":false,\"response\":{\"data\":{\"k\":2},\"outstanding\":[],\"blockers\":[],\"seq\":7}}", output.get(3));
    Assert.assertEquals("DATA:{\"deliver\":2,\"done\":false,\"response\":{\"data\":{},\"outstanding\":[],\"blockers\":[],\"seq\":8}}", output.get(4));
    Assert.assertEquals("DATA:{\"deliver\":3,\"done\":false,\"response\":{\"data\":{\"k\":2},\"outstanding\":[],\"blockers\":[],\"seq\":8}}", output.get(5));
    cleanup();
  }

  @Test
  public void devkitWhoWorks() throws Exception {
    final var options = new CliServerOptions("--port", "9521");
    final var runnable = new ServerRunnable(ServiceHandlerTests.nexus(options));
    final var thread = new Thread(runnable);
    thread.start();
    Assert.assertTrue(runnable.waitForReady(10000));
    Assert.assertTrue(runnable.isAccepting());
    final EventLoopGroup clientEventLoop = new NioEventLoopGroup();
    final var callback = new MockClient();
    final var b = ClientRequestBuilder.start(clientEventLoop).server("localhost", options.port()).get(options.websocketPath()).header("cookie", AdamaCookieCodec.client(AdamaCookieCodec.ADAMA_AUTH_COOKIE_NAME, "XOK")).withWebSocket();
    b.execute(callback);
    callback.awaitFirst();
    MockClient.IdAccum id1 = callback.camp(1);
    MockClient.IdAccum id2 = callback.camp(2);
    MockClient.IdAccum id3 = callback.camp(3);
    b.channel().writeAndFlush(new TextWebSocketFrame("{\"id\":1,\"method\":\"create\",\"space\":\"Demo_Simple_success\",\"key\":\"70\",\"arg\":{}}"));
    id1.done.await(1000, TimeUnit.MILLISECONDS);
    id1.assertOnce("{\"deliver\":1,\"done\":true,\"response\":{\"key\":\"70\",\"seq\":2}}");
    b.channel().writeAndFlush(new TextWebSocketFrame("{\"id\":2,\"method\":\"connect\",\"space\":\"Demo_Simple_success\",\"key\":\"70\"}"));
    id2.first.await(1000, TimeUnit.MILLISECONDS);
    id2.assertOnce("{\"deliver\":2,\"done\":false,\"response\":{\"data\":{\"k\":1},\"outstanding\":[],\"blockers\":[],\"seq\":5}}");
    b.channel().writeAndFlush(new TextWebSocketFrame("{\"id\":3,\"method\":\"connect\",\"devkit_who\":{\"agent\":\"boss\",\"authority\":\"devkit\"},\"space\":\"Demo_Simple_success\",\"key\":\"70\"}"));
    id3.done.await(1000, TimeUnit.MILLISECONDS);
    id3.assertOnce("{\"deliver\":3,\"done\":false,\"response\":{\"data\":{\"k\":2},\"outstanding\":[],\"blockers\":[],\"seq\":8}}");
    cleanup();
  }
}
