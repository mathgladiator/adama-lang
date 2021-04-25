/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * which is in the root directory of the repository. This file is part of the 'Adama'
 * project which is a programming language and document store for board games.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2021 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.data.disk;

import org.adamalang.api.auth.AuthenticatorCallback;
import org.adamalang.api.auth.AuthenticatorService;
import org.adamalang.api.session.Session;
import org.adamalang.runtime.natives.NtClient;

public class InsecureDiskAuthenticatorService implements AuthenticatorService {
  public InsecureDiskAuthenticatorService() {
    // TODO: scan the CSV
  }

  @Override
  public void authenticateByToken(String token, AuthenticatorCallback callback) {

  }

  @Override
  public void authenticateByEmail(String email, String password, AuthenticatorCallback callback) {
  }

  @Override
  public void authenticateImpersonation(Session session, NtClient other, AuthenticatorCallback callback) {
  }
}
