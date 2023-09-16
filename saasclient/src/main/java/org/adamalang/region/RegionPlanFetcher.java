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
package org.adamalang.region;

import com.fasterxml.jackson.databind.JsonNode;
import org.adamalang.api.ClientPlanWithKeysResponse;
import org.adamalang.api.ClientRegionalGetPlanRequest;
import org.adamalang.api.SelfClient;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.ExceptionLogger;
import org.adamalang.common.keys.PrivateKeyBundle;
import org.adamalang.runtime.contracts.PlanFetcher;
import org.adamalang.runtime.deploy.DeploymentBundle;
import org.adamalang.runtime.deploy.DeploymentPlan;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/** fetch a plan from the global region */
public class RegionPlanFetcher implements PlanFetcher {
  private final ExceptionLogger EXLOGGER = ExceptionLogger.FOR(DeploymentBundle.class);
  private final SelfClient client;
  private final String identity;

  public RegionPlanFetcher(SelfClient client, String identity) {
    this.client = client;
    this.identity = identity;
  }

  @Override
  public void find(String space, Callback<DeploymentBundle> callback) {
    ClientRegionalGetPlanRequest request = new ClientRegionalGetPlanRequest();
    request.identity = identity;
    request.space = space;
    client.regionalGetPlan(request, new Callback<ClientPlanWithKeysResponse>() {
      @Override
      public void success(ClientPlanWithKeysResponse value) {
        try {
          DeploymentPlan plan = new DeploymentPlan(value.toString(), EXLOGGER);
          TreeMap<Integer, PrivateKeyBundle> keys = new TreeMap<>();
          Iterator<Map.Entry<String, JsonNode>> it = value.privateKeyBundle.fields();
          while (it.hasNext()) {
            Map.Entry<String, JsonNode> key = it.next();
            keys.put(Integer.parseInt(key.getKey()), PrivateKeyBundle.fromNetwork(key.getValue().textValue()));
          }
          callback.success(new DeploymentBundle(plan, keys));
        } catch (ErrorCodeException ex) {
          failure(ex);
        }
      }

      @Override
      public void failure(ErrorCodeException ex) {
        callback.failure(ex);
      }
    });
  }
}
