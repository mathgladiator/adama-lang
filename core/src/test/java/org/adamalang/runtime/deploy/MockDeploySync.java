/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.runtime.deploy;

import org.junit.Assert;

import java.util.TreeSet;

public class MockDeploySync implements DeploySync {
  public final TreeSet<String> spaces;

  public MockDeploySync() {
    this.spaces = new TreeSet<>();
  }

  @Override
  public synchronized void watch(String space) {
    spaces.add(space);
  }

  @Override
  public synchronized void unwatch(String space) {
    spaces.remove(space);
  }

  public synchronized void assertContains(String space) {
    Assert.assertTrue(spaces.contains(space));
  }

  public synchronized void assertDoesNotContains(String space) {
    Assert.assertFalse(spaces.contains(space));
  }
}
