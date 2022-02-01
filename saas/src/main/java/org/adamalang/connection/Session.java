/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.connection;

public class Session {
  public final long created;
  private long lastActivity;

  public Session() {
    this.created = System.currentTimeMillis();
  }

  public synchronized void activity() {
    lastActivity = System.currentTimeMillis();
  }

  public synchronized boolean keepalive() {
    long now = System.currentTimeMillis();
    long timeSinceCreation = now - created;
    if (timeSinceCreation <= 5 * 60000) {
      return true;
    }
    long timeSinceLastActivity = now - lastActivity;
    if (timeSinceLastActivity <= 2 * 60 * 60000) {
      return true;
    }
    return false;
  }
}
