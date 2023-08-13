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
}
