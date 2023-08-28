/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.runtime.data;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.SimpleExecutor;
import org.adamalang.runtime.data.mocks.MockFinderService;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class BoundLocalFinderServiceTests {
  @Test
  public void flow() throws Exception {
    MockFinderService mock = new MockFinderService("the-machine");
    mock.bindLocal(new Key("space", "key"));
    BoundLocalFinderService finder = new BoundLocalFinderService(SimpleExecutor.NOW, mock, "the-region", "the-machine");
    CountDownLatch latch = new CountDownLatch(4);
    finder.find(new Key("space", "key"), new Callback<DocumentLocation>() {
      @Override
      public void success(DocumentLocation value) {
        latch.countDown();
      }

      @Override
      public void failure(ErrorCodeException ex) {
        ex.printStackTrace();
      }
    });
    finder.bind(new Key("space", "bound"), new Callback<Void>() {
      @Override
      public void success(Void value) {
        latch.countDown();
      }

      @Override
      public void failure(ErrorCodeException ex) {

      }
    });
    finder.find(new Key("space", "bound"), new Callback<DocumentLocation>() {
      @Override
      public void success(DocumentLocation value) {
        latch.countDown();
      }

      @Override
      public void failure(ErrorCodeException ex) {
        ex.printStackTrace();
      }
    });
    finder.backup(new Key("space", "bound"), new BackupResult("new-archive", 1, 100, 200), new Callback<Void>() {
      @Override
      public void success(Void value) {
        latch.countDown();
      }

      @Override
      public void failure(ErrorCodeException ex) {

      }
    });
    Assert.assertTrue(latch.await(1000, TimeUnit.MILLISECONDS));
    CountDownLatch latchAgain = new CountDownLatch(1);
    finder.find(new Key("space", "bound"), new Callback<DocumentLocation>() {
      @Override
      public void success(DocumentLocation value) {
        Assert.assertEquals("new-archive", value.archiveKey);
        latchAgain.countDown();
      }

      @Override
      public void failure(ErrorCodeException ex) {
        ex.printStackTrace();
      }
    });
    Assert.assertTrue(latchAgain.await(1000, TimeUnit.MILLISECONDS));
    CountDownLatch latchFree = new CountDownLatch(1);
    finder.free(new Key("space", "bound"), new Callback<Void>() {
      @Override
      public void success(Void value) {
        latchFree.countDown();
      }

      @Override
      public void failure(ErrorCodeException ex) {

      }
    });
    Assert.assertTrue(latchFree.await(1000, TimeUnit.MILLISECONDS));
    CountDownLatch latchLast = new CountDownLatch(3);
    finder.list(new Callback<List<Key>>() {
      @Override
      public void success(List<Key> value) {
        latchLast.countDown();
      }

      @Override
      public void failure(ErrorCodeException ex) {

      }
    });
    finder.listDeleted(new Callback<List<Key>>() {
      @Override
      public void success(List<Key> value) {
        latchLast.countDown();
      }

      @Override
      public void failure(ErrorCodeException ex) {

      }
    });
    finder.markDelete(new Key("space", "key"), new Callback<Void>() {
      @Override
      public void success(Void value) {
        finder.commitDelete(new Key("space", "key"), new Callback<Void>() {
          @Override
          public void success(Void value) {
            latch.countDown();
          }

          @Override
          public void failure(ErrorCodeException ex) {

          }
        });
      }

      @Override
      public void failure(ErrorCodeException ex) {

      }
    });
    Assert.assertTrue(latchFree.await(1000, TimeUnit.MILLISECONDS));
  }
}
