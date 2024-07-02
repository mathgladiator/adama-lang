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
package org.adamalang.runtime.reactives;

import org.adamalang.runtime.mocks.MockRxParent;
import org.junit.Assert;
import org.junit.Test;

public class RxTableGuardTests {
  @Test
  public void root_all_pkey() {
    MockRxParent par = new MockRxParent();
    MockRxDependent dependent = new MockRxDependent(par);
    RxTableGuard guard = new RxTableGuard(dependent);
    guard.reset();
    guard.readAll();
    Assert.assertFalse(dependent.getAndResetInvalid());
    guard.primary(42);
    Assert.assertTrue(dependent.getAndResetInvalid());
  }

  @Test
  public void root_all_idx() {
    MockRxParent par = new MockRxParent();
    MockRxDependent dependent = new MockRxDependent(par);
    RxTableGuard guard = new RxTableGuard(dependent);
    guard.reset();
    guard.readAll();
    Assert.assertFalse(dependent.getAndResetInvalid());
    guard.index(123, 42);
    Assert.assertTrue(dependent.getAndResetInvalid());
  }

  @Test
  public void root_pkey_pkey_same() {
    MockRxParent par = new MockRxParent();
    MockRxDependent dependent = new MockRxDependent(par);
    RxTableGuard guard = new RxTableGuard(dependent);
    guard.reset();
    guard.readPrimaryKey(42);
    Assert.assertFalse(dependent.getAndResetInvalid());
    guard.primary(42);
    Assert.assertTrue(dependent.getAndResetInvalid());
  }

  @Test
  public void root_pkey_pkey_diff() {
    MockRxParent par = new MockRxParent();
    MockRxDependent dependent = new MockRxDependent(par);
    RxTableGuard guard = new RxTableGuard(dependent);
    guard.reset();
    guard.readPrimaryKey(42);
    Assert.assertFalse(dependent.getAndResetInvalid());
    guard.primary(52);
    Assert.assertFalse(dependent.getAndResetInvalid());
  }

  @Test
  public void root_pkey_idx() {
    MockRxParent par = new MockRxParent();
    MockRxDependent dependent = new MockRxDependent(par);
    RxTableGuard guard = new RxTableGuard(dependent);
    guard.reset();
    guard.readPrimaryKey(42);
    Assert.assertFalse(dependent.getAndResetInvalid());
    guard.index(24, 23);
    Assert.assertFalse(dependent.getAndResetInvalid());
  }

  @Test
  public void root_idx_pkey() {
    MockRxParent par = new MockRxParent();
    MockRxDependent dependent = new MockRxDependent(par);
    RxTableGuard guard = new RxTableGuard(dependent);
    guard.reset();
    guard.readIndexValue(1, 5);
    Assert.assertFalse(dependent.getAndResetInvalid());
    guard.primary(542);
    Assert.assertFalse(dependent.getAndResetInvalid());
  }

  @Test
  public void root_idx_idx_same_same() {
    MockRxParent par = new MockRxParent();
    MockRxDependent dependent = new MockRxDependent(par);
    RxTableGuard guard = new RxTableGuard(dependent);
    guard.reset();
    guard.readIndexValue(1, 5);
    Assert.assertFalse(dependent.getAndResetInvalid());
    guard.index(1, 5);
    Assert.assertTrue(dependent.getAndResetInvalid());
  }

  @Test
  public void root_idx_idx_diff_same() {
    MockRxParent par = new MockRxParent();
    MockRxDependent dependent = new MockRxDependent(par);
    RxTableGuard guard = new RxTableGuard(dependent);
    guard.reset();
    guard.readIndexValue(1, 5);
    Assert.assertFalse(dependent.getAndResetInvalid());
    guard.index(0, 5);
    Assert.assertFalse(dependent.getAndResetInvalid());
  }

