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
import org.adamalang.common.ErrorCodeException;
import org.adamalang.runtime.data.*;

import java.util.List;

public class RegionFinder implements FinderService {
  private final SelfClient client;
  private final String identity;
  private final String region;
  private final String machine;

  public RegionFinder(SelfClient client, String identity, String region, String machine) {
    this.client = client;
    this.identity = identity;
    this.region = region;
    this.machine = machine;
  }

  @Override
  public void find(Key key, Callback<DocumentLocation> callback) {
    ClientRegionalFinderFindRequest request = new ClientRegionalFinderFindRequest();
    request.identity = identity;
    request.space = key.space;
    request.key = key.key;
    client.regionalFinderFind(request, Wraps.wrapResult(callback));
  }

  @Override
  public void bind(Key key, Callback<Void> callback) {
    ClientRegionalFinderBindRequest request = new ClientRegionalFinderBindRequest();
    request.identity = identity;
    request.space = key.space;
    request.key = key.key;
    request.region = region;
    request.machine = machine;
    client.regionalFinderBind(request, Wraps.wrapVoid(callback));
  }

  @Override
  public void free(Key key, Callback<Void> callback) {
    ClientRegionalFinderFreeRequest request = new ClientRegionalFinderFreeRequest();
    request.identity = identity;
    request.space = key.space;
    request.key = key.key;
    request.region = region;
    request.machine = machine;
    client.regionalFinderFree(request, Wraps.wrapVoid(callback));
  }

  @Override
  public void backup(Key key, BackupResult result, Callback<Void> callback) {
    ClientRegionalFinderBackUpRequest request = new ClientRegionalFinderBackUpRequest();
    request.identity = identity;
    request.space = key.space;
    request.key = key.key;
    request.region = region;
    request.machine = machine;
    request.archive = result.archiveKey;
    request.seq = result.seq;
    request.assetBytes = result.assetBytes;
    request.deltaBytes = result.deltaBytes;
    client.regionalFinderBackUp(request, Wraps.wrapVoid(callback));
  }

  @Override
  public void markDelete(Key key, Callback<Void> callback) {
    ClientRegionalFinderDeleteMarkRequest request = new ClientRegionalFinderDeleteMarkRequest();
    request.identity = identity;
    request.space = key.space;
    request.key = key.key;
    request.region = region;
    request.machine = machine;
    client.regionalFinderDeleteMark(request, Wraps.wrapVoid(callback));
  }

  @Override
  public void commitDelete(Key key, Callback<Void> callback) {
    ClientRegionalFinderDeleteCommitRequest request = new ClientRegionalFinderDeleteCommitRequest();
    request.identity = identity;
    request.space = key.space;
    request.key = key.key;
    request.region = region;
    request.machine = machine;
    client.regionalFinderDeleteCommit(request, Wraps.wrapVoid(callback));
  }

  @Override
  public void list(Callback<List<Key>> callback) {
    ClientRegionalFinderListRequest request = new ClientRegionalFinderListRequest();
    request.identity = identity;
    request.region = region;
    request.machine = machine;
    client.regionalFinderList(request, Wraps.wrapListKey(callback));
  }

  @Override
  public void listDeleted(Callback<List<Key>> callback) {
    ClientRegionalFinderDeletionListRequest request = new ClientRegionalFinderDeletionListRequest();
    request.identity = identity;
    request.region = region;
    request.machine = machine;
    client.regionalFinderDeletionList(request, Wraps.wrapListKey(callback));
  }
}
