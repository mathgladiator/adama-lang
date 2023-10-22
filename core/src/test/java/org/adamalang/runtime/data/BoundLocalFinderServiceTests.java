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

  @Test
  public void success_bind_even_after_find_failure() throws Exception {
    MockFinderService mock = new MockFinderService("the-machine");
    mock.bindLocal(new Key("space", "key"));
    BoundLocalFinderService finder = new BoundLocalFinderService(SimpleExecutor.NOW, mock, "the-region", "the-machine");
    CountDownLatch latch = new CountDownLatch(2);
    finder.bind(new Key("space", "cant-find"), new Callback<Void>() {
      @Override
      public void success(Void value) {
        latch.countDown();
      }

      @Override
      public void failure(ErrorCodeException ex) {

      }
    });
    finder.find(new Key("space", "cant-find"), new Callback<DocumentLocation>() {
      @Override
      public void success(DocumentLocation value) {

      }

      @Override
      public void failure(ErrorCodeException ex) {
        latch.countDown();
      }
    });
    Assert.assertTrue(latch.await(10000, TimeUnit.MILLISECONDS));
  }
}
