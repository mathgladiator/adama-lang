/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's Apache2); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.runtime.stdlib;

import org.adamalang.runtime.natives.NtList;
import org.adamalang.runtime.natives.NtMaybe;
import org.adamalang.runtime.natives.lists.ArrayNtList;
import org.adamalang.translator.reflect.Extension;
import org.adamalang.translator.reflect.HiddenType;
import org.adamalang.translator.reflect.HiddenTypes2;

import java.util.ArrayList;
import java.util.regex.Pattern;

/** a basic string library */
public class LibString {
  public static @HiddenTypes2(class1 = NtList.class, class2 = String.class)
  NtMaybe<NtList<String>> split(final @HiddenType(clazz = String.class) NtMaybe<String> sentence, final String word) {
    if (sentence.has()) {
      return new NtMaybe<>(split(sentence.get(), word));
    }
    return new NtMaybe<>();
  }

  @Extension
  public static @HiddenType(clazz = String.class)
  NtList<String> split(final String sentence, final String word) {
    ArrayList<String> list = new ArrayList<>();
    for (String part : sentence.split(Pattern.quote(word))) {
      list.add(part);
    }
    return new ArrayNtList<>(list);
  }

  public static @HiddenTypes2(class1 = NtList.class, class2 = String.class)
  NtMaybe<NtList<String>> split(final String sentence, final @HiddenType(clazz = String.class) NtMaybe<String> word) {
    if (word.has()) {
      return new NtMaybe<>(split(sentence, word.get()));
    }
    return new NtMaybe<>();
  }

  public static @HiddenTypes2(class1 = NtList.class, class2 = String.class)
  NtMaybe<NtList<String>> split(final @HiddenType(clazz = String.class) NtMaybe<String> sentence, final @HiddenType(clazz = String.class) NtMaybe<String> word) {
    if (sentence.has() && word.has()) {
      return new NtMaybe<>(split(sentence.get(), word.get()));
    }
    return new NtMaybe<>();
  }

  @Extension
  public static boolean contains(final String haystack, String needle) {
    return haystack.contains(needle);
  }

  @Extension
  public static @HiddenType(clazz = Boolean.class)
  NtMaybe<Boolean> contains(final @HiddenType(clazz = String.class) NtMaybe<String> haystack, String needle) {
    if (haystack.has()) {
      return new NtMaybe<>(haystack.get().contains(needle));
    }
    return new NtMaybe<>();
  }

  @Extension
  public static @HiddenType(clazz = Boolean.class)
  NtMaybe<Boolean> contains(final String haystack, final @HiddenType(clazz = String.class) NtMaybe<String> needle) {
    if (needle.has()) {
      return new NtMaybe<>(haystack.contains(needle.get()));
    }
    return new NtMaybe<>();
  }

  @Extension
  public static @HiddenType(clazz = Boolean.class)
  NtMaybe<Boolean> contains(final @HiddenType(clazz = String.class) NtMaybe<String> haystack, final @HiddenType(clazz = String.class) NtMaybe<String> needle) {
    if (haystack.has() && needle.has()) {
      return new NtMaybe<>(haystack.get().contains(needle.get()));
    }
    return new NtMaybe<>();
  }

  @Extension
  public static @HiddenType(clazz = Integer.class)
  NtMaybe<Integer> indexOf(final @HiddenType(clazz = String.class) NtMaybe<String> haystack, String needle) {
    if (haystack.has()) {
      return indexOf(haystack.get(), needle);
    }
    return new NtMaybe<>();
  }

  @Extension
  public static @HiddenType(clazz = Integer.class)
  NtMaybe<Integer> indexOf(final String haystack, String needle) {
    int value = haystack.indexOf(needle);
    if (value < 0) {
      return new NtMaybe<>();
    } else {
      return new NtMaybe<>(value);
    }
  }

  @Extension
  public static @HiddenType(clazz = Integer.class)
  NtMaybe<Integer> indexOf(final String haystack, final @HiddenType(clazz = String.class) NtMaybe<String> needle) {
    if (needle.has()) {
      return indexOf(haystack, needle.get());
    }
    return new NtMaybe<>();
  }

  @Extension
  public static @HiddenType(clazz = Integer.class)
  NtMaybe<Integer> indexOf(final @HiddenType(clazz = String.class) NtMaybe<String> haystack, final @HiddenType(clazz = String.class) NtMaybe<String> needle) {
    if (haystack.has() && needle.has()) {
      return indexOf(haystack.get(), needle.get());
    }
    return new NtMaybe<>();
  }

