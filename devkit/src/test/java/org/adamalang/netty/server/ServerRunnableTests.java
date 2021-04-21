/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * which is in the root directory of the repository. This file is part of the 'Adama'
 * project which is a programming language and document store for board games.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2021 by Jeffrey M. Barber (http://jeffrey.io)
*/
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
    return new ServerNexus(options, db, new MockJsonHandler(), new MockAuthenticator(), new MockStaticSite(), null);
  }

  @Test
  public void test_interrupt() throws Exception {
    System.err.println("CONS:9993");
    final var options = new CliServerOptions("--port", "9993");
    final var runnable = new ServerRunnable(nexus(options));
    final var thread = new Thread(runnable);
    System.err.println("Start:9993");
    thread.start();
    Assert.assertTrue(runnable.waitForReady(2500));
    Assert.assertTrue(runnable.isAccepting());
    System.err.println("Interrupt:9993");
    thread.interrupt();
    System.err.println("Wait:9993");
    System.err.flush();
    thread.join();
    System.err.println("Finish:9993");
  }

  @Test
  public void test_shutdown() throws Exception {
    System.err.println("CONS:9995");
    final var options = new CliServerOptions("--port", "9995");
    final var runnable = new ServerRunnable(nexus(options));
    final var thread = new Thread(runnable);
    System.err.println("Start:9995");
    thread.start();
    Assert.assertTrue(runnable.waitForReady(10000));
    Assert.assertTrue(runnable.isAccepting());
    runnable.shutdown();
    System.err.println("Wait:9995");
    thread.join();
    System.err.println("Finish:9995");
  }

  @Test
  public void test_tight_shutdown() throws Exception {
    System.err.println("CONS:9994");
    final var options = new CliServerOptions("--port", "9994");
    final var runnable = new ServerRunnable(nexus(options));
    runnable.shutdown();
    final var thread = new Thread(runnable);
    System.err.println("Start:9994");
    thread.start();
    System.err.println("Wait:9994");
    thread.join();
    System.err.println("Finish:9994");
  }
}
