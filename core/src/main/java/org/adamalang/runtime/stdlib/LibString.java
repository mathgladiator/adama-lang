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

import com.lambdaworks.crypto.SCryptUtil;
import org.adamalang.runtime.natives.NtList;
import org.adamalang.runtime.natives.NtMaybe;
import org.adamalang.runtime.natives.lists.ArrayNtList;
import org.adamalang.translator.reflect.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.PrimitiveIterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** a basic string library */
public class LibString {

  @Extension
  @Deprecated
  public static String passwordHash(String password) {
    return SCryptUtil.scrypt(password, 16384, 8, 1);
  }

  @Extension
  @Deprecated
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
  @UseName(name="join")
  public static  @HiddenType(clazz = String.class) NtMaybe<String> joinMaybes(@HiddenTypes2(class1 = NtMaybe.class, class2 = String.class) NtList<NtMaybe<String>> list, String delimitor) {
    ArrayList<String> real = new ArrayList<>();
    for (NtMaybe<String> item : list) {
      if (item.has()) {
        real.add(item.get());
      }
    }
    if (real.size() > 0) {
      return new NtMaybe<>(String.join(delimitor, real));
    } else {
      return new NtMaybe<>();
    }
  }

  @Extension
  @UseName(name="concat")
  public static  @HiddenType(clazz = String.class) NtMaybe<String> concatMaybes(@HiddenTypes2(class1 = NtMaybe.class, class2 = String.class) NtList<NtMaybe<String>> list) {
    return joinMaybes(list, "");
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

  @Extension
  public static String strOf(final boolean x) {
    return String.valueOf(x);
  }

  @Extension
  public static String strOf(final double x) {
    return String.valueOf(x);
  }

  @Extension
  public static String strOf(final int x) {
    return String.valueOf(x);
  }

  @Extension
  public static String strOf(final long x) {
    return String.valueOf(x);
  }

  @Extension
  public static @HiddenType(clazz = Integer.class) NtMaybe<Integer> intOf(final String x) {
    try {
      return new NtMaybe<>(Integer.parseInt(x));
    } catch (NumberFormatException nfe) {
      return new NtMaybe<>();
    }
  }

  @Extension
  public static @HiddenType(clazz = Long.class) NtMaybe<Long> longOf(final String x) {
    try {
      return new NtMaybe<>(Long.parseLong(x));
    } catch (NumberFormatException nfe) {
      return new NtMaybe<>();
    }
  }

  @Extension
  public static @HiddenType(clazz = Double.class) NtMaybe<Double> doubleOf(final String x) {
    try {
      return new NtMaybe<>(Double.parseDouble(x));
    } catch (NumberFormatException nfe) {
      return new NtMaybe<>();
    }
  }

  @Extension
  public static @HiddenType(clazz = Integer.class) NtMaybe<Integer> intOf(final @HiddenType(clazz = String.class) NtMaybe<String> x) {
    try {
      if (x.has()) {
        return new NtMaybe<>(Integer.parseInt(x.get()));
      }
    } catch (NumberFormatException nfe) {
    }
    return new NtMaybe<>();
  }

  @Extension
  public static @HiddenType(clazz = Long.class) NtMaybe<Long> longOf(final @HiddenType(clazz = String.class) NtMaybe<String> x) {
    try {
      if (x.has()) {
        return new NtMaybe<>(Long.parseLong(x.get()));
      }
    } catch (NumberFormatException nfe) {
    }
    return new NtMaybe<>();
  }

  @Extension
  public static @HiddenType(clazz = Double.class) NtMaybe<Double> doubleOf(final @HiddenType(clazz = String.class) NtMaybe<String> x) {
    try {
      if (x.has()) {
        return new NtMaybe<>(Double.parseDouble(x.get()));
      }
    } catch (NumberFormatException nfe) {
    }
    return new NtMaybe<>();
  }

  @Extension
  public static String hexOf(int x) {
    return Integer.toString(x, 16);
  }

  @Extension
  public static String hexOf(long x) {
    return Long.toString(x, 16);
  }

  @Extension
  @UseName(name="hexOf")
  public static @HiddenType(clazz=String.class) NtMaybe<String> hexOfI(@HiddenType(clazz=Integer.class) NtMaybe<Integer> x) {
    if (x.has()) {
      return new NtMaybe<>(Integer.toString(x.get(), 16));
    }
    return new NtMaybe<>();
  }

  @Extension
  @UseName(name="hexOf")
  public static @HiddenType(clazz=String.class) NtMaybe<String> hexOfL(@HiddenType(clazz=Long.class) NtMaybe<Long> x) {
    if (x.has()) {
      return new NtMaybe<>(Long.toString(x.get(), 16));
    }
    return new NtMaybe<>();
  }

  @Extension
  public static @HiddenType(clazz=Integer.class) NtMaybe<Integer> intFromHex(String hex) {
    try {
      return new NtMaybe<>(Integer.parseInt(hex, 16));
    } catch (NumberFormatException nfe) {
      return new NtMaybe<>();
    }
  }

  @Extension
  public static @HiddenType(clazz=Long.class) NtMaybe<Long> longFromHex(String hex) {
    try {
      return new NtMaybe<>(Long.parseLong(hex, 16));
    } catch (NumberFormatException nfe) {
      return new NtMaybe<>();
    }
  }

  @Extension
  public static @HiddenType(clazz=Integer.class) NtMaybe<Integer> intFromHex(@HiddenType(clazz=String.class) NtMaybe<String> hex) {
    try {
      if (hex.has()) {
        return new NtMaybe<>(Integer.parseInt(hex.get(), 16));
      }
    } catch (NumberFormatException nfe) {
    }
    return new NtMaybe<>();
  }

  @Extension
  public static @HiddenType(clazz=Long.class) NtMaybe<Long> longFromHex(@HiddenType(clazz=String.class) NtMaybe<String> hex) {
    try {
      if (hex.has()) {
        return new NtMaybe<>(Long.parseLong(hex.get(), 16));
      }
    } catch (NumberFormatException nfe) {
    }
    return new NtMaybe<>();
  }

  @Extension
  public static String initialsOf(String x, boolean dots) {
    ArrayList<String> parts = new ArrayList<>();
    for (String p : x.split(Pattern.quote(" "))) {
      if (p.length() > 0) {
        parts.add(p.substring(0, 1));
      }
    }
    return String.join(dots ? "." : "", parts);
  }

  @Extension
  public static @HiddenType(clazz=String.class) NtMaybe<String> initialsOf(@HiddenType(clazz=String.class) NtMaybe<String> x, boolean dots) {
    if (x.has()) {
      return new NtMaybe<>(initialsOf(x.get(), dots));
    } else {
      return x;
    }
  }

  @Extension
  public static int compareInsensitive(String x, String y) {
    return x.compareToIgnoreCase(y);
  }
}
