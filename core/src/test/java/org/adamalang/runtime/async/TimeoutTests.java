/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
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
    Timeout x = Timeout.readFrom(new JsonStreamReader("{\"timestamp\":\"123\",\"timeout\":4.2}"));
    Assert.assertEquals(123, x.timestamp);
    Assert.assertEquals(4.2, x.timeoutSeconds, 0.1);
  }
}