  @Test
  public void root_idx_idx_same_diff() {
    MockRxParent par = new MockRxParent();
    MockRxDependent dependent = new MockRxDependent(par);
    RxTableGuard guard = new RxTableGuard(dependent);
    guard.reset();
    guard.readIndexValue(1, 5);
    Assert.assertFalse(dependent.getAndResetInvalid());
    guard.index(1, 4);
    Assert.assertFalse(dependent.getAndResetInvalid());
  }

  @Test
  public void child_all_pkey() {
    MockRxParent par = new MockRxParent();
    MockRxDependent dependent = new MockRxDependent(par);
    RxTableGuard guard = new RxTableGuard(dependent);
    guard.resetView(100);
    guard.readAll();
    Assert.assertFalse(guard.isFired(100));
    guard.primary(24);
    Assert.assertTrue(guard.isFired(100));
  }

  @Test
  public void child_all_idx() {
    MockRxParent par = new MockRxParent();
    MockRxDependent dependent = new MockRxDependent(par);
    RxTableGuard guard = new RxTableGuard(dependent);
    guard.resetView(100);
    guard.readAll();
    Assert.assertFalse(guard.isFired(100));
    guard.index(24, 42);
    Assert.assertTrue(guard.isFired(100));
  }

  @Test
  public void child_pkey_pkey_same() {
    MockRxParent par = new MockRxParent();
    MockRxDependent dependent = new MockRxDependent(par);
    RxTableGuard guard = new RxTableGuard(dependent);
    guard.resetView(100);
    guard.readPrimaryKey(42);
    Assert.assertFalse(guard.isFired(100));
    guard.primary(42);
    Assert.assertTrue(guard.isFired(100));
  }

  @Test
  public void child_pkey_pkey_diff() {
    MockRxParent par = new MockRxParent();
    MockRxDependent dependent = new MockRxDependent(par);
    RxTableGuard guard = new RxTableGuard(dependent);
    guard.resetView(100);
    guard.readPrimaryKey(42);
    Assert.assertFalse(guard.isFired(100));
    guard.primary(234);
    Assert.assertFalse(guard.isFired(100));
  }

  @Test
  public void child_idx_pkey() {
    MockRxParent par = new MockRxParent();
    MockRxDependent dependent = new MockRxDependent(par);
    RxTableGuard guard = new RxTableGuard(dependent);
    guard.resetView(100);
    guard.readIndexValue(1, 5);
    Assert.assertFalse(guard.isFired(100));
    guard.primary(542);
    Assert.assertFalse(guard.isFired(100));
  }

  @Test
  public void child_idx_idx_same_same() {
    MockRxParent par = new MockRxParent();
    MockRxDependent dependent = new MockRxDependent(par);
    RxTableGuard guard = new RxTableGuard(dependent);
    guard.resetView(100);
    guard.readIndexValue(1, 5);
    Assert.assertFalse(guard.isFired(100));
    guard.index(1, 5);
    Assert.assertTrue(guard.isFired(100));
  }

  @Test
  public void child_idx_idx_diff_same() {
    MockRxParent par = new MockRxParent();
    MockRxDependent dependent = new MockRxDependent(par);
    RxTableGuard guard = new RxTableGuard(dependent);
    guard.resetView(100);
    guard.readIndexValue(1, 5);
    Assert.assertFalse(guard.isFired(100));
    guard.index(0, 5);
    Assert.assertFalse(guard.isFired(100));
  }

  @Test
  public void child_idx_idx_same_diff() {
    MockRxParent par = new MockRxParent();
    MockRxDependent dependent = new MockRxDependent(par);
    RxTableGuard guard = new RxTableGuard(dependent);
    guard.resetView(100);
    guard.readIndexValue(1, 5);
    Assert.assertFalse(guard.isFired(100));
    guard.index(1, 4);
    Assert.assertFalse(guard.isFired(100));
  }

  @Test
  public void proxy_live() {
    MockRxParent par = new MockRxParent();
    MockRxDependent dependent = new MockRxDependent(par);
    RxTableGuard guard = new RxTableGuard(dependent);
    Assert.assertTrue(guard.alive());
    par.alive = false;
    Assert.assertFalse(guard.alive());
  }
}
