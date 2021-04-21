package org.adamalang.api.session;

import org.adamalang.runtime.natives.NtClient;

import java.util.ArrayList;
import java.util.HashMap;

/** session of a connected user */
public class UserSession implements Session {
  /** who is the user behind this session */
  private final NtClient who;

  /** is the session still alive/connected */
  private boolean alive;

  /** the attached resources to the session */
  private final HashMap<Integer, Resource> resources;

  public UserSession(final NtClient who) {
    this.who = who;
    alive = true;
    resources = new HashMap<>();
  }

  @Override
  public NtClient who() {
    return who;
  }

  /** is the session still alive? */
  public synchronized boolean alive() {
    return alive;
  }

  /** indicate the session is no more... */
  @Override
  public void kill() {
    for (final Resource event : killUnderLock()) {
      event.end();
    }
  }

  /** see: kill; this has a lock and returns the events to run. can't be null. */
  private synchronized ArrayList<Resource> killUnderLock() {
    alive = false;
    final var copy = new ArrayList<>(resources.values());
    resources.clear();
    return copy;
  }

  /** subscribe to the given session's end of life (i.e. death event)
   *
   * @param event the event to fire when the session goes "poof" */
  @Override
  public void attach(int id, final Resource event) {
    final var dead = attachUnderLock(id, event);
    if (dead != null) {
      dead.end();
    }
  }

  /** see: subscribeToSessionDeath; this has a lock and returns the events to run.
   * maybe null. */
  private synchronized Resource attachUnderLock(int id, final Resource event) {
    if (!alive) {
      return event;
    } else {
      resources.put(id, event);
      return null;
    }
  }

  /** unbind the given id */
  @Override
  public boolean detach(int id) {
    Resource event = detachUnderLock(id);
    if (event != null) {
      event.end();
      return true;
    } else {
      return false;
    }
  }

  /** unbind an id under a lock */
  public synchronized Resource detachUnderLock(int id) {
    return resources.remove(id);
  }

}
