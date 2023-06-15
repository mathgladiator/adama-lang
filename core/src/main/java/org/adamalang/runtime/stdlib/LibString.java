/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.runtime.stdlib;

import com.lambdaworks.crypto.SCryptUtil;
import org.adamalang.runtime.natives.NtList;
import org.adamalang.runtime.natives.NtMaybe;
import org.adamalang.runtime.natives.lists.ArrayNtList;
import org.adamalang.translator.reflect.Extension;
import org.adamalang.translator.reflect.HiddenType;
import org.adamalang.translator.reflect.HiddenTypes2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.PrimitiveIterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** a basic string library */
public class LibString {

  @Extension
  public static String passwordHash(String password) {
    return SCryptUtil.scrypt(password, 16384, 8, 1);
  }

  @Extension
  public static boolean passwordCheck(String hash, String password) {
    try {
      return SCryptUtil.check(password, hash);
    } catch (Exception ex) {
      return false;
    }
  }

  public static @HiddenTypes2(class1 = NtList.class, class2 = String.class) NtMaybe<NtList<String>> split(final @HiddenType(clazz = String.class) NtMaybe<String> sentence, final String word) {
    if (sentence.has()) {
      return new NtMaybe<>(split(sentence.get(), word));
    }
    return new NtMaybe<>();
  }

  @Extension
  public static @HiddenType(clazz = String.class) NtList<String> split(final String sentence, final String word) {
    ArrayList<String> list = new ArrayList<>();
    Collections.addAll(list, sentence.split(Pattern.quote(word)));
    return new ArrayNtList<>(list);
  }

  public static @HiddenTypes2(class1 = NtList.class, class2 = String.class) NtMaybe<NtList<String>> split(final String sentence, final @HiddenType(clazz = String.class) NtMaybe<String> word) {
    if (word.has()) {
      return new NtMaybe<>(split(sentence, word.get()));
    }
    return new NtMaybe<>();
  }

