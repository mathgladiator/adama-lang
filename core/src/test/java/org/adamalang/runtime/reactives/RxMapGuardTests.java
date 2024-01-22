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
package org.adamalang.runtime.reactives;

import org.adamalang.runtime.mocks.MockRxParent;
import org.junit.Assert;
import org.junit.Test;

public class RxMapGuardTests {
  @Test
  public void root_changed() {
    MockRxParent par = new MockRxParent();
    MockRxDependent dependent = new MockRxDependent(par);
    RxMapGuard<String> guard = new RxMapGuard<>(dependent);
    guard.reset();
    guard.readAll();
    Assert.assertFalse(dependent.getAndResetInvalid());
    guard.changed("foo");
    Assert.assertTrue(dependent.getAndResetInvalid());
  }

  @Test
  public void root_specific_miss() {
    MockRxParent par = new MockRxParent();
    MockRxDependent dependent = new MockRxDependent(par);
    RxMapGuard<String> guard = new RxMapGuard<>(dependent);
    guard.reset();
    guard.readKey("zoo");
    Assert.assertFalse(dependent.getAndResetInvalid());
    guard.changed("foo");
    Assert.assertFalse(dependent.getAndResetInvalid());
  }

  @Test
  public void root_specific_hit() {
    MockRxParent par = new MockRxParent();
    MockRxDependent dependent = new MockRxDependent(par);
    RxMapGuard<String> guard = new RxMapGuard<>(dependent);
    guard.reset();
    guard.readKey("foo");
    Assert.assertFalse(dependent.getAndResetInvalid());
    guard.changed("foo");
    Assert.assertTrue(dependent.getAndResetInvalid());
  }

  @Test
  public void child_change() {
    MockRxParent par = new MockRxParent();
    MockRxDependent dependent = new MockRxDependent(par);
    RxMapGuard<String> guard = new RxMapGuard<>(dependent);
    guard.resetView(100);
    guard.readAll();
    Assert.assertFalse(guard.isFired(100));
    guard.changed("foo");
    Assert.assertTrue(guard.isFired(100));
  }

  @Test
  public void child_specific_miss() {
    MockRxParent par = new MockRxParent();
    MockRxDependent dependent = new MockRxDependent(par);
    RxMapGuard<String> guard = new RxMapGuard<>(dependent);
    guard.resetView(100);
    guard.readKey("zoo");
    Assert.assertFalse(guard.isFired(100));
    guard.changed("foo");
    Assert.assertFalse(guard.isFired(100));
  }

  @Test
  public void child_specific_hit() {
    MockRxParent par = new MockRxParent();
    MockRxDependent dependent = new MockRxDependent(par);
    RxMapGuard<String> guard = new RxMapGuard<>(dependent);
    guard.resetView(100);
    guard.readKey("foo");
    Assert.assertFalse(guard.isFired(100));
    guard.changed("foo");
    Assert.assertTrue(guard.isFired(100));
  }
}
