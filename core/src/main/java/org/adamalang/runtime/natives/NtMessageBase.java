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
package org.adamalang.runtime.natives;

import org.adamalang.runtime.contracts.MultiIndexable;
import org.adamalang.runtime.exceptions.AbortMessageException;
import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.natives.algo.HashBuilder;

/** the base contract which messages must obey */
public abstract class NtMessageBase implements NtToDynamic, MultiIndexable {
  public final static NtMessageBase NULL = new NtMessageBase() {
    @Override
    public void __hash(HashBuilder __hash) {
    }

    @Override
    public void __writeOut(JsonStreamWriter writer) {
      writer.beginObject();
      writer.endObject();
    }

    @Override
    public void __ingest(JsonStreamReader reader) {
      reader.skipValue();
    }

    @Override
    public int[] __getIndexValues() {
      return new int[] {};
    }

    @Override
    public String[] __getIndexColumns() {
      return new String[] {};
    }

    @Override
    public long __memory() {
      return 64;
    }

    @Override
    public void __parsed() throws AbortMessageException {
    }
  };

  public abstract void __hash(HashBuilder __hash);

  @Override
  public NtDynamic to_dynamic() {
    JsonStreamWriter writer = new JsonStreamWriter();
    __writeOut(writer);
    return new NtDynamic(writer.toString());
  }

  public abstract void __writeOut(JsonStreamWriter writer);

  public void ingest_dynamic(NtDynamic value) {
    __ingest(new JsonStreamReader(value.json));
  }

  public abstract void __ingest(JsonStreamReader reader);

  public abstract long __memory();

  public abstract void __parsed() throws AbortMessageException;
}
