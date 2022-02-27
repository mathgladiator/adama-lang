package org.adamalang.disk.records;

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
