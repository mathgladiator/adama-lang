/*
* Adama Platform and Language
* Copyright (C) 2021 - 2024 by Adama Platform Engineering, LLC
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
package org.adamalang.system;

import org.adamalang.system.distributed.Backend;
import org.adamalang.system.distributed.Frontend;
import org.adamalang.net.client.LocalRegionClient;
import org.adamalang.runtime.sys.capacity.HeatMonitor;
import org.adamalang.system.contracts.JsonConfig;

public class Solo {
  public static void run(JsonConfig config) throws Exception {
    // run the core service
    Backend backend = Backend.run(config);

    LocalRegionClient client = backend.init.makeLocalClient(new HeatMonitor() {
      @Override
      public void heat(String machine, double cpu, double memory) {

      }
    });
    // spin up the frontend
    new Frontend(config, backend.init, client).run();
  }
}
