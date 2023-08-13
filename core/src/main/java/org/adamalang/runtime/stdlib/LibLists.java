/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.runtime.stdlib;

import org.adamalang.runtime.natives.NtList;
import org.adamalang.runtime.natives.lists.ArrayNtList;
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
}
