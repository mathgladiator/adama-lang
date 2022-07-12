package org.adamalang.runtime.remote;

import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.json.JsonStreamWriter;

/** the output of a service call */
public class RemoteResult {
  public final String result;
  public final String failure;

  public RemoteResult(String result, String failure) {
    this.result = result;
    this.failure = failure;
  }

  public RemoteResult(JsonStreamReader reader) {
    String _result = null;
    String _failure = null;
    if (reader.startObject()) {
      while (reader.notEndOfObject()) {
        switch (reader.fieldName()) {
          case "result":
            if (reader.testLackOfNull()) {
              _result = reader.readString();
            }
            break;
          case "failure":
            if (reader.testLackOfNull()) {
              _failure = reader.readString();
            }
            break;
        }
      }
    }
    this.result = _result;
    this.failure = _failure;
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
    writer.endObject();
  }
}
