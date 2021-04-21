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
