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

import org.adamalang.runtime.natives.NtMaybe;
import org.adamalang.runtime.natives.NtTimeSpan;
import org.adamalang.translator.reflect.Extension;
import org.adamalang.translator.reflect.HiddenType;

/** timespan */
public class LibTimeSpan {
  @Extension
  public static NtTimeSpan add(NtTimeSpan a, NtTimeSpan b) {
    return new NtTimeSpan(a.seconds + b.seconds);
  }

  @Extension
  public static NtTimeSpan multiply(NtTimeSpan y, double x) {
    return new NtTimeSpan(x * y.seconds);
  }

  @Extension
  public static NtTimeSpan makeFromSeconds(double x) {
    return new NtTimeSpan(x);
  }

  @Extension
  public static NtTimeSpan makeFromSeconds(int x) {
    return new NtTimeSpan(x);
  }

  @Extension
  public static NtTimeSpan makeFromMinutes(double x) {
    return new NtTimeSpan(x * 60);
  }

  @Extension
  public static NtTimeSpan makeFromMinutes(int x) {
    return new NtTimeSpan(x * 60);
  }

  @Extension
  public static double seconds(NtTimeSpan x) {
    return x.seconds;
  }

  @Extension
  public static @HiddenType(clazz = Double.class) NtMaybe<Double> seconds(@HiddenType(clazz = NtTimeSpan.class) NtMaybe<NtTimeSpan> mt) {
    if (mt.has()) {
      return new NtMaybe<>(mt.get().seconds);
    }
    return new NtMaybe<>();
  }

  @Extension
  public static double minutes(NtTimeSpan x) {
    return x.seconds / 60.0;
  }

  @Extension
  public static @HiddenType(clazz = Double.class) NtMaybe<Double> minutes(@HiddenType(clazz = NtTimeSpan.class) NtMaybe<NtTimeSpan> mt) {
    if (mt.has()) {
      return new NtMaybe<>(mt.get().seconds / 60.0);
    }
    return new NtMaybe<>();
  }

  @Extension
  public static double hours(NtTimeSpan x) {
    return x.seconds / 3600.0;
  }

  @Extension
  public static @HiddenType(clazz = Double.class) NtMaybe<Double> hours(@HiddenType(clazz = NtTimeSpan.class) NtMaybe<NtTimeSpan> mt) {
    if (mt.has()) {
      return new NtMaybe<>(mt.get().seconds / 3600.0);
    }
    return new NtMaybe<>();
  }
}
