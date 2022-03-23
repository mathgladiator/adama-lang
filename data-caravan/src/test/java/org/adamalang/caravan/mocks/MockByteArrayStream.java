package org.adamalang.caravan.mocks;

import org.adamalang.caravan.contracts.ByteArrayStream;
import org.junit.Assert;

import java.nio.charset.StandardCharsets;

public class MockByteArrayStream implements ByteArrayStream {
  private StringBuilder sb;

  public MockByteArrayStream() {
    this.sb = new StringBuilder();
  }

  @Override
  public void next(int appendIndex, byte[] value) {
    sb.append("[").append(appendIndex).append("=").append(new String(value, StandardCharsets.UTF_8)).append("]");
  }

  @Override
  public void finished() {
    sb.append("FINISHED");
  }

  public void assertIs(String expected) {
    Assert.assertEquals(expected, sb.toString());
  }
}
