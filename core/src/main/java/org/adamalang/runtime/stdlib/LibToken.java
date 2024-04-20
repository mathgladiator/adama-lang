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
import org.adamalang.translator.reflect.Extension;
import org.adamalang.translator.reflect.HiddenType;

import java.util.ArrayList;

/** generic methods for normalized tokens */
public class LibToken {
  @Extension
  public static final int[] sortAndUniqueAsIntTokens(@HiddenType(clazz = Integer.class) NtList<Integer> vals) {
    ArrayList<Integer> sorted = new ArrayList<>();
    for (Integer v : vals) {
      sorted.add(v);
    }
    sorted.sort(Integer::compareTo);
    ArrayList<Integer> unique = new ArrayList<>(sorted.size());
    int last = Integer.MIN_VALUE;
    for (Integer v : sorted) {
      if (v > last) {
        unique.add(v);
      }
      last = v;
    }
    int[] result = new int[unique.size()];
    for (int k = 0; k < unique.size(); k++) {
      result[k] = unique.get(k);
    }
    return result;
  }

  @Extension
  public static final String[] normalizeSortAndUniqueAsStringTokens(@HiddenType(clazz = String.class) NtList<String> vals) {
    ArrayList<String> sorted = new ArrayList<>();
    for (String v : vals) {
      sorted.add(v.trim().toLowerCase());
    }
    sorted.sort(String::compareTo);
    ArrayList<String> unique = new ArrayList<>(sorted.size());
    String last = "";
    for (String v : sorted) {
      if (v.compareTo(last) > 0) {
        unique.add(v);
      }
      last = v;
    }
    String[] result = new String[unique.size()];
    for (int k = 0; k < unique.size(); k++) {
      result[k] = unique.get(k);
    }
    return result;
  }

  @Extension
  public static final int[] intersect(int[] a, int[] b) {
    ArrayList<Integer> result = new ArrayList<>();
    int i = 0;
    int j = 0;
    while (i < a.length && j < b.length) {
      if (a[i] < b[j]) {
        i++;
      } else if (a[i] > b[j]) {
        j++;
      } else { // equals
        result.add(a[i]);
        i++;
        j++;
      }
    }
    int[] f = new int[result.size()];
    for (int k = 0; k < f.length; k++) {
      f[k] = result.get(k);
    }
    return f;
  }

  @Extension
  public static final String[] intersect(String[] a, String[] b) {
    ArrayList<String> result = new ArrayList<>();
    int i = 0;
    int j = 0;
    while (i < a.length && j < b.length) {
      int delta = a[i].compareTo(b[j]);
      if (delta < 0) {
        i++;
      } else if (delta > 0) {
        j++;
      } else { // equals
        result.add(a[i]);
        i++;
        j++;
      }
    }
    String[] f = new String[result.size()];
    for (int k = 0; k < f.length; k++) {
      f[k] = result.get(k);
    }
    return f;
  }
}
