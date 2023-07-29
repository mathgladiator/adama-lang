/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.rxhtml;

import org.adamalang.runtime.sys.web.WebFragment;
import org.adamalang.runtime.sys.web.WebPath;
import org.adamalang.rxhtml.template.Shell;

import java.util.ArrayList;
import java.util.HashMap;

/** result of executing RxHtml */
public class RxHtmlResult {
  public final String javascript;
  public final String style;
  public final Shell shell;
  public final ArrayList<WebPath> paths;
  public final HashMap<String, Integer> cssFreq;

  public RxHtmlResult(String javascript, String style, Shell shell, ArrayList<String> patterns, HashMap<String, Integer> cssFreq) {
    this.javascript = javascript;
    this.style = style;
    this.shell = shell;
    this.paths = new ArrayList<>();
    for (String pattern : patterns) {
      paths.add(new WebPath(pattern));
    }
    this.cssFreq = cssFreq;
  }

  public boolean test(String rawUri) {
    String uri = rawUri;
    // truncate a trailing "/"
    while (uri.length() > 1 && uri.endsWith("/")) {
      uri = uri.substring(0, uri.length() - 1);
    }
    WebPath test = new WebPath(uri);
    for (WebPath path : paths) {
      if (test.size() == path.size()) {
        boolean good = true;
        for (int k = 0; k < test.size() && good; k++) {
          WebFragment t = test.at(k);
          WebFragment p = path.at(k);
          if (!p.fragment.startsWith("$")) {
            if (!p.fragment.equals(t.fragment)) {
              good = false;
            }
          }
        }
        if (good) {
          return true;
        }
      }
    }
    return false;
  }

  @Override
  public String toString() {
    return "JavaScript:" + javascript.trim() + "\nStyle:" + style.trim() + "\nShell:" + shell.makeShell(this);
  }
}
