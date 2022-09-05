package org.adamalang.web.client;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.nio.file.Files;

public class FileReaderHttpRequestBodyTests {
  @Test
  public void flow() throws Exception {
    File temp = File.createTempFile("ADAMA_TEST", "SUIF");
    try {
      Files.writeString(temp.toPath(), "Hello World");
      FileReaderHttpRequestBody body = new FileReaderHttpRequestBody(temp);
      Assert.assertEquals(11, body.size);
      Assert.assertEquals("a591a6d40bf420404a011733cfb7b190d62c65bf0bcda32b57b277d9ad9f146e", body.sha256);
      Assert.assertEquals(11, body.size());
      Assert.assertEquals(5, body.read(new byte[5]));
      Assert.assertEquals(4, body.read(new byte[4]));
      Assert.assertEquals(2, body.read(new byte[5]));
      body.finished(true);
    } finally {
      temp.delete();
    }
  }
}
