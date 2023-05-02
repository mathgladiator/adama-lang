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

import java.util.Objects;

/** the output of a service call */
public class RemoteResult {
  public static RemoteResult NULL = new RemoteResult(null, null, null);
  public final String result;
  public final String failure;
  public final Integer failureCode;

  public RemoteResult(String result, String failure, Integer failureCode) {
    this.result = result;
    this.failure = failure;
    this.failureCode = failureCode;
  }

  public RemoteResult(JsonStreamReader reader) {
    String _result = null;
    String _failure = null;
    Integer _failureCode = null;
    if (reader.startObject()) {
      while (reader.notEndOfObject()) {
        switch (reader.fieldName()) {
          case "result":
            if (reader.testLackOfNull()) {
              _result = reader.skipValueIntoJson();
            }
            break;
          case "failure":
            if (reader.testLackOfNull()) {
              _failure = reader.readString();
            }
            break;
          case "failure_code":
            if (reader.testLackOfNull()) {
              _failureCode = reader.readInteger();
            }
            break;
        }
      }
    }
    this.result = _result;
    this.failure = _failure;
    this.failureCode = _failureCode;
  }

  public void write(JsonStreamWriter writer) {
    writer.beginObject();
    writer.writeObjectFieldIntro("result");
    if (result != null) {
      writer.injectJson(result);
    } else {
      writer.writeNull();
    }
    writer.writeObjectFieldIntro("failure");
    if (failure != null) {
      writer.writeString(failure);
    } else {
      writer.writeNull();
    }
    writer.writeObjectFieldIntro("failure_code");
    if (failureCode != null) {
      writer.writeInteger(failureCode);
    } else {
      writer.writeNull();
    }
    writer.endObject();
  }

  @Override
  public int hashCode() {
    return Objects.hash(result, failure, failureCode);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    RemoteResult that = (RemoteResult) o;
    return Objects.equals(result, that.result) && Objects.equals(failure, that.failure) && Objects.equals(failureCode, that.failureCode);
  }
}
