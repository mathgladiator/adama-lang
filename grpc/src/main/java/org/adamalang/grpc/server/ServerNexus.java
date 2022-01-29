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
import org.adamalang.runtime.sys.CoreService;
import org.adamalang.runtime.sys.metering.DiskMeteringBatchMaker;
import org.adamalang.runtime.sys.metering.MeteringPubSub;

import java.util.function.Consumer;

public class ServerNexus {
  public final MachineIdentity identity;
  public final CoreService service;
  public final ServerMetrics metrics;
  public final MeteringPubSub meteringPubSub;
  public final DiskMeteringBatchMaker meteringBatchMaker;
  public final DeploymentFactoryBase deploymentFactoryBase;
  public final Consumer<String> scanForDeployments;
  public final int port;
  public final int handlerThreads;

  public ServerNexus(MachineIdentity identity, CoreService service, ServerMetrics metrics, DeploymentFactoryBase deploymentFactoryBase, Consumer<String> scanForDeployments, MeteringPubSub meteringPubSub, DiskMeteringBatchMaker meteringBatchMaker, int port, int handlerThreads) {
    this.identity = identity;
    this.service = service;
    this.metrics = metrics;
    this.deploymentFactoryBase = deploymentFactoryBase;
    this.scanForDeployments = scanForDeployments;
    this.meteringPubSub = meteringPubSub;
    this.meteringBatchMaker = meteringBatchMaker;
    this.port = port;
    this.handlerThreads = handlerThreads;
  }
}
