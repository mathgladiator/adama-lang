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

/** the results from Authenticating will fire on this callback class */
public interface AuthenticatorCallback {
  /** authentication went well, here is a session */
  public void success(String token, Session session);

  /** authentication went poorly */
  public void failure();
}
