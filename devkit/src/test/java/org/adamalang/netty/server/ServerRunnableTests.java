/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.netty.server;

import java.io.File;
import org.adamalang.netty.api.GameSpaceDB;
import org.adamalang.runtime.contracts.TimeSource;
import org.adamalang.translator.env.CompilerOptions;
import org.junit.Assert;
import org.junit.Test;

public class ServerRunnableTests {
  public static ServerNexus nexus(final CliServerOptions options) throws Exception {
    final var db = new GameSpaceDB(new File("./test_code"), new File("./test_data"), CompilerOptions.start().make(), TimeSource.REAL_TIME);
    return new ServerNexus(options, db, new MockJsonHandler(), new MockAuthenticator(), new MockStaticSite());
  }

  @Test
  public void test_interrupt() throws Exception {
    final var options = new CliServerOptions("--port", "9993");
    final var runnable = new ServerRunnable(nexus(options));
    final var thread = new Thread(runnable);
    thread.start();
    Assert.assertTrue(runnable.waitForReady(10000));
    Assert.assertTrue(runnable.isAccepting());
    thread.interrupt();
    thread.join();
  }

  @Test
  public void test_shutdown() throws Exception {
    final var options = new CliServerOptions("--port", "9995");
    final var runnable = new ServerRunnable(nexus(options));
    final var thread = new Thread(runnable);
    thread.start();
    Assert.assertTrue(runnable.waitForReady(10000));
    Assert.assertTrue(runnable.isAccepting());
    runnable.shutdown();
    thread.join();
  }

  @Test
  public void test_tight_shutdown() throws Exception {
    final var options = new CliServerOptions("--port", "9994");
    final var runnable = new ServerRunnable(nexus(options));
    runnable.shutdown();
    final var thread = new Thread(runnable);
    thread.start();
    thread.join();
  }
}
