/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.overlord.grpc;

import org.adamalang.common.MachineIdentity;
import org.adamalang.common.SimpleExecutor;
import org.adamalang.overlord.html.ConcurrentCachedHttpHandler;

public class SingleTargetClient {
  private final MachineIdentity identity;
  private final SimpleExecutor executor;
  private final ConcurrentCachedHttpHandler handler;

  public SingleTargetClient(MachineIdentity identity, ConcurrentCachedHttpHandler handler) {
    this.identity = identity;
    this.executor = SimpleExecutor.create("overlord-http-client");
    this.handler = handler;
  }

  public void setTarget(String target) {
  }
}
