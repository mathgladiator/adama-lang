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

import java.util.ArrayList;
import java.util.HashMap;

import org.adamalang.api.session.UserSession;
import org.adamalang.runtime.natives.NtClient;

/** a session of a connected user */
public class AdamaSession {
  private boolean alive;
  private final HashMap<String, Runnable> onSessionDeath;
  public final NtClient who;
  public final UserSession session;

  public AdamaSession(final NtClient who) {
    alive = true;
    this.who = who;
    onSessionDeath = new HashMap<>();
    this.session = new UserSession(who);
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
    session.kill();
  }

  /** see: kill; this has a lock and returns the events to run. can't be null. */
  private synchronized ArrayList<Runnable> killUnderLock() {
    alive = false;
    final var copy = new ArrayList<>(onSessionDeath.values());
    onSessionDeath.clear();
    return copy;
  }

  /** subscribe to the given session's end of life (i.e. death event)
   *
   * @param event the event to fire when the session goes "poof" */
  public void subscribeToSessionDeath(String id, final Runnable event) {
    final var dead = subscribeToSessionDeathUnderLock(id, event);
    if (dead != null) {
      for (final Runnable deadEvent : dead) {
        deadEvent.run();
      }
    }
  }

  /** see: subscribeToSessionDeath; this has a lock and returns the events to run.
   * maybe null. */
  private synchronized ArrayList<Runnable> subscribeToSessionDeathUnderLock(String id, final Runnable event) {
    onSessionDeath.put(id, event);
    if (!alive) {
      final var copy = new ArrayList<>(onSessionDeath.values());
      onSessionDeath.clear();
      return copy;
    }
    return null;
  }

  public synchronized boolean checkNotUnique(String id) {
    return onSessionDeath.containsKey(id);
  }

  /** unbind the given id */
  public boolean unbind(String id) {
    Runnable event = unbindUnderLock(id);
    if (event != null) {
      event.run();
      return true;
    } else {
      return false;
    }
  }

  /** unbind an id under a lock */
  public synchronized Runnable unbindUnderLock(String id) {
    return onSessionDeath.remove(id);
  }
}
