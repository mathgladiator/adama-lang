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
package org.adamalang.net.client;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.adamalang.net.TestBed;
import org.adamalang.runtime.data.DocumentLocation;
import org.adamalang.runtime.data.Key;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class LocalFinderTests {

  @Test
  public void flow() throws Exception {
    try (TestBed bed =
             new TestBed(
                 13542,
                 "@static { create { return true; } } @connected { return true; } public int x; @construct { x = 123; transition #p in 0.25; } #p { x++; } ")) {
      bed.startServer();
      ClientConfig clientConfig = new TestClientConfig();
      bed.finderService.bindArchive(new Key("archive", "key"), "archive");
      bed.finderService.bindLocal(new Key("space", "key"));
      LocalRegionClient client = new LocalRegionClient(bed.base, clientConfig, new LocalRegionClientMetrics(new NoOpMetricsFactory()), null);
      try {
        LocalRegionClientTests.waitForRouting(bed, client);

        CountDownLatch latch = new CountDownLatch(3);
        client.finder.find(new Key("space", "key"), new Callback<DocumentLocation>() {
          @Override
          public void success(DocumentLocation value) {
            System.err.println("WUT:" + value.region + "/" + value.machine);
            Assert.assertEquals("test-region", value.region);
            Assert.assertEquals("the-machine", value.machine);
            latch.countDown();
          }

          @Override
          public void failure(ErrorCodeException ex) {
            ex.printStackTrace();

          }
        });

        client.finder.find(new Key("archive", "key"), new Callback<DocumentLocation>() {
          @Override
          public void success(DocumentLocation value) {
            System.err.println("WUT:" + value.region + "/" + value.machine);
            Assert.assertEquals("the-region", value.region);
            Assert.assertEquals("the-machine", value.machine);
            latch.countDown();
          }

          @Override
          public void failure(ErrorCodeException ex) {
            ex.printStackTrace();
          }
        });

        client.finder.find(new Key("nope", "key"), new Callback<DocumentLocation>() {
          @Override
          public void success(DocumentLocation value) {
            System.err.println("WUT:" + value.region + "/" + value.machine);
            Assert.assertEquals("the-region", value.region);
            Assert.assertEquals("the-machine", value.machine);
            latch.countDown();
          }

          @Override
          public void failure(ErrorCodeException ex) {
            ex.printStackTrace();
          }
        });
        Assert.assertTrue(latch.await(10000, TimeUnit.MILLISECONDS));

      } finally{
        client.shutdown();
      }
    }
  }
}
