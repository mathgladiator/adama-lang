/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.runtime.natives;

import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.natives.algo.HashBuilder;

/** the base contract which messages must obey */
public abstract class NtMessageBase {
  public final static NtMessageBase NULL = new NtMessageBase() {
    @Override
    public void __ingest(JsonStreamReader reader) {
      reader.skipValue();
    }

    @Override
    public void __writeOut(JsonStreamWriter writer) {
      writer.beginObject();
      writer.endObject();
    }

    @Override
    public void __hash(HashBuilder __hash) {
    }
  };

  public abstract void __hash(HashBuilder __hash);

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
}
