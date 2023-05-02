/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's Apache2); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.runtime.natives;

import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.json.JsonStreamWriter;
import org.junit.Test;

public class NtMessageBaseTests {
  @Test
  public void coverage() {
    NtMessageBase.NULL.__writeOut(new JsonStreamWriter());
    NtMessageBase.NULL.to_dynamic();
    NtMessageBase.NULL.ingest_dynamic(new NtDynamic("{}"));
    NtMessageBase.NULL.__hash(null);
    NtMessageBase.NULL.__ingest(new JsonStreamReader("{}"));
  }
}
