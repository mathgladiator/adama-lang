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
package org.adamalang.cli.services.distributed;

import org.adamalang.cli.Config;
import org.adamalang.cli.services.CommonServiceInit;
import org.adamalang.cli.services.Role;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.NamedRunnable;
import org.adamalang.multiregion.MultiRegionClient;
import org.adamalang.net.client.LocalRegionClient;
import org.adamalang.overlord.heat.HeatTable;
import org.adamalang.overlord.html.ConcurrentCachedHttpHandler;
import org.adamalang.runtime.sys.domains.Domain;
import org.adamalang.runtime.sys.domains.DomainFinder;
import org.adamalang.web.contracts.HttpHandler;
import org.adamalang.web.contracts.ServiceBase;
import org.adamalang.web.service.ServiceRunnable;
import org.adamalang.web.service.WebMetrics;

import java.io.File;

public class Overlord {
  public static void run(Config config) throws Exception {
    CommonServiceInit init = new CommonServiceInit(config, Role.Overlord);
    File targetsPath = new File(config.get_string("targets-filename", "targets.json"));
    init.engine.createLocalApplicationHeartbeat("overlord", init.webConfig.port, init.monitoringPort, (hb) -> {
      init.system.schedule(new NamedRunnable("overlord-hb") {
        @Override
        public void execute() throws Exception {
          hb.run();
          if (init.alive.get()) {
            init.system.schedule(this, 1000);
          }
        }
      }, 100);
    });
    ConcurrentCachedHttpHandler overlordHandler = new ConcurrentCachedHttpHandler();
    HeatTable heatTable = new HeatTable(overlordHandler);
    LocalRegionClient client = init.makeLocalClient(heatTable::onSample);
    boolean isGlobalOverlord = config.get_string("overlord-region-global", null).equals(init.region);
    if (isGlobalOverlord) {
      System.err.println("[Global Overlord Established]");
    }
    MultiRegionClient adama = init.makeGlobalClient(client);
    HttpHandler handler = org.adamalang.overlord.Overlord.execute(overlordHandler, isGlobalOverlord, client, adama, init.engine, init.metricsFactory, targetsPath, init.database, init.s3, init.s3, init.alive);
    ServiceBase serviceBase = ServiceBase.JUST_HTTP(handler);
    final var runnable = new ServiceRunnable(init.webConfig, new WebMetrics(init.metricsFactory), serviceBase, (domain, callback) -> callback.success(null), new DomainFinder() {
      @Override
      public void find(String domain, Callback<Domain> callback) {
        callback.failure(new ErrorCodeException(0));
      }
    }, () -> {
    });
    Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
      @Override
      public void run() {
        System.err.println("shutting down overlord");
        runnable.shutdown();
      }
    }));
    System.err.println("running overlord web");
    runnable.run();
    System.err.println("overlord finished");
  }
}
