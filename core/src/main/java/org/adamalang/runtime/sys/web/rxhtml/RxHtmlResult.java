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

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.runtime.sys.web.WebFragment;
import org.adamalang.runtime.sys.web.WebPath;
import org.adamalang.runtime.sys.web.rxhtml.LiveSiteRxHtmlResult;
import org.adamalang.rxhtml.RxHtmlBundle;
import org.adamalang.rxhtml.routing.Table;
import org.adamalang.rxhtml.template.Shell;
import org.adamalang.rxhtml.template.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

/** result of executing RxHtml */
public class RxHtmlResult {
  public final RxHtmlBundle bundle;
  public final String javascript;
  public final String style;
  public final Shell shell;
  public final HashMap<String, Integer> cssFreq;
  public final ArrayList<Task> tasks;
  public final ObjectNode viewSchema;
  public final Table table;

  public RxHtmlResult(RxHtmlBundle bundle) {
    this.bundle = bundle;
    this.javascript = bundle.javascript;
    this.style = bundle.style;
    this.shell = bundle.shell;
    this.table = bundle.table;
    this.cssFreq = bundle.cssFreq;
    this.tasks = bundle.tasks;
    this.viewSchema = bundle.viewSchema;
  }

  public boolean test(String uri) {
    return table.route(uri, new TreeMap<>()) != null;
  }
}
