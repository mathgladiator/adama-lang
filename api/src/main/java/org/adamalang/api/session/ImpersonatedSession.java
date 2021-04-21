package org.adamalang.api.session;

import org.adamalang.runtime.natives.NtClient;

public class ImpersonatedSession implements Session {
  private final NtClient who;
  private final UserSession session;

  public ImpersonatedSession(NtClient who, UserSession session) {
    this.who = who;
    this.session = session;
  }

  @Override
  public NtClient who() {
    return who;
  }

  @Override
  public void kill() {
    session.kill();
  }

  @Override
  public void attach(int id, Resource event) {
    session.attach(id, event);
  }

  @Override
  public boolean detach(int id) {
    return session.detach(id);
  }
}
