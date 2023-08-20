/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.runtime.deploy;

import org.adamalang.common.Callback;

import java.util.ArrayList;
import java.util.function.Consumer;

/** the cyclic reference for the binding of deployments to deployment */
public class DelayedDeploy implements Deploy {
  private Deploy actual;

  private ArrayList<Consumer<Deploy>> delayed;

  public DelayedDeploy() {
    this.actual = null;
    this.delayed = new ArrayList<>();
  }

  @Override
  public synchronized void deploy(String space, Callback<Void> callback) {
    if (this.actual == null) {
      delayed.add((d) -> d.deploy(space, callback));
    } else {
      this.actual.deploy(space, callback);
    }
  }

  public synchronized void set(Deploy deploy) {
    this.actual = deploy;
    for (Consumer<Deploy> c : delayed) {
      c.accept(actual);
    }
    delayed = null;
  }
}
