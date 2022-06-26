/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.runtime.mocks;

import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.natives.NtMessageBase;
import org.adamalang.runtime.natives.algo.HashBuilder;

public class MockMessage implements NtMessageBase {
  public int x;
  public int y;

  public MockMessage() {
    x = 42;
    y = 13;
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
}
