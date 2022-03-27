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
    proxy.takeover(KEY, new Callback<Void>() {
      @Override
      public void success(Void value) {
        v.incrementAndGet();
      }

      @Override
      public void failure(ErrorCodeException ex) {

      }
    });
    proxy.archive(KEY, "KEY", new Callback<Void>() {
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

    Assert.assertEquals(9, v.get());
  }
}
