/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.rxhtml;

import org.adamalang.runtime.sys.web.WebFragment;
import org.adamalang.runtime.sys.web.WebPath;
import org.adamalang.rxhtml.template.Shell;

import java.util.ArrayList;

/** result of executing RxHtml */
public class RxHtmlResult {
  public final String javascript;
  public final String style;
  public final Shell shell;
  public final ArrayList<WebPath> paths;

  public RxHtmlResult(String javascript, String style, Shell shell, ArrayList<String> patterns) {
    this.javascript = javascript;
    this.style = style;
    this.shell = shell;
    this.paths = new ArrayList<>();
    for (String pattern : patterns) {
      paths.add(new WebPath(pattern));
    }
  }

  public boolean test(String uri) {
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
    return "JavaScript:" + javascript.trim() + "\nStyle:" + style.trim() + "\nShell:" + shell.makeShell(this, false);
  }
}