  public static @HiddenTypes2(class1 = NtList.class, class2 = String.class) NtMaybe<NtList<String>> split(final @HiddenType(clazz = String.class) NtMaybe<String> sentence, final @HiddenType(clazz = String.class) NtMaybe<String> word) {
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
  public static @HiddenType(clazz = Boolean.class) NtMaybe<Boolean> contains(final @HiddenType(clazz = String.class) NtMaybe<String> haystack, String needle) {
    if (haystack.has()) {
      return new NtMaybe<>(haystack.get().contains(needle));
    }
    return new NtMaybe<>();
  }

  @Extension
  public static @HiddenType(clazz = Boolean.class) NtMaybe<Boolean> contains(final String haystack, final @HiddenType(clazz = String.class) NtMaybe<String> needle) {
    if (needle.has()) {
      return new NtMaybe<>(haystack.contains(needle.get()));
    }
    return new NtMaybe<>();
  }

  @Extension
  public static @HiddenType(clazz = Boolean.class) NtMaybe<Boolean> contains(final @HiddenType(clazz = String.class) NtMaybe<String> haystack, final @HiddenType(clazz = String.class) NtMaybe<String> needle) {
    if (haystack.has() && needle.has()) {
      return new NtMaybe<>(haystack.get().contains(needle.get()));
    }
    return new NtMaybe<>();
  }

  @Extension
  public static @HiddenType(clazz = Integer.class) NtMaybe<Integer> indexOf(final @HiddenType(clazz = String.class) NtMaybe<String> haystack, String needle) {
    if (haystack.has()) {
      return indexOf(haystack.get(), needle);
    }
    return new NtMaybe<>();
  }

  @Extension
  public static @HiddenType(clazz = Integer.class) NtMaybe<Integer> indexOf(final String haystack, String needle) {
    int value = haystack.indexOf(needle);
    if (value < 0) {
      return new NtMaybe<>();
    } else {
      return new NtMaybe<>(value);
    }
  }

  @Extension
  public static @HiddenType(clazz = Integer.class) NtMaybe<Integer> indexOf(final String haystack, final @HiddenType(clazz = String.class) NtMaybe<String> needle) {
    if (needle.has()) {
      return indexOf(haystack, needle.get());
    }
    return new NtMaybe<>();
  }

  @Extension
  public static @HiddenType(clazz = Integer.class) NtMaybe<Integer> indexOf(final @HiddenType(clazz = String.class) NtMaybe<String> haystack, final @HiddenType(clazz = String.class) NtMaybe<String> needle) {
    if (haystack.has() && needle.has()) {
      return indexOf(haystack.get(), needle.get());
    }
    return new NtMaybe<>();
  }

  @Extension
  public static @HiddenType(clazz = Integer.class) NtMaybe<Integer> indexOf(final @HiddenType(clazz = String.class) NtMaybe<String> haystack, String needle, int offset) {
    if (haystack.has()) {
      return indexOf(haystack.get(), needle, offset);
    }
    return new NtMaybe<>();
  }

  @Extension
  public static @HiddenType(clazz = Integer.class) NtMaybe<Integer> indexOf(final String haystack, String needle, int offset) {
    int value = haystack.indexOf(needle, offset);
    if (value < 0) {
      return new NtMaybe<>();
    } else {
      return new NtMaybe<>(value);
    }
  }

  @Extension
  public static @HiddenType(clazz = Integer.class) NtMaybe<Integer> indexOf(final String haystack, final @HiddenType(clazz = String.class) NtMaybe<String> needle, int offset) {
    if (needle.has()) {
      return indexOf(haystack, needle.get(), offset);
    }
    return new NtMaybe<>();
  }

  @Extension
  public static @HiddenType(clazz = Integer.class) NtMaybe<Integer> indexOf(final @HiddenType(clazz = String.class) NtMaybe<String> haystack, final @HiddenType(clazz = String.class) NtMaybe<String> needle, int offset) {
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
  public static @HiddenType(clazz = String.class) NtMaybe<String> trim(final @HiddenType(clazz = String.class) NtMaybe<String> s) {
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
  public static @HiddenType(clazz = String.class) NtMaybe<String> trimLeft(final @HiddenType(clazz = String.class) NtMaybe<String> s) {
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
  public static @HiddenType(clazz = String.class) NtMaybe<String> trimRight(final @HiddenType(clazz = String.class) NtMaybe<String> s) {
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
  public static @HiddenType(clazz = String.class) NtMaybe<String> upper(final @HiddenType(clazz = String.class) NtMaybe<String> s) {
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
  public static @HiddenType(clazz = String.class) NtMaybe<String> lower(final @HiddenType(clazz = String.class) NtMaybe<String> s) {
    if (s.has()) {
      return new NtMaybe<>(s.get().toLowerCase());
    }
    return s;
  }

  @Extension
  public static @HiddenType(clazz = String.class) NtMaybe<String> mid(final @HiddenType(clazz = String.class) NtMaybe<String> s, int start, int num_chars) {
    if (s.has()) {
      return mid(s.get(), start, num_chars);
    }
    return s;
  }

  @Extension
  public static @HiddenType(clazz = String.class) NtMaybe<String> mid(String s, final int start, int num_chars) {
    int begin = start - 1;
    if (begin < 0 || begin >= s.length() || num_chars < 0) {
      return new NtMaybe<>();
    }
    int max_len = s.length() - begin;
    return new NtMaybe<>(s.substring(begin, begin + Math.min(max_len, num_chars)));
  }

  @Extension
  public static @HiddenType(clazz = String.class) NtMaybe<String> substr(final @HiddenType(clazz = String.class) NtMaybe<String> s, int start, int end) {
    if (s.has()) {
      return substr(s.get(), start, end);
    }
    return s;
  }

  @Extension
  public static @HiddenType(clazz = String.class) NtMaybe<String> substr(String s, final int start, int end) {
    if (start < 0 || start > s.length() || end < 0 || end > s.length() || end < start) {
      return new NtMaybe<>();
    }
    return new NtMaybe<>(s.substring(start, end));
  }

  @Extension
  public static @HiddenType(clazz = String.class) NtMaybe<String> left(@HiddenType(clazz = String.class) final NtMaybe<String> s, int n) {
    if (s.has()) {
      return left(s.get(), n);
    }
    return s;
  }

  @Extension
  public static @HiddenType(clazz = String.class) NtMaybe<String> left(final String s, int n) {
    if (n < 0) {
      return new NtMaybe<>();
    }
    return new NtMaybe<>(s.substring(0, Math.min(n, s.length())));
  }

  @Extension
  public static @HiddenType(clazz = String.class) NtMaybe<String> right(@HiddenType(clazz = String.class) final NtMaybe<String> s, int n) {
    if (s.has()) {
      return right(s.get(), n);
    }
    return s;
  }

  @Extension
  public static @HiddenType(clazz = String.class) NtMaybe<String> right(final String s, int n) {
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
  public static @HiddenType(clazz = String.class) NtMaybe<String> multiply(final @HiddenType(clazz = String.class) NtMaybe<String> input, final int count) {
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
  public static @HiddenType(clazz = String.class) NtMaybe<String> reverse(final @HiddenType(clazz = String.class) NtMaybe<String> x) {
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
  public static @HiddenType(clazz = String.class) NtMaybe<String> charOf(@HiddenType(clazz = Integer.class) NtMaybe<Integer> x) {
    if (x.has()) {
      return new NtMaybe<>(Character.toString(x.get()));
    }
    return new NtMaybe<>();
  }

  @Extension
  public static String join(@HiddenType(clazz = String.class) NtList<String> list, String delimitor) {
    return String.join(delimitor, list);
  }

  @Extension
  public static String concat(@HiddenType(clazz = String.class) NtList<String> list) {
    return String.join("", list);
  }

  @Extension
  public static String join(String[] list, String delimitor) {
    return String.join(delimitor, list);
  }

  @Extension
  public static String concat(String[] list) {
    return String.join("", list);
  }

  @Extension
  public static String replaceAll(String haystack, String oldNeedle, String newNeedle) {
    return haystack.replaceAll(Pattern.quote(oldNeedle), Matcher.quoteReplacement(newNeedle));
  }

  @Extension
  public static String removeAll(String haystack, String needle) {
    return haystack.replaceAll(Pattern.quote(needle), "");
  }

  @Extension
  public static @HiddenType(clazz = Integer.class) NtMaybe<Integer> codepointAt(String str, int index) {
    try {
      return new NtMaybe<>(str.codePointAt(index));
    } catch (Exception ex) {
      return new NtMaybe<>();
    }
  }

  @Extension
  public static @HiddenType(clazz = Integer.class) NtList<Integer> codepoints(String str) {
    ArrayList<Integer> codepoints = new ArrayList<>();
    PrimitiveIterator.OfInt it = str.codePoints().iterator();
    while (it.hasNext()) {
      codepoints.add(it.next());
    }
    return new ArrayNtList<>(codepoints);
  }

  private static class RomanStage {
    public final int value;
    public final String label;
    public RomanStage(int value, String label) {
      this.value = value;
      this.label = label;
    }
  }

  private static RomanStage[] ROMAN_HUNDREDS = new RomanStage[] {
      new RomanStage(900, "CM"),
      new RomanStage(800, "DCCC"),
      new RomanStage(700, "DCC"),
      new RomanStage(600, "DC"),
      new RomanStage(500, "D"),
      new RomanStage(400, "CD"),
      new RomanStage(300, "CCC"),
      new RomanStage(200, "CC"),
      new RomanStage(100, "C")
  };
  private static RomanStage[] ROMAN_TENS = new RomanStage[] {
      new RomanStage(90, "XC"),
      new RomanStage(80, "LXXX"),
      new RomanStage(70, "LXX"),
      new RomanStage(60, "LX"),
      new RomanStage(50, "L"),
      new RomanStage(40, "XL"),
      new RomanStage(30, "XXX"),
      new RomanStage(20, "XX"),
      new RomanStage(10, "X")
  };

  private static RomanStage[] ROMAN_ONES = new RomanStage[] {
      new RomanStage(9, "IX"),
      new RomanStage(8, "VIII"),
      new RomanStage(7, "VII"),
      new RomanStage(6, "VI"),
      new RomanStage(5, "V"),
      new RomanStage(4, "IV"),
      new RomanStage(3, "III"),
      new RomanStage(2, "II"),
      new RomanStage(1, "I")
  };

  private static RomanStage[][] ROMAN_STAGES = new RomanStage[][] { ROMAN_HUNDREDS, ROMAN_TENS, ROMAN_ONES };

  @Extension
  public static String to_roman(int x) {
    StringBuilder sb = new StringBuilder();
    while (x >= 1000) {
      sb.append("M");
      x -= 1000;
    }
    for (RomanStage[] s : ROMAN_STAGES) {
      for (RomanStage rs : s) {
        if (x >= rs.value) {
          x -= rs.value;
          sb.append(rs.label);
        }
      }
    }
    return sb.toString();
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
