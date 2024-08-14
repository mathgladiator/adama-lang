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
package org.adamalang.runtime.sys.web.rxhtml;

import org.adamalang.common.cache.Measurable;
import org.adamalang.rxhtml.routing.Table;

import java.nio.charset.StandardCharsets;
import java.util.TreeMap;

public class LiveSiteRxHtmlResult implements Measurable {
  public final byte[] html;
  private final Table table;
  private final long _measure;

  public LiveSiteRxHtmlResult(String html, Table table) {
    this.html = html.getBytes(StandardCharsets.UTF_8);
    this.table = table;
    long m = html.length();
    _measure = m + table.memory();
  }

  public boolean test(String uri) {
    return table.route(uri, new TreeMap<>()) != null;
  }

  @Override
  public long measure() {
    return _measure;
  }
}