  @Extension
  public static @HiddenType(clazz = Integer.class)
  NtMaybe<Integer> indexOf(final @HiddenType(clazz = String.class) NtMaybe<String> haystack, String needle, int offset) {
    if (haystack.has()) {
      return indexOf(haystack.get(), needle, offset);
    }
    return new NtMaybe<>();
  }

  @Extension
  public static @HiddenType(clazz = Integer.class)
  NtMaybe<Integer> indexOf(final String haystack, String needle, int offset) {
    int value = haystack.indexOf(needle, offset);
    if (value < 0) {
      return new NtMaybe<>();
    } else {
      return new NtMaybe<>(value);
    }
  }

  @Extension
  public static @HiddenType(clazz = Integer.class)
  NtMaybe<Integer> indexOf(final String haystack, final @HiddenType(clazz = String.class) NtMaybe<String> needle, int offset) {
    if (needle.has()) {
      return indexOf(haystack, needle.get(), offset);
    }
    return new NtMaybe<>();
  }

  @Extension
  public static @HiddenType(clazz = Integer.class)
  NtMaybe<Integer> indexOf(final @HiddenType(clazz = String.class) NtMaybe<String> haystack, final @HiddenType(clazz = String.class) NtMaybe<String> needle, int offset) {
    if (haystack.has() && needle.has()) {
      return indexOf(haystack.get(), needle.get(), offset);
    }
    return new NtMaybe<>();
  }

  @Extension
  public static String trim(String s) {
    return s.strip();
  }

  @Extension
  public static @HiddenType(clazz = String.class)
  NtMaybe<String> trim(final @HiddenType(clazz = String.class) NtMaybe<String> s) {
    if (s.has()) {
      return new NtMaybe<>(s.get().strip());
    }
    return s;
  }

  @Extension
  public static String trimLeft(String s) {
    return s.stripLeading();
  }

  @Extension
  public static @HiddenType(clazz = String.class)
  NtMaybe<String> trimLeft(final @HiddenType(clazz = String.class) NtMaybe<String> s) {
    if (s.has()) {
      return new NtMaybe<>(s.get().stripLeading());
    }
    return s;
  }

  @Extension
  public static String trimRight(String s) {
    return s.stripTrailing();
  }

  @Extension
  public static @HiddenType(clazz = String.class)
  NtMaybe<String> trimRight(final @HiddenType(clazz = String.class) NtMaybe<String> s) {
    if (s.has()) {
      return new NtMaybe<>(s.get().stripTrailing());
    }
    return s;
  }

  @Extension
  public static String upper(String s) {
    return s.toUpperCase();
  }

  @Extension
  public static @HiddenType(clazz = String.class)
  NtMaybe<String> upper(final @HiddenType(clazz = String.class) NtMaybe<String> s) {
    if (s.has()) {
      return new NtMaybe<>(s.get().toUpperCase());
    }
    return s;
  }

  @Extension
  public static String lower(String s) {
    return s.toLowerCase();
  }

  @Extension
  public static @HiddenType(clazz = String.class)
  NtMaybe<String> lower(final @HiddenType(clazz = String.class) NtMaybe<String> s) {
    if (s.has()) {
      return new NtMaybe<>(s.get().toLowerCase());
    }
    return s;
  }

  @Extension
  public static @HiddenType(clazz = String.class)
  NtMaybe<String> mid(final @HiddenType(clazz = String.class) NtMaybe<String> s, int start, int num_chars) {
    if (s.has()) {
      return mid(s.get(), start, num_chars);
    }
    return s;
  }

  @Extension
  public static @HiddenType(clazz = String.class)
  NtMaybe<String> mid(String s, final int start, int num_chars) {
    int begin = start - 1;
    if (begin < 0 || begin >= s.length() || num_chars < 0) {
      return new NtMaybe<>();
    }
    int max_len = s.length() - begin;
    return new NtMaybe<>(s.substring(begin, begin + Math.min(max_len, num_chars)));
  }

  @Extension
  public static @HiddenType(clazz = String.class)
  NtMaybe<String> substr(final @HiddenType(clazz = String.class) NtMaybe<String> s, int start, int end) {
    if (s.has()) {
      return substr(s.get(), start, end);
    }
    return s;
  }

