/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.caravan.events;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.runtime.data.BackupResult;
import org.adamalang.runtime.data.FinderService;
import org.adamalang.runtime.data.Key;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class FinderServiceToKeyToIdServiceTests {
  @Test
  public void flow() {
    AtomicInteger count = new AtomicInteger(0);
    FinderService real = new FinderService() {
      @Override
      public void find(Key key, Callback<Result> callback) {
        if (key.key.equals("fail")) {
          callback.failure(new ErrorCodeException(-1));
          return;
        }
        count.incrementAndGet();
        callback.success(new Result(Long.parseLong(key.key), Location.Machine, "", "", ""));

      }

      @Override
      public void bind(Key key, String region, String machine, Callback<Void> callback) {

      }

      @Override
      public void free(Key key, String machineOn, Callback<Void> callback) {

      }

      @Override
      public void backup(Key key, BackupResult result, String machineOn, Callback<Void> callback) {

      }

      @Override
      public void delete(Key key, String machineOn, Callback<Void> callback) {

      }
    };
    FinderServiceToKeyToIdService service = new FinderServiceToKeyToIdService(real);
    AtomicLong sum = new AtomicLong(0);
    for (int k = 0; k < 10; k++) {
      service.translate(new Key("space", "1"), new Callback<Long>() {
        @Override
        public void success(Long value) {
          sum.addAndGet(value);
        }

        @Override
        public void failure(ErrorCodeException ex) {

        }
      });
      if (k < 5) {
        Assert.assertEquals(1, count.get());
      }
      if (k > 5) {
        Assert.assertEquals(2, count.get());
      }
      if (k == 5) {
        service.forget(new Key("space", "1"));
      }
    }
    Assert.assertEquals(2, count.get());
    Assert.assertEquals(10, sum.get());
    service.translate(new Key("space", "fail"), new Callback<Long>() {
      @Override
      public void success(Long value) {
        Assert.fail();
      }

      @Override
      public void failure(ErrorCodeException ex) {

      }
    });
  }
}
