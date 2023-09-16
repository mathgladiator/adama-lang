/*
* Adama Platform and Language
* Copyright (C) 2021 - 2023 by Adama Platform Initiative, LLC
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as published
* by the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Affero General Public License for more details.
* 
* You should have received a copy of the GNU Affero General Public License
* along with this program.  If not, see <https://www.gnu.org/licenses/>.
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
