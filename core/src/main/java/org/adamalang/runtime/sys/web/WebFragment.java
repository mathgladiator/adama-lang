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
package org.adamalang.runtime.sys.web;

/** part of a URI. For example, if the URI = is /XYZ/123 then [XYZ] and [123] are fragments. */
public class WebFragment {
  public final String uri;
  public final String fragment;
  public final int tail;
  public final Boolean val_boolean;
  public final Integer val_int;
  public final Long val_long;
  public final Double val_double;

  public WebFragment(String uri, String fragment, int tail) {
    this.uri = uri;
    this.fragment = fragment;
    this.tail = tail;
    this.val_boolean = parseBoolean(fragment);
    this.val_int = parseInteger(fragment);
    this.val_long = parseLong(fragment);
    this.val_double = parseDouble(fragment);
  }

  private static final Boolean parseBoolean(String x) {
    if ("true".equalsIgnoreCase(x)) {
      return true;
    }
    if ("false".equalsIgnoreCase(x)) {
      return false;
    }
    return null;
  }

  private static final Integer parseInteger(String x) {
    try {
      return Integer.parseInt(x);
    } catch (NumberFormatException nfe) {
      return null;
    }
  }

  private static final Long parseLong(String x) {
    try {
      return Long.parseLong(x);
    } catch (NumberFormatException nfe) {
      return null;
    }
  }

  private static final Double parseDouble(String x) {
    try {
      return Double.parseDouble(x);
    } catch (NumberFormatException nfe) {
      return null;
    }
  }

  public String tail() {
    return uri.substring(tail);
  }
}
