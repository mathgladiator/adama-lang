/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.netty.server;

import java.io.File;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import org.adamalang.netty.api.AdamaSession;
import org.adamalang.netty.api.GameSpaceDB;
import org.adamalang.netty.client.AdamaCookieCodec;
import org.adamalang.netty.client.ClientRequestBuilder;
import org.adamalang.netty.client.MockClientCallback;
import org.adamalang.netty.contracts.AuthCallback;
import org.adamalang.netty.contracts.Authenticator;
import org.adamalang.runtime.contracts.TimeSource;
import org.adamalang.runtime.natives.NtClient;
import org.adamalang.translator.env.CompilerOptions;
import org.junit.Assert;
import org.junit.Test;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

public class WebSocketHandlerTests {
  @Test
  public void authGood() throws Exception {
    final var options = new CliServerOptions("--port", "9957");
    final var runnable = new ServerRunnable(ServerRunnableTests.nexus(options));
    final var thread = new Thread(runnable);
    thread.start();
    Assert.assertTrue(runnable.waitForReady(10000));
    Assert.assertTrue(runnable.isAccepting());
    final EventLoopGroup clientEventLoop = new NioEventLoopGroup();
    final var callback = new MockClientCallback(1);
    ClientRequestBuilder.start(clientEventLoop).server("localhost", 9957).get(options.websocketPath()).header("cookie", AdamaCookieCodec.client(AdamaCookieCodec.ADAMA_AUTH_COOKIE_NAME, "XOK")).withWebSocket().execute(callback);
    callback.awaitDone();
    final var output = callback.output();
    Assert.assertEquals(1, output.size());
    Assert.assertEquals("DATA:{\"signal\":\"setup\",\"status\":\"connected\"}", output.get(0));
  }

  @Test
  public void cantDealWithBinaryFrame() throws Exception {
    final var options = new CliServerOptions("--port", "9956");
    final var runnable = new ServerRunnable(ServerRunnableTests.nexus(options));
    final var thread = new Thread(runnable);
    thread.start();
    Assert.assertTrue(runnable.waitForReady(10000));
    Assert.assertTrue(runnable.isAccepting());
    final EventLoopGroup clientEventLoop = new NioEventLoopGroup();
    final var callback = new MockClientCallback(2);
    final var first = callback.latchAt(1);
    final var b = ClientRequestBuilder.start(clientEventLoop).server("localhost", 9956).get(options.websocketPath()).header("cookie", AdamaCookieCodec.client(AdamaCookieCodec.ADAMA_AUTH_COOKIE_NAME, "XOK")).withWebSocket();
    b.execute(callback);
    first.await(1000, TimeUnit.MILLISECONDS);
    b.channel().writeAndFlush(new BinaryWebSocketFrame());
    callback.awaitDone();
    final var output = callback.output();
    Assert.assertEquals(2, output.size());
    Assert.assertEquals("DATA:{\"signal\":\"setup\",\"status\":\"connected\"}", output.get(0));
    Assert.assertEquals("Closed", output.get(1));
  }

  @Test
  public void cookiePresentButBadName() throws Exception {
    final var options = new CliServerOptions("--port", "9949");
    final var runnable = new ServerRunnable(ServerRunnableTests.nexus(options));
    final var thread = new Thread(runnable);
    thread.start();
    Assert.assertTrue(runnable.waitForReady(10000));
    Assert.assertTrue(runnable.isAccepting());
    final EventLoopGroup clientEventLoop = new NioEventLoopGroup();
    final var callback = new MockClientCallback(1);
    ClientRequestBuilder.start(clientEventLoop).server("localhost", 9949).get(options.websocketPath()).header("cookie", AdamaCookieCodec.client("Woopx", "nope")).withWebSocket().execute(callback);
    callback.awaitDone();
    final var output = callback.output();
    Assert.assertEquals(1, output.size());
    Assert.assertEquals("DATA:{\"signal\":\"setup\",\"status\":\"failed_setup_no_cookie\"}", output.get(0));
  }

