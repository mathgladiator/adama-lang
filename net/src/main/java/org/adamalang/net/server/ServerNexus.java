package org.adamalang.net.server;

import org.adamalang.common.MachineIdentity;
import org.adamalang.common.net.NetBase;
import org.adamalang.runtime.deploy.DeploymentFactoryBase;
import org.adamalang.runtime.sys.CoreService;
import org.adamalang.runtime.sys.metering.DiskMeteringBatchMaker;
import org.adamalang.runtime.sys.metering.MeteringPubSub;

import java.util.function.Consumer;

public class ServerNexus {
  public final NetBase base;
  public final MachineIdentity identity;
  public final CoreService service;
  public final ServerMetrics metrics;
  public final MeteringPubSub meteringPubSub;
  public final DiskMeteringBatchMaker meteringBatchMaker;
  public final DeploymentFactoryBase deploymentFactoryBase;
  public final Consumer<String> scanForDeployments;
  public final int port;
  public final int handlerThreads;

  public ServerNexus(NetBase base, MachineIdentity identity, CoreService service, ServerMetrics metrics, DeploymentFactoryBase deploymentFactoryBase, Consumer<String> scanForDeployments, MeteringPubSub meteringPubSub, DiskMeteringBatchMaker meteringBatchMaker, int port, int handlerThreads) {
    this.base = base;
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
