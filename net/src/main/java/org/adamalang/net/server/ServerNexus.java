/*
* Adama Platform and Language
* Copyright (C) 2021 - 2023 by Adama Platform Initiative, LLC
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as published
* by the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Affero General Public License for more details.
* 
* You should have received a copy of the GNU Affero General Public License
* along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
package org.adamalang.net.server;

import org.adamalang.common.MachineIdentity;
import org.adamalang.common.net.NetBase;
import org.adamalang.runtime.data.BoundLocalFinderService;
import org.adamalang.runtime.data.FinderService;
import org.adamalang.runtime.deploy.Deploy;
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
  public final Deploy deployer;
  public final int port;
  public final int handlerThreads;
  public final BoundLocalFinderService finder;

  public ServerNexus(NetBase base, MachineIdentity identity, CoreService service, ServerMetrics metrics, DeploymentFactoryBase deploymentFactoryBase, BoundLocalFinderService finder, Deploy deployer, MeteringPubSub meteringPubSub, DiskMeteringBatchMaker meteringBatchMaker, int port, int handlerThreads) {
    this.base = base;
    this.identity = identity;
    this.service = service;
    this.metrics = metrics;
    this.deploymentFactoryBase = deploymentFactoryBase;
    this.deployer = deployer;
    this.meteringPubSub = meteringPubSub;
    this.meteringBatchMaker = meteringBatchMaker;
    this.port = port;
    this.handlerThreads = handlerThreads;
    this.finder = finder;
  }
}
