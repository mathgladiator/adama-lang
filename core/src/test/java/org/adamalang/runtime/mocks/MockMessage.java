/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * The 'LICENSE' file is in the root directory of the repository. Hint: it is MIT.
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

public class MockMessage implements NtMessageBase {
  public int x;
  public int y;

  public MockMessage() {
    x = 42;
    y = 13;
  }
  /* @Override public ObjectNode convertToObjectNode() { ObjectNode node =
   * Utility.createObjectNode(); node.put("x", x); node.put("y", y); return node;
   * } */

  @Override
  public void __writeOut(final JsonStreamWriter writer) {
    writer.beginObject();
    writer.writeObjectFieldIntro("x");
    writer.writeInteger(x);
    writer.writeObjectFieldIntro("y");
    writer.writeInteger(y);
    writer.endObject();
  }
}
