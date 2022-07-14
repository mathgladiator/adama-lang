/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.runtime.remote;

import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.natives.NtResult;

/** the output of a service call */
public class RemoteResult {
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
    if (result != null) {
      writer.writeObjectFieldIntro("result");
      writer.injectJson(result);
    }
    if (failure != null) {
      writer.writeObjectFieldIntro("failure");
      writer.writeString(failure);
    }
    if (failureCode != null) {
      writer.writeObjectFieldIntro("failure_code");
      writer.writeInteger(failureCode);
    }
    writer.endObject();
  }

  public static RemoteResult NULL = new RemoteResult(null, null, null);
}
