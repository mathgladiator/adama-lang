/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
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
