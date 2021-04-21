package org.adamalang.api.auth;

import org.adamalang.api.session.Session;

/** the results from Authenticating will fire on this callback class */
public interface AuthenticatorCallback {
  /** authentication went well, here is a session */
  public void success(String token, Session session);

  /** authentication went poorly */
  public void failure();
}
