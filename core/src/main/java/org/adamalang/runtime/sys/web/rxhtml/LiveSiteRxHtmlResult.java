/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.runtime.sys.web.rxhtml;

import org.adamalang.common.cache.Measurable;
import org.adamalang.runtime.sys.web.WebPath;
import org.adamalang.rxhtml.RxHtmlResult;

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