  @Extension
  public static @HiddenType(clazz = String.class)
  NtMaybe<String> substr(String s, final int start, int end) {
    if (start < 0 || start > s.length() || end < 0 || end > s.length() || end < start) {
      return new NtMaybe<>();
    }
    return new NtMaybe<>(s.substring(start, end));
  }

  @Extension
  public static @HiddenType(clazz = String.class)
  NtMaybe<String> left(@HiddenType(clazz = String.class) final NtMaybe<String> s, int n) {
    if (s.has()) {
      return left(s.get(), n);
    }
    return s;
  }

  @Extension
  public static @HiddenType(clazz = String.class)
  NtMaybe<String> left(final String s, int n) {
    if (n < 0) {
      return new NtMaybe<>();
    }
    return new NtMaybe<>(s.substring(0, Math.min(n, s.length())));
  }

  @Extension
  public static @HiddenType(clazz = String.class)
  NtMaybe<String> right(@HiddenType(clazz = String.class) final NtMaybe<String> s, int n) {
    if (s.has()) {
      return right(s.get(), n);
    }
    return s;
  }

  @Extension
  public static @HiddenType(clazz = String.class)
  NtMaybe<String> right(final String s, int n) {
    if (n < 0) {
      return new NtMaybe<>();
    }
    int len = s.length();
    return new NtMaybe<>(s.substring(Math.max(0, len - n), len));
  }

  @Extension
  public static boolean endsWith(String what, String suffix) {
    return what.endsWith(suffix);
  }

  @Extension
  public static @HiddenType(clazz = Boolean.class) NtMaybe<Boolean> endsWith(@HiddenType(clazz = String.class) NtMaybe<String> what, String suffix) {
    if (what.has()) {
      return new NtMaybe<>(what.get().endsWith(suffix));
    }
    return new NtMaybe<>();
  }

  @Extension
  public static boolean startsWith(String what, String suffix) {
    return what.startsWith(suffix);
  }

  @Extension
  public static @HiddenType(clazz = Boolean.class) NtMaybe<Boolean> startsWith(@HiddenType(clazz = String.class) NtMaybe<String> what, String suffix) {
    if (what.has()) {
      return new NtMaybe<>(what.get().startsWith(suffix));
    }
    return new NtMaybe<>();
  }

  @Extension
  public static int compare(final String a, final String b) {
    if (a == null && b == null) {
      return 0;
    }
    if (a == null && b != null) {
      return -1;
    }
    if (a != null && b == null) {
      return 1;
    }
    return a.compareTo(b);
  }

  @Extension
  public static boolean equality(final String a, final String b) {
    if (a == null && b == null) {
      return true;
    }
    if (a == null || b == null) {
      return false;
    }
    return a.equals(b);
  }

  @Extension
  public static @HiddenType(clazz = String.class)
  NtMaybe<String> multiply(final @HiddenType(clazz = String.class) NtMaybe<String> input, final int count) {
    if (input.has()) {
      return new NtMaybe<>(multiply(input.get(), count));
    }
    return input;
  }

  @Extension
  public static String multiply(final String input, final int count) {
    final var sb = new StringBuilder();
    for (var k = 0; k < count; k++) {
      sb.append(input);
    }
    return sb.toString();
  }

  @Extension
  public static @HiddenType(clazz = String.class)
  NtMaybe<String> reverse(final @HiddenType(clazz = String.class) NtMaybe<String> x) {
    if (x.has()) {
      return new NtMaybe<>(reverse(x.get()));
    }
    return x;
  }

  @Extension
  public static String reverse(final String x) {
    final var sb = new StringBuilder();
    sb.append(x);
    return sb.reverse().toString();
  }

  @Extension
  public static String charOf(int x) {
    return Character.toString(x);
  }

  @Extension
  public static @HiddenType(clazz = String.class)
  NtMaybe<String> charOf(@HiddenType(clazz = Integer.class) NtMaybe<Integer> x) {
    if (x.has()) {
      return new NtMaybe<>(Character.toString(x.get()));
    }
    return new NtMaybe<>();
  }

  public static String of(final boolean x) {
    return String.valueOf(x);
  }

  public static String of(final double x) {
    return String.valueOf(x);
  }

  public static String of(final int x) {
    return String.valueOf(x);
  }

  public static String of(final long x) {
    return String.valueOf(x);
  }
}
