/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
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
