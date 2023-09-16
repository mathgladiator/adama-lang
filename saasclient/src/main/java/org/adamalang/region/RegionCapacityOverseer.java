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

import org.adamalang.Wraps;
import org.adamalang.api.*;
import org.adamalang.common.Callback;
import org.adamalang.runtime.sys.capacity.CapacityInstance;
import org.adamalang.runtime.sys.capacity.CapacityOverseer;

import java.util.List;

public class RegionCapacityOverseer implements CapacityOverseer {
  private final SelfClient client;
  private final String identity;

  public RegionCapacityOverseer(SelfClient client, String identity) {
    this.client = client;
    this.identity = identity;
  }

  @Override
  public void listAllSpace(String space, Callback<List<CapacityInstance>> callback) {
    ClientRegionalCapacityListSpaceRequest request = new ClientRegionalCapacityListSpaceRequest();
    request.identity = identity;
    request.space = space;
    client.regionalCapacityListSpace(request, Wraps.wrapCapacityList(callback));
  }

  @Override
  public void listWithinRegion(String space, String region, Callback<List<CapacityInstance>> callback) {
    ClientRegionalCapacityListRegionRequest request = new ClientRegionalCapacityListRegionRequest();
    request.identity = identity;
    request.space = space;
    request.region = region;
    client.regionalCapacityListRegion(request, Wraps.wrapCapacityList(callback));
  }

  @Override
  public void listAllOnMachine(String region, String machine, Callback<List<CapacityInstance>> callback) {
    ClientRegionalCapacityListMachineRequest request = new ClientRegionalCapacityListMachineRequest();
    request.identity = identity;
    request.region = region;
    request.machine = machine;
    client.regionalCapacityListMachine(request, Wraps.wrapCapacityList(callback));
  }

  @Override
  public void add(String space, String region, String machine, Callback<Void> callback) {
    ClientRegionalCapacityAddRequest request = new ClientRegionalCapacityAddRequest();
    request.identity = identity;
    request.space = space;
    request.region = region;
    request.machine = machine;
    client.regionalCapacityAdd(request, Wraps.wrapVoid(callback));
  }

  @Override
  public void remove(String space, String region, String machine, Callback<Void> callback) {
    ClientRegionalCapacityRemoveRequest request = new ClientRegionalCapacityRemoveRequest();
    request.identity = identity;
    request.space = space;
    request.region = region;
    request.machine = machine;
    client.regionalCapacityRemove(request, Wraps.wrapVoid(callback));
  }

  @Override
  public void nuke(String space, Callback<Void> callback) {
    ClientRegionalCapacityNukeRequest request = new ClientRegionalCapacityNukeRequest();
    request.identity = identity;
    request.space = space;
    client.regionalCapacityNuke(request, Wraps.wrapVoid(callback));
  }

  @Override
  public void pickStableHostForSpace(String space, String region, Callback<String> callback) {
    ClientRegionalCapacityPickSpaceHostRequest request = new ClientRegionalCapacityPickSpaceHostRequest();
    request.identity = identity;
    request.space = space;
    request.region = region;
    client.regionalCapacityPickSpaceHost(request, Wraps.wrapCapacityHost(callback));
  }

  @Override
  public void pickNewHostForSpace(String space, String region, Callback<String> callback) {
    ClientRegionalCapacityPickSpaceHostNewRequest request = new ClientRegionalCapacityPickSpaceHostNewRequest();
    request.identity = identity;
    request.space = space;
    request.region = region;
    client.regionalCapacityPickSpaceHostNew(request, Wraps.wrapCapacityHost(callback));
  }
}
