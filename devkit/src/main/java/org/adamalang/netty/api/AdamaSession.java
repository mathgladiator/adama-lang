/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.netty.api;

import java.util.ArrayList;
import org.adamalang.runtime.natives.NtClient;

/** a session of a connected user */
public class AdamaSession {
  private boolean alive;
  private final ArrayList<Runnable> onSessionDeath;
  public final NtClient who;

  public AdamaSession(final NtClient who) {
    alive = true;
    this.who = who;
    onSessionDeath = new ArrayList<>();
  }

  /** is the session still alive? */
  public synchronized boolean isAlive() {
    return alive;
  }

  /** indicate the session is no more... */
  public void kill() {
    for (final Runnable event : killUnderLock()) {
      event.run();
    }
  }

  /** see: kill; this has a lock and returns the events to run. can't be null. */
  private synchronized ArrayList<Runnable> killUnderLock() {
    alive = false;
    final var copy = new ArrayList<>(onSessionDeath);
    onSessionDeath.clear();
    return copy;
  }

  /** subscribe to the given session's end of life (i.e. death event)
   *
   * @param event the event to fire when the session goes "poof" */
  public synchronized void subscribeToSessionDeath(final Runnable event) {
    final var dead = subscribeToSessionDeathUnderLock(event);
    if (dead != null) {
      for (final Runnable deadEvent : dead) {
        deadEvent.run();
      }
    }
  }

  /** see: subscribeToSessionDeath; this has a lock and returns the events to run.
   * maybe null. */
  private synchronized ArrayList<Runnable> subscribeToSessionDeathUnderLock(final Runnable event) {
    onSessionDeath.add(event);
    if (!alive) {
      final var copy = new ArrayList<>(onSessionDeath);
      onSessionDeath.clear();
      return copy;
    }
    return null;
  }
}