  @Test
  public void cookiePresentButWrong() throws Exception {
    final var options = new CliServerOptions("--port", "9958");
    final var runnable = new ServerRunnable(ServerRunnableTests.nexus(options));
    final var thread = new Thread(runnable);
    thread.start();
    Assert.assertTrue(runnable.waitForReady(10000));
    Assert.assertTrue(runnable.isAccepting());
    final EventLoopGroup clientEventLoop = new NioEventLoopGroup();
    final var callback = new MockClientCallback(1);
    ClientRequestBuilder.start(clientEventLoop).server("localhost", 9958).get(options.websocketPath()).header("cookie", AdamaCookieCodec.client(AdamaCookieCodec.ADAMA_AUTH_COOKIE_NAME, "nope")).withWebSocket().execute(callback);
    callback.awaitDone();
    final var output = callback.output();
    Assert.assertEquals(1, output.size());
    Assert.assertEquals("DATA:{\"signal\":\"setup\",\"status\":\"failed_auth\"}", output.get(0));
  }

  @Test
  public void handlerCrash() throws Exception {
    final var options = new CliServerOptions("--port", "9947");
    final var runnable = new ServerRunnable(ServerRunnableTests.nexus(options));
    final var thread = new Thread(runnable);
    thread.start();
    Assert.assertTrue(runnable.waitForReady(10000));
    Assert.assertTrue(runnable.isAccepting());
    final EventLoopGroup clientEventLoop = new NioEventLoopGroup();
    final var callback = new MockClientCallback(2);
    final var first = callback.latchAt(1);
    final var afterWrite = callback.latchAt(2);
    final var b = ClientRequestBuilder.start(clientEventLoop).server("localhost", 9947).get(options.websocketPath()).header("cookie", AdamaCookieCodec.client(AdamaCookieCodec.ADAMA_AUTH_COOKIE_NAME, "XOK")).withWebSocket();
    b.execute(callback);
    first.await(2000, TimeUnit.MILLISECONDS);
    b.channel().writeAndFlush(new TextWebSocketFrame("{\"id\":1,\"crash\":true}"));
    afterWrite.await(1000, TimeUnit.MILLISECONDS);
    final var output = callback.output();
    Assert.assertEquals(2, output.size());
    Assert.assertEquals("DATA:{\"signal\":\"setup\",\"status\":\"connected\"}", output.get(0));
    Assert.assertEquals("DATA:{\"failure\":1,\"reason\":5500}", output.get(1));
  }

  @Test
  public void handlerError() throws Exception {
    final var options = new CliServerOptions("--port", "9944");
    final var runnable = new ServerRunnable(ServerRunnableTests.nexus(options));
    final var thread = new Thread(runnable);
    thread.start();
    Assert.assertTrue(runnable.waitForReady(10000));
    Assert.assertTrue(runnable.isAccepting());
    final EventLoopGroup clientEventLoop = new NioEventLoopGroup();
    final var callback = new MockClientCallback(2);
    final var first = callback.latchAt(1);
    final var afterWrite = callback.latchAt(2);
    final var b = ClientRequestBuilder.start(clientEventLoop).server("localhost", 9944).get(options.websocketPath()).header("cookie", AdamaCookieCodec.client(AdamaCookieCodec.ADAMA_AUTH_COOKIE_NAME, "XOK")).withWebSocket();
    b.execute(callback);
    first.await(2000, TimeUnit.MILLISECONDS);
    b.channel().writeAndFlush(new TextWebSocketFrame("{\"id\":1,\"error\":true}"));
    afterWrite.await(1000, TimeUnit.MILLISECONDS);
    final var output = callback.output();
    Assert.assertEquals(2, output.size());
    Assert.assertEquals("DATA:{\"signal\":\"setup\",\"status\":\"connected\"}", output.get(0));
    Assert.assertEquals("DATA:{\"failure\":1,\"reason\":13}", output.get(1));
  }

