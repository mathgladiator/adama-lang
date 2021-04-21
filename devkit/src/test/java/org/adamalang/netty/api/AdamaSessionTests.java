/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * which is in the root directory of the repository. This file is part of the 'Adama'
 * project which is a programming language and document store for board games.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2021 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.netty.api;

import java.util.concurrent.atomic.AtomicInteger;
import org.adamalang.runtime.natives.NtClient;
import org.junit.Assert;
import org.junit.Test;

public class AdamaSessionTests {
  @Test
  public void happyflow() {
    final var session = new AdamaSession(NtClient.NO_ONE);
    final var ai = new AtomicInteger(0);
    session.subscribeToSessionDeath("key1", () -> ai.getAndIncrement());
    Assert.assertEquals(0, ai.get());
    Assert.assertTrue(session.isAlive());
    session.kill();
    Assert.assertFalse(session.isAlive());
    Assert.assertEquals(1, ai.get());
    session.subscribeToSessionDeath("key2", () -> ai.getAndIncrement());
    Assert.assertEquals(2, ai.get());
  }

  @Test
  public void partial() {
    final var session = new AdamaSession(NtClient.NO_ONE);
    final var ai = new AtomicInteger(0);
    session.subscribeToSessionDeath("key1", () -> ai.getAndIncrement());
    Assert.assertEquals(0, ai.get());
    Assert.assertTrue(session.isAlive());
    Assert.assertFalse(session.unbind("x"));
    Assert.assertEquals(0, ai.get());
    Assert.assertTrue(session.isAlive());
    Assert.assertTrue(session.unbind("key1"));
    Assert.assertEquals(1, ai.get());
    Assert.assertTrue(session.isAlive());
    session.kill();
    Assert.assertFalse(session.isAlive());
    Assert.assertEquals(1, ai.get());
  }

}
