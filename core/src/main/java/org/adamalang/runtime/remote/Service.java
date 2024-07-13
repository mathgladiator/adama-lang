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
package org.adamalang.runtime.remote;

import org.adamalang.ErrorCodes;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.runtime.contracts.Caller;
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.runtime.natives.NtResult;
import org.adamalang.runtime.natives.NtToDynamic;
import org.adamalang.runtime.remote.replication.Replicator;

import java.util.function.Function;

/** a service is responsible for executing a method with a message */
public interface Service {

  Replicator UNAVAILABLE = new Replicator() {
    @Override
    public String key() {
      return null;
    }

    @Override
    public void complete(Callback<Void> callback) {
      callback.failure(new ErrorCodeException(ErrorCodes.REPLICATION_NOT_AVAILABLE));
    }
  };

  Service FAILURE = new Service() {
    @Override
    public <T> NtResult<T> invoke(Caller caller, String method, RxCache cache, NtPrincipal agent, NtToDynamic request, Function<String, T> result) {
      return new NtResult<>(null, true, 500, "Service failed to resolve");
    }

    @Override
    public Replicator beginCreateReplica(String method, NtToDynamic body) {
      return UNAVAILABLE;
    }

    @Override
    public void deleteReplica(String method, String key, Callback<Void> callback) {
      UNAVAILABLE.complete(callback);
    }
  };

  Service NOT_READY = new Service() {
    @Override
    public <T> NtResult<T> invoke(Caller caller, String method, RxCache cache, NtPrincipal agent, NtToDynamic request, Function<String, T> result) {
      return new NtResult<>(null, true, ErrorCodes.DOCUMENT_NOT_READY, "Document is creating");
    }

    @Override
    public Replicator beginCreateReplica(String method, NtToDynamic body) {
      return UNAVAILABLE;
    }

    @Override
    public void deleteReplica(String method, String key, Callback<Void> callback) {
      UNAVAILABLE.complete(callback);
    }
  };

  /** invoke the given method */
  <T> NtResult<T> invoke(Caller caller, String method, RxCache cache, NtPrincipal agent, NtToDynamic request, Function<String, T> result);

  public Replicator beginCreateReplica(String method, NtToDynamic body);

  public void deleteReplica(String method, String key, Callback<Void> callback);
}