  @Test
  public void handlerHappy() throws Exception {
    final var options = new CliServerOptions("--port", "9945");
    final var runnable = new ServerRunnable(ServerRunnableTests.nexus(options));
    final var thread = new Thread(runnable);
    thread.start();
    Assert.assertTrue(runnable.waitForReady(10000));
    Assert.assertTrue(runnable.isAccepting());
    final EventLoopGroup clientEventLoop = new NioEventLoopGroup();
    final var callback = new MockClientCallback(2);
    final var first = callback.latchAt(1);
    final var afterWrite = callback.latchAt(2);
    final var b = ClientRequestBuilder.start(clientEventLoop).server("localhost", 9945).get(options.websocketPath()).header("cookie", AdamaCookieCodec.client(AdamaCookieCodec.ADAMA_AUTH_COOKIE_NAME, "XOK")).withWebSocket();
    b.execute(callback);
    first.await(2000, TimeUnit.MILLISECONDS);
    b.channel().writeAndFlush(new TextWebSocketFrame("{\"id\":1,\"success\":true}"));
    afterWrite.await(1000, TimeUnit.MILLISECONDS);
    final var output = callback.output();
    Assert.assertEquals(2, output.size());
    Assert.assertEquals("DATA:{\"signal\":\"setup\",\"status\":\"connected\"}", output.get(0));
    Assert.assertEquals("DATA:{\"deliver\":1,\"done\":true,\"response\":{\"ok\":\"go\"}}", output.get(1));
  }

  @Test
  public void injectPong() throws Exception {
    final var options = new CliServerOptions("--port", "9920");
    final var runnable = new ServerRunnable(ServerRunnableTests.nexus(options));
    final var thread = new Thread(runnable);
    thread.start();
    Assert.assertTrue(runnable.waitForReady(10000));
    Assert.assertTrue(runnable.isAccepting());
    final EventLoopGroup clientEventLoop = new NioEventLoopGroup();
    final var callback = new MockClientCallback(1);
    final var first = callback.latchAt(1);
    final var b = ClientRequestBuilder.start(clientEventLoop).server("localhost", 9920).get(options.websocketPath()).header("cookie", AdamaCookieCodec.client(AdamaCookieCodec.ADAMA_AUTH_COOKIE_NAME, "XOK")).withWebSocket();
    b.execute(callback);
    first.await(2000, TimeUnit.MILLISECONDS);
    b.channel().writeAndFlush(new TextWebSocketFrame("{\"ping\":100,\"pong\":40}"));
    callback.ping.await(5000, TimeUnit.MILLISECONDS);
    final var output = callback.output();
    Assert.assertEquals(1, output.size());
    Assert.assertEquals("DATA:{\"signal\":\"setup\",\"status\":\"connected\"}", output.get(0));
  }

  @Test
  public void noCookie() throws Exception {
    final var options = new CliServerOptions("--port", "9959");
    final var runnable = new ServerRunnable(ServerRunnableTests.nexus(options));
    final var thread = new Thread(runnable);
    thread.start();
    Assert.assertTrue(runnable.waitForReady(10000));
    Assert.assertTrue(runnable.isAccepting());
    final EventLoopGroup clientEventLoop = new NioEventLoopGroup();
    final var callback = new MockClientCallback(1);
    ClientRequestBuilder.start(clientEventLoop).get(options.websocketPath()).withWebSocket().server("localhost", 9959).execute(callback);
    callback.awaitDone();
    final var output = callback.output();
    Assert.assertEquals(1, output.size());
    Assert.assertEquals("DATA:{\"signal\":\"setup\",\"status\":\"failed_setup_no_cookie\"}", output.get(0));
  }

