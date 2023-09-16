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
package org.adamalang.mysql.impl;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.adamalang.mysql.*;
import org.adamalang.mysql.model.Spaces;
import org.adamalang.runtime.sys.web.rxhtml.LiveSiteRxHtmlResult;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class GlobalRxHtmlFetcherTests {
  @Test
  public void flow() throws Exception {
    DataBaseConfig dataBaseConfig = DataBaseConfigTests.getLocalIntegrationConfig();
    try (DataBase dataBase = new DataBase(dataBaseConfig, new DataBaseMetrics(new NoOpMetricsFactory()))) {
      Installer installer = new Installer(dataBase);
      try {
        installer.install();
        GlobalRxHtmlFetcher fetcher = new GlobalRxHtmlFetcher(dataBase);
        CountDownLatch latch = new CountDownLatch(2);
        fetcher.fetch("space", new Callback<LiveSiteRxHtmlResult>() {
          @Override
          public void success(LiveSiteRxHtmlResult value) {

          }

          @Override
          public void failure(ErrorCodeException ex) {
            latch.countDown();
          }
        });
        int spaceId = Spaces.createSpace(dataBase, 1, "newspace");
        Spaces.setRxHtml(dataBase, spaceId, "<forest></forest>");
        fetcher.fetch("newspace", new Callback<LiveSiteRxHtmlResult>() {
          @Override
          public void success(LiveSiteRxHtmlResult value) {
            latch.countDown();
          }

          @Override
          public void failure(ErrorCodeException ex) {

          }
        });
        Assert.assertTrue(latch.await(10000, TimeUnit.MILLISECONDS));
      } finally {
        installer.uninstall();
      }
    }
  }
}
