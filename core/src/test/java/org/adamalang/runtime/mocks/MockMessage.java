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
package org.adamalang.runtime.mocks;

import org.adamalang.runtime.exceptions.AbortMessageException;
import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.natives.NtMessageBase;
import org.adamalang.runtime.natives.algo.HashBuilder;

public class MockMessage extends NtMessageBase {
  public int x;
  public int y;

  public MockMessage() {
    x = 42;
    y = 13;
  }

  public MockMessage(int x, int y) {
    this.x = x;
    this.y = y;
  }

  public MockMessage(JsonStreamReader reader) {
    __ingest(reader);
  }

  @Override
  public void __ingest(JsonStreamReader reader) {
    if (reader.startObject()) {
      while (reader.notEndOfObject()) {
        switch (reader.fieldName()) {
          case "x":
            x = reader.readInteger();
            break;
          case "y":
            y = reader.readInteger();
            break;
          default:
            reader.skipValue();
        }
      }
    }
  }

  @Override
  public int[] __getIndexValues() {
    return new int[0];
  }

  @Override
  public String[] __getIndexColumns() {
    return new String[0];
  }

  @Override
  public void __writeOut(final JsonStreamWriter writer) {
    writer.beginObject();
    writer.writeObjectFieldIntro("x");
    writer.writeInteger(x);
    writer.writeObjectFieldIntro("y");
    writer.writeInteger(y);
    writer.endObject();
  }

  @Override
  public void __hash(HashBuilder __hash) {
    __hash.hashInteger(x);
    __hash.hashInteger(y);
  }

  @Override
  public String toString() {
    JsonStreamWriter writer = new JsonStreamWriter();
    __writeOut(writer);
    return writer.toString();
  }

  @Override
  public long __memory() {
    return 128;
  }

  @Override
  public void __parsed() throws AbortMessageException {
  }
}
