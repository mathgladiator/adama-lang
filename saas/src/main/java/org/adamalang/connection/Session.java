/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.connection;

import org.adamalang.transforms.PerSessionAuthenticator;
import org.adamalang.transforms.results.AuthenticatedUser;

import java.util.HashMap;

public class Session {
  public final long created;
  private long lastActivity;
  public HashMap<String, AuthenticatedUser> identityCache;
  public final PerSessionAuthenticator authenticator;

  public Session(final PerSessionAuthenticator authenticator) {
    this.created = System.currentTimeMillis();
    this.identityCache = new HashMap<>();
    this.authenticator = authenticator;
  }

  public synchronized void activity() {
    lastActivity = System.currentTimeMillis();
  }

  public synchronized boolean keepalive() {
    long now = System.currentTimeMillis();
    long timeSinceCreation = now - created;
    long timeSinceLastActivity = now - lastActivity;
    return (timeSinceCreation <= 5 * 60000) || (timeSinceLastActivity <= 2 * 60 * 60000);
  }
}
