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
package org.adamalang.runtime.sys.web.rxhtml;

import org.adamalang.common.cache.Measurable;
import org.adamalang.runtime.sys.web.WebPath;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class LiveSiteRxHtmlResult implements Measurable {
  public final byte[] html;
  private final ArrayList<WebPath> paths;
  private final long _measure;

  public LiveSiteRxHtmlResult(String html, ArrayList<WebPath> paths) {
    this.html = html.getBytes(StandardCharsets.UTF_8);
    this.paths = paths;
    long m = html.length();
    for (WebPath path : paths) {
      m += path.measure();
    }
    _measure = m + 1024;
  }

  public boolean test(String uri) {
    return RxHtmlResult.testUri(paths, uri);
  }

  @Override
  public long measure() {
    return _measure;
  }
}
