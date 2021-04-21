package org.adamalang.api.auth;

/** service to asynchronously authenticate users by various forms */
public interface AuthenticatorService {
  /** authenticate by token */
  public void authenticateByToken(String token, AuthenticatorCallback callback);

  /** authenticate by email and password */
  public void authenticateByEmail(String email, String password, AuthenticatorCallback callback);
}
