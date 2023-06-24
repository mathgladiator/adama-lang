/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
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
