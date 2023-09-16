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
package org.adamalang;

import org.adamalang.api.*;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.Stream;
import org.adamalang.runtime.data.DocumentLocation;
import org.adamalang.runtime.data.Key;
import org.adamalang.runtime.data.LocationType;
import org.adamalang.runtime.sys.capacity.CapacityInstance;

import java.util.ArrayList;
import java.util.List;

public class Wraps {
  public static Callback<ClientFinderResultResponse> wrapResult(Callback<DocumentLocation> callback) {
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

  public static Callback<ClientSimpleResponse> wrapVoid(Callback<Void> callback) {
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

  public static Stream<ClientKeysResponse> wrapListKey(Callback<List<Key>> callback) {
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

  public static Stream<ClientCapacityListResponse> wrapCapacityList(Callback<List<CapacityInstance>> callback) {
    return new Stream<ClientCapacityListResponse>() {
      ArrayList<CapacityInstance> list = new ArrayList<>();

      @Override
      public void next(ClientCapacityListResponse instance) {
        list.add(new CapacityInstance(instance.space, instance.region, instance.machine, instance.override));
      }

      @Override
      public void complete() {
        callback.success(list);
      }

      @Override
      public void failure(ErrorCodeException ex) {
        callback.failure(ex);
      }
    };
  }

  public static Callback<ClientCapacityHostResponse> wrapCapacityHost(Callback<String> callback) {
    return new Callback<ClientCapacityHostResponse>() {
      @Override
      public void success(ClientCapacityHostResponse value) {
        callback.success(value.machine);
      }

      @Override
      public void failure(ErrorCodeException ex) {
        callback.failure(ex);
      }
    };
  }
}
