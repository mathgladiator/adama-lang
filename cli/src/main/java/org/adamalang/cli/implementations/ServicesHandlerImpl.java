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
package org.adamalang.cli.implementations;

import org.adamalang.api.GlobalApiMetrics;
import org.adamalang.api.RegionApiMetrics;
import org.adamalang.caravan.CaravanMetrics;
import org.adamalang.caravan.data.DiskMetrics;
import org.adamalang.cli.Config;
import org.adamalang.cli.probe.ProbeStart;
import org.adamalang.cli.router.Arguments;
import org.adamalang.cli.router.ServicesHandler;
import org.adamalang.cli.runtime.Output;
import org.adamalang.system.distributed.Backend;
import org.adamalang.system.distributed.Frontend;
import org.adamalang.system.distributed.Overlord;
import org.adamalang.system.Solo;
import org.adamalang.common.net.NetMetrics;
import org.adamalang.extern.aws.AWSMetrics;
import org.adamalang.extern.prometheus.PrometheusDashboard;
import org.adamalang.frontend.FrontendMetrics;
import org.adamalang.mysql.DataBaseMetrics;
import org.adamalang.net.client.LocalRegionClientMetrics;
import org.adamalang.net.server.ServerMetrics;
import org.adamalang.region.AdamaDeploymentSyncMetrics;
import org.adamalang.region.MeteringBatchSubmitMetrics;
import org.adamalang.runtime.sys.capacity.CapacityMetrics;
import org.adamalang.runtime.deploy.DeploymentMetrics;
import org.adamalang.overlord.OverlordMetrics;
import org.adamalang.runtime.sys.CoreMetrics;
import org.adamalang.services.FirstPartyMetrics;
import org.adamalang.web.client.WebClientBaseMetrics;
import org.adamalang.web.service.WebMetrics;

import java.io.File;

public class ServicesHandlerImpl implements ServicesHandler {

  @Override
  public void auto(Arguments.ServicesAutoArgs args, Output.YesOrError output) throws Exception {
    Config config = args.config;
    String role = config.get_string("role", "none");
    switch (role) {
      case "backend":
        Backend.run(config).serverThread.join();
        return;
      case "overlord":
        Overlord.run(config);
        return;
      case "frontend":
        Frontend.run(config);
        return;
      case "solo":
        Solo.run(config);
        return;
      default:
        System.err.println("invalid role:" + role);
    }
    output.out();
  }

  @Override
  public void backend(Arguments.ServicesBackendArgs args, Output.YesOrError output) throws Exception {
    Backend.run(args.config).serverThread.join();
    output.out();
  }

  @Override
  public void dashboards(Arguments.ServicesDashboardsArgs args, Output.YesOrError output) throws Exception {
    PrometheusDashboard metricsFactory = new PrometheusDashboard();
    metricsFactory.page("web", "Web");
    new WebMetrics(metricsFactory);
    metricsFactory.page("api", "Public API");
    new FrontendMetrics(metricsFactory);
    new GlobalApiMetrics(metricsFactory);
    new RegionApiMetrics(metricsFactory);
    metricsFactory.page("billing", "Billing");
    new MeteringBatchSubmitMetrics(metricsFactory);
    metricsFactory.page("client", "Web to Adama");
    new LocalRegionClientMetrics(metricsFactory);
    metricsFactory.page("server", "Adama Service");
    new ServerMetrics(metricsFactory);
    metricsFactory.page("adama", "Adama Core");
    new CoreMetrics(metricsFactory);
    metricsFactory.page("deploy", "Deploy");
    new DeploymentMetrics(metricsFactory);
    new AdamaDeploymentSyncMetrics(metricsFactory);
    metricsFactory.page("capacity", "Capacity");
    new CapacityMetrics(metricsFactory);
    metricsFactory.page("database", "Database");
    new DataBaseMetrics(metricsFactory);
    metricsFactory.page("caravan", "Caravan");
    new CaravanMetrics(metricsFactory);
    metricsFactory.page("disk", "Disk");
    new DiskMetrics(metricsFactory);
    metricsFactory.page("overlord", "Overlord");
    new OverlordMetrics(metricsFactory);
    metricsFactory.page("net", "Network");
    new NetMetrics(metricsFactory);
    metricsFactory.page("aws", "AWS");
    new AWSMetrics(metricsFactory);
    metricsFactory.page("aws", "Web Client");
    new WebClientBaseMetrics(metricsFactory);
    metricsFactory.page("fp", "First Party");
    new FirstPartyMetrics(metricsFactory);
    metricsFactory.finish(new File("./prometheus/consoles"));
    output.out();
  }

  @Override
  public void frontend(Arguments.ServicesFrontendArgs args, Output.YesOrError output) throws Exception {
    Frontend.run(args.config);
    output.out();
  }

  @Override
  public void overlord(Arguments.ServicesOverlordArgs args, Output.YesOrError output) throws Exception {
    Overlord.run(args.config);
    output.out();
  }

  @Override
  public void probe(Arguments.ServicesProbeArgs args, Output.YesOrError output) throws Exception {
    new ProbeStart(args).run();
  }

  @Override
  public void solo(Arguments.ServicesSoloArgs args, Output.YesOrError output) throws Exception {
    Solo.run(args.config);
    output.out();
  }
}
