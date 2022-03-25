package org.adamalang.caravan.mocks;

import org.adamalang.caravan.contracts.ByteArrayStream;
import org.junit.Assert;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class MockByteArrayStream implements ByteArrayStream {
  private StringBuilder sb;
  private final CountDownLatch latch;

  public MockByteArrayStream() {
    this.sb = new StringBuilder();
    this.latch = new CountDownLatch(1);
  }

  @Override
  public void next(int appendIndex, byte[] value) {
    sb.append("[").append(appendIndex).append("=").append(new String(value, StandardCharsets.UTF_8)).append("]");
  }

  @Override
  public void finished() {
    sb.append("FINISHED");
    latch.countDown();
  }

  public void assertIs(String expected) throws Exception {
    Assert.assertTrue(latch.await(1000, TimeUnit.MILLISECONDS));
    Assert.assertEquals(expected, sb.toString());
  }
}
