package org.adamalang.api.session;

import org.adamalang.runtime.natives.NtClient;

/** session of a connected user */
public interface Session {
  public NtClient who();
  public void attach(int id, final Resource event);
  public void kill();
  public boolean detach(int id);
}
