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
import org.adamalang.runtime.sys.CoreService;
import org.adamalang.runtime.sys.TriggerDeployment;

import java.util.ArrayList;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/** the cyclic reference for the binding of deployments to deployment */
public class DelayedDeploy implements Deploy {
  private Deploy actual;
  private CoreService service;
  private ArrayList<BiConsumer<Deploy, CoreService>> delayed;

  public DelayedDeploy() {
    this.actual = null;
    this.delayed = new ArrayList<>();
  }

  @Override
  public synchronized void deploy(String space, Callback<Void> callback) {
    if (this.actual == null) {
      delayed.add((d, s) -> d.deploy(space, new TriggerDeployment(service, callback)));
    } else {
      this.actual.deploy(space, new TriggerDeployment(service, callback));
    }
  }

  public synchronized void set(Deploy deploy, CoreService service) {
    this.actual = deploy;
    this.service = service;
    for (BiConsumer<Deploy, CoreService> c : delayed) {
      c.accept(actual, service);
    }
    delayed = null;
  }
}
