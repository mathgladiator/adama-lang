/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * which is in the root directory of the repository. This file is part of the 'Adama'
 * project which is a programming language and document store for board games.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2021 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.api.auth;

import org.adamalang.api.session.Session;
import org.adamalang.runtime.natives.NtClient;

/** service to asynchronously authenticate users by various forms */
public interface AuthenticatorService {
  /** authenticate by token */
  public void authenticateByToken(String token, AuthenticatorCallback callback);

  /** authenticate by email and password */
  public void authenticateByEmail(String email, String password, AuthenticatorCallback callback);

  /** authenticate an impersonation of another person */
  public void authenticateImpersonation(Session session, NtClient other, AuthenticatorCallback callback);
}
