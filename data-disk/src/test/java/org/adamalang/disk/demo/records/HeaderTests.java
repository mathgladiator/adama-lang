/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.disk.demo.records;

import org.junit.Assert;
import org.junit.Test;

import java.io.*;

public class HeaderTests {
  @Test
  public void flow() throws Exception {
    ByteArrayOutputStream memory = new ByteArrayOutputStream();
    DataOutputStream output = new DataOutputStream(new BufferedOutputStream(memory));
    Header.writeNewHeader(output);
    output.flush();
    output.close();
    DataInputStream input = new DataInputStream(new ByteArrayInputStream(memory.toByteArray()));
    Header header = Header.from(input);
    input.close();
    Assert.assertEquals(Header.CURRENT_VERSION, header.version);
    Assert.assertEquals(424242, header.version);
  }
}
