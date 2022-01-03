package org.adamalang.grpc.server;

import org.adamalang.common.MachineIdentity;
import org.adamalang.runtime.deploy.DeploymentFactoryBase;
import org.adamalang.runtime.sys.BillingPubSub;
import org.adamalang.runtime.sys.CoreService;

public class ServerNexus {
  public final MachineIdentity identity;
  public final CoreService service;
  public final BillingPubSub billingPubSub;
  public final DeploymentFactoryBase deploymentFactoryBase;
  public final Runnable scanForDeployments;
  public final int port;

  public ServerNexus(
      MachineIdentity identity,
      CoreService service,
      DeploymentFactoryBase deploymentFactoryBase,
      Runnable scanForDeployments,
      BillingPubSub billingPubSub,
      int port) {
    this.identity = identity;
    this.service = service;
    this.deploymentFactoryBase = deploymentFactoryBase;
    this.scanForDeployments = scanForDeployments;
    this.billingPubSub = billingPubSub;
    this.port = port;
  }
}
