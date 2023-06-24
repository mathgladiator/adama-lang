/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.runtime.async;

import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.json.JsonStreamWriter;
import org.junit.Assert;
import org.junit.Test;

public class TimeoutTests {
  @Test
  public void write() {
    Timeout x = new Timeout(123, 4.2);
    JsonStreamWriter writer = new JsonStreamWriter();
    x.write(writer);
    Assert.assertEquals("{\"timestamp\":\"123\",\"timeout\":4.2}", writer.toString());
  }

  @Test
  public void readFrom() {
    Timeout x = Timeout.readFrom(new JsonStreamReader("{\"timestamp\":\"123\",\"timeout\":4.2,\"junk\":true}"));
    Assert.assertEquals(123, x.timestamp);
    Assert.assertEquals(4.2, x.timeoutSeconds, 0.1);
  }

  @Test
  public void junk() {
    Timeout x = Timeout.readFrom(new JsonStreamReader("\"timeout\""));
    Assert.assertNull(x);
  }
}