  @Test
  public void noSessionYetSlowAuth() throws Exception {
    final var options = new CliServerOptions("--port", "9948");
    final var db = new GameSpaceDB(new File("./test_code"), new File("./test_data"), CompilerOptions.start().make(), TimeSource.REAL_TIME);
    final var callbackRef = new AtomicReference<AuthCallback>(null);
    final var latch = new CountDownLatch(1);
    final Authenticator slowAuthenticate = new Authenticator() {
      @Override
      public void authenticate(final String token, final AuthCallback callback) {
        callbackRef.set(callback);
        latch.countDown();
      }

      @Override
      public void close() {
      }
    };
    final var delay = new ServerNexus(options, db, new MockJsonHandler(), slowAuthenticate, new MockStaticSite());
    final var runnable = new ServerRunnable(delay);
    final var thread = new Thread(runnable);
    thread.start();
    Assert.assertTrue(runnable.waitForReady(10000));
    Assert.assertTrue(runnable.isAccepting());
    final EventLoopGroup clientEventLoop = new NioEventLoopGroup();
    final var callback = new MockClientCallback(2);
    final var first = callback.latchAt(1);
    final var b = ClientRequestBuilder.start(clientEventLoop).server("localhost", 9948).get(options.websocketPath()).header("cookie", AdamaCookieCodec.client(AdamaCookieCodec.ADAMA_AUTH_COOKIE_NAME, "XOK")).withWebSocket();
    b.execute(callback);
    Assert.assertTrue(latch.await(1000, TimeUnit.MILLISECONDS));
    b.channel().writeAndFlush(new TextWebSocketFrame("{\"id\":1,\"method\":\"get\",\"resource\":\"Demo_Bomb_success.a\",\"data\":{}}"));
    first.await(2000, TimeUnit.MILLISECONDS);
    callbackRef.get().success(new AdamaSession(NtClient.NO_ONE));
    callback.awaitDone();
    final var output = callback.output();
    Assert.assertEquals(2, output.size());
    Assert.assertEquals("DATA:{\"failure\":1,\"reason\":12}", output.get(0));
    Assert.assertEquals("DATA:{\"signal\":\"setup\",\"status\":\"connected\"}", output.get(1));
  }

  @Test
  public void sendWithoutId() throws Exception {
    final var options = new CliServerOptions("--port", "9955");
    final var runnable = new ServerRunnable(ServerRunnableTests.nexus(options));
    final var thread = new Thread(runnable);
    thread.start();
    Assert.assertTrue(runnable.waitForReady(10000));
    Assert.assertTrue(runnable.isAccepting());
    final EventLoopGroup clientEventLoop = new NioEventLoopGroup();
    final var callback = new MockClientCallback(2);
    final var first = callback.latchAt(1);
    final var afterWrite = callback.latchAt(2);
    final var b = ClientRequestBuilder.start(clientEventLoop).server("localhost", 9955).get(options.websocketPath()).header("cookie", AdamaCookieCodec.client(AdamaCookieCodec.ADAMA_AUTH_COOKIE_NAME, "XOK")).withWebSocket();
    b.execute(callback);
    first.await(1000, TimeUnit.MILLISECONDS);
    b.channel().writeAndFlush(new TextWebSocketFrame("{}"));
    afterWrite.await(1000, TimeUnit.MILLISECONDS);
    Assert.assertFalse(b.channel().isOpen());
  }

  @Test
  public void superRaceWithSlowAuthAndCrash() throws Exception {
    final var options = new CliServerOptions("--port", "9946");
    final var db = new GameSpaceDB(new File("./test_code"), new File("./test_data"), CompilerOptions.start().make(), TimeSource.REAL_TIME);
    final var callbackRef = new AtomicReference<AuthCallback>(null);
    final var latch = new CountDownLatch(1);
    final Authenticator slowAuthenticate = new Authenticator() {
      @Override
      public void authenticate(final String token, final AuthCallback callback) {
        callbackRef.set(callback);
        latch.countDown();
        throw new UnsupportedOperationException();
      }

      @Override
      public void close() {
      }
    };
    final var delay = new ServerNexus(options, db, new MockJsonHandler(), slowAuthenticate, new MockStaticSite());
    final var runnable = new ServerRunnable(delay);
    final var thread = new Thread(runnable);
    thread.start();
    Assert.assertTrue(runnable.waitForReady(10000));
    Assert.assertTrue(runnable.isAccepting());
    final EventLoopGroup clientEventLoop = new NioEventLoopGroup();
    final var callback = new MockClientCallback(1);
    final var latchForCrash = callback.latchAt(1);
    final var b = ClientRequestBuilder.start(clientEventLoop).server("localhost", 9946).get(options.websocketPath()).header("cookie", AdamaCookieCodec.client(AdamaCookieCodec.ADAMA_AUTH_COOKIE_NAME, "XOK")).withWebSocket();
    b.execute(callback);
    Assert.assertTrue(latch.await(1000, TimeUnit.MILLISECONDS));
    latchForCrash.await(1000, TimeUnit.MILLISECONDS);
    Assert.assertFalse(b.channel().isOpen());
    final var session = new AdamaSession(NtClient.NO_ONE);
    callbackRef.get().success(session);
    Assert.assertFalse(session.isAlive());
  }
}
