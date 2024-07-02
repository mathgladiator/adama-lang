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

import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.natives.NtResult;

import java.util.Objects;
import java.util.function.Function;

/** the binding site where inputs are connected to outputs within a differential state machine */
public class RemoteSite {
  public final int id;
  private RemoteInvocation invocation;
  private RemoteResult backup;
  private RemoteResult value;
  private Object cached;

  public RemoteSite(int id, RemoteInvocation invocation) {
    this.id = id;
    this.invocation = invocation;
    this.backup = RemoteResult.NULL;
    this.value = backup;
    this.cached = null;
  }

  public RemoteSite(int id, JsonStreamReader reader) {
    this.id = id;
    patch(reader);
    this.backup = this.value;
  }

  public void patch(JsonStreamReader reader) {
    if (reader.startObject()) {
      while (reader.notEndOfObject()) {
        switch (reader.fieldName()) {
          case "invoke":
            invocation = new RemoteInvocation(reader);
            break;
          case "result":
            value = new RemoteResult(reader);
            break;
          default:
            reader.skipValue();
        }
      }
    } else {
      reader.skipValue();
    }
  }

  public RemoteInvocation invocation() {
    return this.invocation;
  }

  public <T> NtResult<T> of(Function<String, T> parser) {
    if (cached == null && this.value.result != null) {
      cached = parser.apply(this.value.result);
    }
    if (cached != null) {
      return new NtResult<>((T) cached, false, 0, "OK");
    } else {
      if (this.value.failure != null) {
        return new NtResult<>(null, true, this.value.failureCode, this.value.failure);
      } else {
        return new NtResult<>(null, false, 0, "In progress");
      }
    }
  }

  public void deliver(RemoteResult result) {
    this.value = result;
  }

  public void dump(JsonStreamWriter writer) {
    writer.beginObject();
    writer.writeObjectFieldIntro("invoke");
    invocation.write(writer);
    writer.writeObjectFieldIntro("result");
    value.write(writer);
    writer.endObject();
  }

  public void writeValue(JsonStreamWriter writer) {
    writer.beginObject();
    writer.writeObjectFieldIntro("result");
    value.write(writer);
    writer.endObject();
  }

  public void writeBackup(JsonStreamWriter writer) {
    writer.beginObject();
    writer.writeObjectFieldIntro("result");
    backup.write(writer);
    writer.endObject();
  }

  public boolean shouldCommit() {
    return value != backup;
  }

  public void commit() {
    backup = value;
  }

  public void revert() {
    value = backup;
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, invocation, backup, value, cached);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    RemoteSite that = (RemoteSite) o;
    return id == that.id && Objects.equals(invocation, that.invocation) && Objects.equals(backup, that.backup) && Objects.equals(value, that.value) && Objects.equals(cached, that.cached);
  }
}
