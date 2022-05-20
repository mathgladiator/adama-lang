/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.runtime.data;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

public class CachedFinderProxyTests {
  private static final Key KEY = new Key("space", "key");
  @Test
  public void flow() {
    MockFinderService finder = new MockFinderService();
    CachedFinderProxy proxy = new CachedFinderProxy(finder);
    AtomicInteger v = new AtomicInteger(1);
    proxy.find(KEY, new Callback<FinderService.Result>() {
      @Override
      public void success(FinderService.Result value) {
      }

      @Override
      public void failure(ErrorCodeException ex) {
        v.incrementAndGet();
      }
    });


    proxy.create(KEY, new Callback<Void>() {
      @Override
      public void success(Void value) {
        v.incrementAndGet();
      }

      @Override
      public void failure(ErrorCodeException ex) {

      }
    });

    proxy.find(KEY, new Callback<FinderService.Result>() {
      @Override
      public void success(FinderService.Result value) {
        v.incrementAndGet();
      }

      @Override
      public void failure(ErrorCodeException ex) {

      }
    });
    proxy.find(KEY, new Callback<FinderService.Result>() {
      @Override
      public void success(FinderService.Result value) {
        v.incrementAndGet();
      }

      @Override
      public void failure(ErrorCodeException ex) {

      }
    });
    proxy.nuke(KEY);
    proxy.find(KEY, new Callback<FinderService.Result>() {
      @Override
      public void success(FinderService.Result value) {
        v.incrementAndGet();
      }

      @Override
      public void failure(ErrorCodeException ex) {

      }
    });
    proxy.set(KEY, "region", "machine", new Callback<Void>() {
      @Override
      public void success(Void value) {
        v.incrementAndGet();
      }

      @Override
      public void failure(ErrorCodeException ex) {

      }
    });
    proxy.archive(KEY, "KEY", "machine", new Callback<Void>() {
      @Override
      public void success(Void value) {
        v.incrementAndGet();
      }

      @Override
      public void failure(ErrorCodeException ex) {

      }
    });
    proxy.update(KEY, 1, 2, new Callback<Void>() {
      @Override
      public void success(Void value) {
        v.incrementAndGet();
      }

      @Override
      public void failure(ErrorCodeException ex) {

      }
    });
    proxy.delete(KEY, "machine", new Callback<Void>() {
      @Override
      public void success(Void value) {
        v.incrementAndGet();
      }

      @Override
      public void failure(ErrorCodeException ex) {

      }
    });

    Assert.assertEquals(10, v.get());
  }
}
