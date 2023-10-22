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
package org.adamalang.runtime.stdlib;

import org.adamalang.runtime.natives.NtList;
import org.adamalang.runtime.natives.lists.ArrayNtList;
import org.adamalang.runtime.natives.lists.JoinNtList;
import org.adamalang.translator.reflect.Skip;

import java.util.ArrayList;
import java.util.Stack;

public class LibLists {
  @Skip
  public static <T> NtList<T> flatten(NtList<NtList<T>> list) {
    ArrayList<T> result = new ArrayList<>();
    for (NtList<T> sub : list) {
      for (T item : sub) {
        result.add(item);
      }
    }
    return new ArrayNtList<>(result);
  }

  @Skip
  public static <T> NtList<T> reverse(NtList<T> list) {
    Stack<T> stack = new Stack<>();
    ArrayList<T> result = new ArrayList<>();
    for (T item : list) {
      stack.push(item);
    }
    while (!stack.empty()) {
      result.add(stack.pop());
    }
    return new ArrayNtList<>(result);
  }

  @Skip
  public static <T> NtList<T> skip(NtList<T> list, int count) {
    ArrayList<T> result = new ArrayList<>();
    int skip = count;
    for (T item : list) {
      if (skip == 0) {
        result.add(item);
      } else {
        skip--;
      }
    }
    return new ArrayNtList<>(result);
  }

  @Skip
  public static <T> NtList<T> drop(NtList<T> list, int count) {
    ArrayList<T> result = new ArrayList<>();
    int keep = list.size() - count;
    for (T item : list) {
      if (keep > 0) {
        result.add(item);
      }
      keep--;
    }
    return new ArrayNtList<>(result);
  }

  @Skip
  public static <T> NtList<T> join(NtList<T> a, NtList<T> b) {
    return new JoinNtList<>(a, b);
  }
}
