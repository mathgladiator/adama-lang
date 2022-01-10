/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.grpc.server;

import org.adamalang.common.MachineIdentity;
import org.adamalang.runtime.deploy.DeploymentFactoryBase;
import org.adamalang.runtime.sys.billing.BillingPubSub;
import org.adamalang.runtime.sys.CoreService;

import java.util.function.Consumer;

public class ServerNexus {
  public final MachineIdentity identity;
  public final CoreService service;
  public final BillingPubSub billingPubSub;
  public final DeploymentFactoryBase deploymentFactoryBase;
  public final Consumer<String> scanForDeployments;
  public final int port;
  public final int handlerThreads;

  public ServerNexus(
      MachineIdentity identity,
      CoreService service,
      DeploymentFactoryBase deploymentFactoryBase,
      Consumer<String> scanForDeployments,
      BillingPubSub billingPubSub,
      int port,
      int handlerThreads) {
    this.identity = identity;
    this.service = service;
    this.deploymentFactoryBase = deploymentFactoryBase;
    this.scanForDeployments = scanForDeployments;
    this.billingPubSub = billingPubSub;
    this.port = port;
    this.handlerThreads = handlerThreads;
  }
}
