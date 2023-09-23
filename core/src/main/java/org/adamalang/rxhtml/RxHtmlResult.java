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
package org.adamalang.rxhtml;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.runtime.sys.web.WebFragment;
import org.adamalang.runtime.sys.web.WebPath;
import org.adamalang.runtime.sys.web.rxhtml.LiveSiteRxHtmlResult;
import org.adamalang.rxhtml.template.Shell;
import org.adamalang.rxhtml.template.Task;

import java.util.ArrayList;
import java.util.HashMap;

/** result of executing RxHtml */
public class RxHtmlResult {
  public final String javascript;
  public final String style;
  public final Shell shell;
  public final ArrayList<WebPath> paths;
  public final HashMap<String, Integer> cssFreq;
  public final ArrayList<Task> tasks;
  public final ObjectNode viewSchema;

  public RxHtmlResult(String javascript, String style, Shell shell, ArrayList<String> patterns, HashMap<String, Integer> cssFreq, ArrayList<Task> tasks, ObjectNode viewSchema) {
    this.javascript = javascript;
    this.style = style;
    this.shell = shell;
    this.paths = new ArrayList<>();
    for (String pattern : patterns) {
      paths.add(new WebPath(pattern));
    }
    this.cssFreq = cssFreq;
    this.tasks = tasks;
    this.viewSchema = viewSchema;
  }

  public boolean test(String rawUri) {
    return testUri(paths, rawUri);
  }

  public static boolean testUri(ArrayList<WebPath> paths, String rawUri) {
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
