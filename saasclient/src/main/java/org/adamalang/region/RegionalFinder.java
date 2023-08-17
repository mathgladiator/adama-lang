/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.region;

import org.adamalang.api.*;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.Stream;
import org.adamalang.runtime.data.*;

import java.util.ArrayList;
import java.util.List;

public class RegionalFinder implements FinderService {
  private final SelfClient client;
  private final String identity;
  private final String region;

  public RegionalFinder(SelfClient client, String identity, String region) {
    this.client = client;
    this.identity = identity;
    this.region = region;
  }

  @Override
  public void find(Key key, Callback<DocumentLocation> callback) {
    ClientRegionalFinderFindRequest request = new ClientRegionalFinderFindRequest();
    request.identity = identity;
    request.space = key.space;
    request.key = key.key;
    client.regionalFinderFind(request, wrapResult(callback));
  }

  @Override
  public void findbind(Key key, String machine, Callback<DocumentLocation> callback) {
    ClientRegionalFinderFindbindRequest request = new ClientRegionalFinderFindbindRequest();
    request.identity = identity;
    request.space = key.space;
    request.key = key.key;
    request.region = region;
    request.machine = machine;
    client.regionalFinderFindbind(request, wrapResult(callback));
  }

  @Override
  public void bind(Key key, String machine, Callback<Void> callback) {
    ClientRegionalFinderFindbindRequest request = new ClientRegionalFinderFindbindRequest();
    request.identity = identity;
    request.space = key.space;
    request.key = key.key;
    request.region = region;
    request.machine = machine;
    client.regionalFinderFindbind(request, new Callback<>() {
      @Override
      public void success(ClientFinderResultResponse value) {
        if (region.equals(value.region) && machine.equals(value.region)) {
          callback.success(null);
        } else {
          // failed to bind
          callback.failure(new ErrorCodeException(-1));
        }
      }

      @Override
      public void failure(ErrorCodeException ex) {
        callback.failure(ex);
      }
    });
  }

  @Override
  public void free(Key key, String machine, Callback<Void> callback) {
    ClientRegionalFinderFreeRequest request = new ClientRegionalFinderFreeRequest();
    request.identity = identity;
    request.space = key.space;
    request.key = key.key;
    request.region = region;
    request.machine = machine;
    client.regionalFinderFree(request, wrapVoid(callback));
  }

  @Override
  public void backup(Key key, BackupResult result, String machine, Callback<Void> callback) {
    ClientRegionalFinderBackUpRequest request = new ClientRegionalFinderBackUpRequest();
    request.identity = identity;
    request.space = key.space;
    request.key = key.key;
    request.region = region;
    request.machine = machine;
    request.archive = result.archiveKey;
    client.regionalFinderBackUp(request, wrapVoid(callback));
  }

  @Override
  public void markDelete(Key key, String machine, Callback<Void> callback) {
    ClientRegionalFinderDeleteMarkRequest request = new ClientRegionalFinderDeleteMarkRequest();
    request.identity = identity;
    request.space = key.space;
    request.key = key.key;
    request.region = region;
    request.machine = machine;
    client.regionalFinderDeleteMark(request, wrapVoid(callback));
  }

  @Override
  public void commitDelete(Key key, String machine, Callback<Void> callback) {
    ClientRegionalFinderDeleteCommitRequest request = new ClientRegionalFinderDeleteCommitRequest();
    request.identity = identity;
    request.space = key.space;
    request.key = key.key;
    request.region = region;
    request.machine = machine;
    client.regionalFinderDeleteCommit(request, wrapVoid(callback));
  }

  @Override
  public void list(String machine, Callback<List<Key>> callback) {
    ClientRegionalFinderListRequest request = new ClientRegionalFinderListRequest();
    request.identity = identity;
    request.region = region;
    request.machine = machine;
    client.regionalFinderList(request, wrapListKey(callback));
  }

  @Override
  public void listDeleted(String machine, Callback<List<Key>> callback) {
    ClientRegionalFinderDeletionListRequest request = new ClientRegionalFinderDeletionListRequest();
    request.identity = identity;
    request.region = region;
    request.machine = machine;
    client.regionalFinderDeletionList(request, wrapListKey(callback));
  }

  private Callback<ClientFinderResultResponse> wrapResult(Callback<DocumentLocation> callback) {
    return new Callback<ClientFinderResultResponse>() {
      @Override
      public void success(ClientFinderResultResponse value) {
        callback.success(new DocumentLocation(value.id, LocationType.fromType(value.locationType), value.region, value.machine, value.archive, value.deleted));
      }

      @Override
      public void failure(ErrorCodeException ex) {
        callback.failure(ex);
      }
    };
  }

  private Callback<ClientSimpleResponse> wrapVoid(Callback<Void> callback) {
    return new Callback<ClientSimpleResponse>() {
      @Override
      public void success(ClientSimpleResponse value) {
        callback.success(null);
      }

      @Override
      public void failure(ErrorCodeException ex) {
        callback.failure(ex);
      }
    };
  }

  private Stream<ClientKeysResponse> wrapListKey(Callback<List<Key>> callback) {
    return new Stream<ClientKeysResponse>() {
      private ArrayList<Key> result = new ArrayList<>();
      @Override
      public void next(ClientKeysResponse value) {
        result.add(new Key(value.space, value.key));
      }

      @Override
      public void complete() {
        callback.success(result);
      }

      @Override
      public void failure(ErrorCodeException ex) {
        callback.failure(ex);
      }
    };
  }
}
