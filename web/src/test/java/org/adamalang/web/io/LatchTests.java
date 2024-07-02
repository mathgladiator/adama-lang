/*
* Adama Platform and Language
* Copyright (C) 2021 - 2024 by Adama Platform Engineering, LLC
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
package org.adamalang.web.io;

import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.SimpleExecutor;
import org.junit.Test;

public class LatchTests {

  @Test
  public void basic() {
    MockIntegerCallback callback = new MockIntegerCallback();
    BulkLatch<Integer> bl = new BulkLatch<>(SimpleExecutor.NOW, 1, callback);
    bl.with(() -> 42);
    bl.countdown(null);
    callback.assertValue(42);
  }

  @Test
  public void withRef() {
    MockIntegerCallback callback = new MockIntegerCallback();
    BulkLatch<Integer> bl = new BulkLatch<>(SimpleExecutor.NOW, 2, callback);
    LatchRefCallback<Integer> ref1 = new LatchRefCallback<>(bl);
    LatchRefCallback<Integer> ref2 = new LatchRefCallback<>(bl);
    bl.with(() -> ref1.get() + ref2.get());
    ref1.success(50);
    ref2.success(32);
    callback.assertValue(82);
  }

  @Test
  public void failure1() {
    MockIntegerCallback callback = new MockIntegerCallback();
    BulkLatch<Integer> bl = new BulkLatch<>(SimpleExecutor.NOW, 2, callback);
    LatchRefCallback<Integer> ref1 = new LatchRefCallback<>(bl);
    LatchRefCallback<Integer> ref2 = new LatchRefCallback<>(bl);
    bl.with(() -> ref1.get() + ref2.get());
    ref1.failure(new ErrorCodeException(2));
    ref2.success(32);
    callback.assertErrorCode(2);
  }

  @Test
  public void failure2() {
    MockIntegerCallback callback = new MockIntegerCallback();
    BulkLatch<Integer> bl = new BulkLatch<>(SimpleExecutor.NOW, 2, callback);
    LatchRefCallback<Integer> ref1 = new LatchRefCallback<>(bl);
    LatchRefCallback<Integer> ref2 = new LatchRefCallback<>(bl);
    bl.with(() -> ref1.get() + ref2.get());
    ref1.success(32);
    ref2.failure(new ErrorCodeException(7));
    callback.assertErrorCode(7);
  }

  @Test
  public void failureBoth1() {
    MockIntegerCallback callback = new MockIntegerCallback();
    BulkLatch<Integer> bl = new BulkLatch<>(SimpleExecutor.NOW, 2, callback);
    LatchRefCallback<Integer> ref1 = new LatchRefCallback<>(bl);
    LatchRefCallback<Integer> ref2 = new LatchRefCallback<>(bl);
    bl.with(() -> ref1.get() + ref2.get());
    ref1.failure(new ErrorCodeException(2));
    ref2.failure(new ErrorCodeException(7));
    callback.assertErrorCode(2);
  }

  @Test
  public void failureBoth2() {
    MockIntegerCallback callback = new MockIntegerCallback();
    BulkLatch<Integer> bl = new BulkLatch<>(SimpleExecutor.NOW, 2, callback);
    LatchRefCallback<Integer> ref1 = new LatchRefCallback<>(bl);
    LatchRefCallback<Integer> ref2 = new LatchRefCallback<>(bl);
    bl.with(() -> ref1.get() + ref2.get());
    ref2.failure(new ErrorCodeException(7));
    ref1.failure(new ErrorCodeException(2));
    callback.assertErrorCode(2);
  }
}
