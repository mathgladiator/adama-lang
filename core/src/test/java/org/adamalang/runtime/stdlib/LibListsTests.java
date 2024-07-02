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
package org.adamalang.runtime.stdlib;

import org.adamalang.runtime.natives.NtList;
import org.adamalang.runtime.natives.NtMaybe;
import org.adamalang.runtime.natives.lists.ArrayNtList;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

public class LibListsTests {
  @Test
  public void flatten() {
    ArrayList<NtList<Integer>> x = new ArrayList<>();
    {
      ArrayList<Integer> z = new ArrayList<>();
      z.add(1);
      z.add(2);
      z.add(3);
      x.add(new ArrayNtList<>(z));
    }
    {
      ArrayList<Integer> z = new ArrayList<>();
      z.add(4);
      z.add(5);
      x.add(new ArrayNtList<>(z));
    }
    {
      ArrayList<Integer> z = new ArrayList<>();
      z.add(6);
      z.add(7);
      x.add(new ArrayNtList<>(z));
    }
    NtList<NtList<Integer>> result = new ArrayNtList<>(x);
    NtList<Integer> vals = LibLists.flatten(result);
    Assert.assertEquals(7, vals.size());
    for (int k = 0; k < 7; k++) {
      Assert.assertEquals(k + 1, (int) vals.lookup(k).get());
    }
  }

  @Test
  public void manifest() {
    ArrayList<NtMaybe<Integer>> x = new ArrayList<>();
    x.add(new NtMaybe<>(123));
    x.add(new NtMaybe<>());
    x.add(new NtMaybe<>(42));
    x.add(new NtMaybe<>(0));
    x.add(new NtMaybe<>());
    x.add(new NtMaybe<>(-13));
    NtList<Integer> result = LibLists.manifest(new ArrayNtList<>(x));
    Assert.assertEquals(4, result.size());
    Assert.assertEquals(123, (int) result.lookup(0).get());
    Assert.assertEquals(42, (int) result.lookup(1).get());
    Assert.assertEquals(0, (int) result.lookup(2).get());
    Assert.assertEquals(-13, (int) result.lookup(3).get());
  }

  @Test
  public void reverse() {
    ArrayList<Integer> z = new ArrayList<>();
    z.add(1);
    z.add(2);
    z.add(3);
    NtList<Integer> vals = LibLists.reverse(new ArrayNtList<>(z));
    Assert.assertEquals(3, vals.size());
    for (int k = 0; k < 3; k++) {
      Assert.assertEquals(3 - k, (int) vals.lookup(k).get());
    }
  }

  @Test
  public void skip() {
    ArrayList<Integer> z = new ArrayList<>();
    z.add(1);
    z.add(2);
    z.add(3);
    z.add(4);
    z.add(5);
    NtList<Integer> vals = LibLists.skip(new ArrayNtList<>(z), 2);
    Assert.assertEquals(3, vals.size());
    for (int k = 0; k < 3; k++) {
      Assert.assertEquals(3 + k, (int) vals.lookup(k).get());
    }
  }

  @Test
  public void drop() {
    ArrayList<Integer> z = new ArrayList<>();
    z.add(1);
    z.add(2);
    z.add(3);
    z.add(4);
    z.add(5);
    NtList<Integer> vals = LibLists.drop(new ArrayNtList<>(z), 2);
    Assert.assertEquals(3, vals.size());
    for (int k = 0; k < 3; k++) {
      Assert.assertEquals(1 + k, (int) vals.lookup(k).get());
    }
  }
}
