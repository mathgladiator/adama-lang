/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's Apache2); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
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
  public void next(int appendIndex, byte[] value, int seq, long assetBytes) {
    sb.append("[").append(appendIndex).append("=").append(new String(value, StandardCharsets.UTF_8)).append("/").append(seq).append(assetBytes > 0 ? ":" + assetBytes : "").append("]");
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

  public void assertLength(int size) throws Exception {
    Assert.assertTrue(latch.await(1000, TimeUnit.MILLISECONDS));
    Assert.assertEquals(size, sb.toString().length());
  }
}
