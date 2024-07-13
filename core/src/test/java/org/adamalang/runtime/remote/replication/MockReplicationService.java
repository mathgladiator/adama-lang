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
package org.adamalang.runtime.remote.replication;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.runtime.contracts.Caller;
import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.natives.NtDynamic;
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.runtime.natives.NtResult;
import org.adamalang.runtime.natives.NtToDynamic;
import org.adamalang.runtime.remote.RxCache;
import org.adamalang.runtime.remote.Service;

import java.util.ArrayList;
import java.util.Map;
import java.util.function.Function;

public class MockReplicationService implements Service {
  private ArrayList<String> log;

  public MockReplicationService() {
    this.log = new ArrayList<>();
  }

  @Override
  public <T> NtResult<T> invoke(Caller caller, String method, RxCache cache, NtPrincipal agent, NtToDynamic request, Function<String, T> result) {
    return Service.FAILURE.invoke(caller, method, cache, agent, request, result);
  }

  public static NtToDynamic SIMPLE_KEY_OBJECT(String key) {
    JsonStreamWriter writer = new JsonStreamWriter();
    writer.beginObject();
    writer.writeObjectFieldIntro("key");
    writer.writeString(key);
    writer.endObject();
    return new NtDynamic(writer.toString());
  }

  private synchronized void writeLog(String ln) {
    log.add(ln);
  }

  @Override
  public Replicator beginCreateReplica(String method, NtToDynamic body) {
    if ("nope".equals(method)) {
      return null;
    }
    final String key;
    Object map = body.to_dynamic().cached();
    if (map instanceof Map<?,?>) {
      key = ((Map<String, Object>) map).get("key").toString();
    } else {
      key = null;
    }

    writeLog("BEGIN[" + method + "]:" + key);

    return new Replicator() {
      @Override
      public String key() {
        writeLog("ASK[" + method + "]:" + key);
        return key;
      }

      @Override
      public void complete(Callback<Void> callback) {
        if ("failure".equals(method)) {
          writeLog("FAILED[" + method + "]:" + key);
          callback.failure(new ErrorCodeException(-42));
        } else {
          writeLog("SUCCESS[" + method + "]:" + key);
          callback.success(null);
        }
      }
    };
  }

  @Override
  public void deleteReplica(String method, String key, Callback<Void> callback) {
    if ("failure".equals(method)) {
      writeLog("NODELETE[" + method + "]:" + key);
      callback.failure(new ErrorCodeException(-42));
    } else {
      writeLog("DELETED[" + method + "]:" + key);
      callback.success(null);
    }
  }
}
