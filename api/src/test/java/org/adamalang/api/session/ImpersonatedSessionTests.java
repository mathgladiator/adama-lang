package org.adamalang.api.session;

import org.adamalang.runtime.natives.NtClient;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

public class ImpersonatedSessionTests {
  @Test
  public void happyflow() {
    final var sessionReal = new UserSession(NtClient.NO_ONE);
    final var session = new ImpersonatedSession(new NtClient("a", "a"), sessionReal);
    final var ai = new AtomicInteger(0);
    session.attach(1, () -> ai.getAndIncrement());
    Assert.assertEquals(0, ai.get());
    Assert.assertTrue(sessionReal.alive());
    session.kill();
    Assert.assertFalse(sessionReal.alive());
    Assert.assertEquals(1, ai.get());
    session.attach(2, () -> ai.getAndIncrement());
    Assert.assertEquals(2, ai.get());
  }

  @Test
  public void partial() {
    final var sessionReal = new UserSession(NtClient.NO_ONE);
    Assert.assertEquals(NtClient.NO_ONE, sessionReal.who());
    final var session = new ImpersonatedSession(new NtClient("a", "a"), sessionReal);
    Assert.assertNotEquals(NtClient.NO_ONE, session.who());
    final var ai = new AtomicInteger(0);
    session.attach(1, () -> ai.getAndIncrement());
    Assert.assertEquals(0, ai.get());
    Assert.assertTrue(sessionReal.alive());
    Assert.assertFalse(session.detach(2));
    Assert.assertEquals(0, ai.get());
    Assert.assertTrue(sessionReal.alive());
    Assert.assertTrue(session.detach(1));
    Assert.assertEquals(1, ai.get());
    Assert.assertTrue(sessionReal.alive());
    session.kill();
    Assert.assertFalse(sessionReal.alive());
    Assert.assertEquals(1, ai.get());
  }
}
