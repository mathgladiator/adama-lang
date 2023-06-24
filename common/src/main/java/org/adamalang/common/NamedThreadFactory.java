/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.common;

import java.util.concurrent.ThreadFactory;

/** name a thread on creation; this enables executors to be named in a meaningful way */
public class NamedThreadFactory implements ThreadFactory {

  private final String name;

  public NamedThreadFactory(String name) {
    this.name = name;
  }

  @Override
  public Thread newThread(Runnable r) {
    Thread thread = new Thread(r, name);
    thread.setDaemon(true);
    return thread;
  }
}
