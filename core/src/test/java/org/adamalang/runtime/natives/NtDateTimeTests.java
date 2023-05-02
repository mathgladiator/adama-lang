/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.runtime.natives;

import org.junit.Assert;
import org.junit.Test;

import java.time.ZonedDateTime;

public class NtDateTimeTests {
  @Test
  public void coverage() {
    NtDateTime dt = new NtDateTime(ZonedDateTime.parse("2023-04-24T17:57:19.802528800-05:00[America/Chicago]"));
    Assert.assertEquals(dt, dt);
    Assert.assertEquals(dt, new NtDateTime(ZonedDateTime.parse("2023-04-24T17:57:19.802528800-05:00[America/Chicago]")));
    Assert.assertNotEquals(dt, new NtDateTime(ZonedDateTime.parse("2021-04-24T17:57:19.802528800-05:00[America/Chicago]")));
    Assert.assertNotEquals(dt, "");
    Assert.assertNotEquals(dt, null);
    dt.hashCode();
    Assert.assertEquals("2023-04-24T17:57:19.802528800-05:00[America/Chicago]", dt.toString());
    Assert.assertEquals(64, dt.memory());
  }
}
