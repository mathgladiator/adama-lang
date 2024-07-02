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
package org.adamalang.api;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.auth.AuthenticatedUser;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.NamedRunnable;
import org.adamalang.frontend.Session;
import org.adamalang.web.io.*;

/** List capacity on a machine */
public class RegionalCapacityListMachineRequest {
  public final String identity;
  public final AuthenticatedUser who;
  public final String region;
  public final String machine;

  public RegionalCapacityListMachineRequest(final String identity, final AuthenticatedUser who, final String region, final String machine) {
    this.identity = identity;
    this.who = who;
    this.region = region;
    this.machine = machine;
  }

  public static void resolve(Session session, GlobalConnectionNexus nexus, JsonRequest request, Callback<RegionalCapacityListMachineRequest> callback) {
    try {
      final BulkLatch<RegionalCapacityListMachineRequest> _latch = new BulkLatch<>(nexus.executor, 1, callback);
      final String identity = request.getString("identity", true, 458759);
      final LatchRefCallback<AuthenticatedUser> who = new LatchRefCallback<>(_latch);
      final String region = request.getString("region", true, 9006);
      final String machine = request.getString("machine", true, 9005);
      _latch.with(() -> new RegionalCapacityListMachineRequest(identity, who.get(), region, machine));
      nexus.identityService.execute(session, identity, who);
    } catch (ErrorCodeException ece) {
      nexus.executor.execute(new NamedRunnable("regionalcapacitylistmachine-error") {
        @Override
        public void execute() throws Exception {
          callback.failure(ece);
        }
      });
    }
  }

  public void logInto(ObjectNode _node) {
    org.adamalang.transforms.PerSessionAuthenticator.logInto(who, _node);
    _node.put("region", region);
    _node.put("machine", machine);
  }
}
