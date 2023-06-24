/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.runtime.delta;

import org.adamalang.runtime.delta.secure.TestKey;
import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.json.PrivateLazyDeltaWriter;
import org.adamalang.runtime.natives.NtDateTime;
import org.adamalang.runtime.natives.NtPrincipal;
import org.junit.Assert;
import org.junit.Test;

import java.time.ZonedDateTime;

public class DDateTimeTests {
  @Test
  public void flow() {
    final var db = new DDateTime();
    final var stream = new JsonStreamWriter();
    final var writer = PrivateLazyDeltaWriter.bind(NtPrincipal.NO_ONE, stream, null, TestKey.ENCODER);
    db.show(new NtDateTime(ZonedDateTime.parse("2023-04-24T17:57:19.802528800-05:00[America/Chicago]")), writer);
    db.show(new NtDateTime(ZonedDateTime.parse("2021-04-24T17:57:19.802528800-05:00[America/Chicago]")), writer);
    db.hide(writer);
    db.hide(writer);
    db.show(new NtDateTime(ZonedDateTime.parse("2023-04-24T17:57:19.802528800-05:00[America/Chicago]")), writer);
    db.show(new NtDateTime(ZonedDateTime.parse("2021-04-24T17:57:19.802528800-05:00[America/Chicago]")), writer);
    Assert.assertEquals("\"2023-04-24T17:57:19.802528800-05:00[America/Chicago]\"\"2021-04-24T17:57:19.802528800-05:00[America/Chicago]\"null\"2023-04-24T17:57:19.802528800-05:00[America/Chicago]\"\"2021-04-24T17:57:19.802528800-05:00[America/Chicago]\"", stream.toString());
    Assert.assertEquals(96, db.__memory());
    db.clear();
  }
}
