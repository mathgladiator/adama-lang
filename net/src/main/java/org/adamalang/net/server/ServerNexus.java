/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.net.server;

import org.adamalang.common.MachineIdentity;
import org.adamalang.common.net.NetBase;
import org.adamalang.runtime.deploy.DeploymentFactoryBase;
import org.adamalang.runtime.sys.CoreService;
import org.adamalang.runtime.sys.metering.DiskMeteringBatchMaker;
import org.adamalang.runtime.sys.metering.MeteringPubSub;

public class ServerNexus {
  public final NetBase base;
  public final MachineIdentity identity;
  public final CoreService service;
  public final ServerMetrics metrics;
  public final MeteringPubSub meteringPubSub;
  public final DiskMeteringBatchMaker meteringBatchMaker;
  public final DeploymentFactoryBase deploymentFactoryBase;
  public final LocalCapacityRequestor capacityRequestor;
  public final int port;
  public final int handlerThreads;

  public ServerNexus(NetBase base, MachineIdentity identity, CoreService service, ServerMetrics metrics, DeploymentFactoryBase deploymentFactoryBase, LocalCapacityRequestor capacityRequestor, MeteringPubSub meteringPubSub, DiskMeteringBatchMaker meteringBatchMaker, int port, int handlerThreads) {
    this.base = base;
    this.identity = identity;
    this.service = service;
    this.metrics = metrics;
    this.deploymentFactoryBase = deploymentFactoryBase;
    this.capacityRequestor = capacityRequestor;
    this.meteringPubSub = meteringPubSub;
    this.meteringBatchMaker = meteringBatchMaker;
    this.port = port;
    this.handlerThreads = handlerThreads;
  }
}
