/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * which is in the root directory of the repository. This file is part of the 'Adama'
 * project which is a programming language and document store for board games.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2021 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.api.session;

import org.adamalang.runtime.natives.NtClient;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

public class UserSessionTests {
  @Test
  public void happyflow() {
    final var session = new UserSession(NtClient.NO_ONE);
    final var ai = new AtomicInteger(0);
    session.attach(1, () -> ai.getAndIncrement());
    Assert.assertEquals(0, ai.get());
    Assert.assertTrue(session.alive());
    session.kill();
    Assert.assertFalse(session.alive());
    Assert.assertEquals(1, ai.get());
    session.attach(2, () -> ai.getAndIncrement());
    Assert.assertEquals(2, ai.get());
  }

  @Test
  public void partial() {
    final var session = new UserSession(NtClient.NO_ONE);
    Assert.assertEquals(NtClient.NO_ONE, session.who());
    final var ai = new AtomicInteger(0);
    session.attach(1, () -> ai.getAndIncrement());
    Assert.assertEquals(0, ai.get());
    Assert.assertTrue(session.alive());
    Assert.assertFalse(session.detach(2));
    Assert.assertEquals(0, ai.get());
    Assert.assertTrue(session.alive());
    Assert.assertTrue(session.detach(1));
    Assert.assertEquals(1, ai.get());
    Assert.assertTrue(session.alive());
    session.kill();
    Assert.assertFalse(session.alive());
    Assert.assertEquals(1, ai.get());
  }

}
