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

import org.adamalang.common.cache.Measurable;

import java.util.ArrayList;

/** Tear down a URI into fragments */
public class WebPath implements Measurable {
  public String uri;
  public WebFragment[] fragments;

  public WebPath(String uri) {
    this.uri = uri;
    ArrayList<WebFragment> fragments = new ArrayList<>();
    int at = uri.indexOf('/') + 1; // skip the first slash
    while (at > 0) {
      int slashAt = uri.indexOf('/', at);
      if (slashAt >= 0) {
        fragments.add(new WebFragment(uri, uri.substring(at, slashAt), at));
        at = slashAt + 1;
      } else {
        fragments.add(new WebFragment(uri, uri.substring(at), at));
        at = -1;
      }
    }
    this.fragments = fragments.toArray(new WebFragment[fragments.size()]);
  }

  public WebFragment at(int k) {
    if (k < fragments.length) {
      return fragments[k];
    }
    return null;
  }

  public int size() {
    return fragments.length;
  }

  @Override
  public long measure() {
    return uri.length() * 3L + 32L * fragments.length + 64;
  }
}
