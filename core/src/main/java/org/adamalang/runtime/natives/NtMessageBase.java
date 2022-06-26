/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.runtime.natives;

import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.natives.algo.HashBuilder;

/** the base contract which messages must obey */
public interface NtMessageBase /* extends CanConvertToObject */ {
  NtMessageBase NULL = new NtMessageBase() {
    @Override
    public void __writeOut(JsonStreamWriter writer) {
      writer.beginObject();
      writer.endObject();
    }

    @Override
    public void __hash(HashBuilder __hash) {
    }
  };

  void __writeOut(JsonStreamWriter writer);

  void __hash(HashBuilder __hash);
}
