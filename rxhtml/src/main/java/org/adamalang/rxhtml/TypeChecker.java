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
package org.adamalang.rxhtml;

import org.adamalang.rxhtml.routing.Instructions;
import org.adamalang.rxhtml.template.config.Feedback;
import org.adamalang.rxhtml.typing.RxRootEnvironment;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
import java.util.TreeSet;

/** entry point for the type checker */
public class TypeChecker {
  /** Given a bundled forest, produce feedback for the developer */
  public static void typecheck(String forest, File input, Feedback feedback) {
    Document document = Loader.parseForest(forest, feedback, ProductionMode.Web);
    warnDuplicatePages(document, feedback);
    if (input != null && input.exists() && input.isDirectory()) {
      RxRootEnvironment env = new RxRootEnvironment(forest, input, feedback);
      env.check();
    }
  }

  public static void warnDuplicatePages(Document document, Feedback feedback) {
    TreeSet<String> paths = new TreeSet<>();
    for (Element element : document.getElementsByTag("page")) {
      if (!element.hasAttr("uri")) {
        feedback.warn(element, "page is missing a uri");
        continue;
      }
      String normalizedUri = Instructions.parse(element.attr("uri")).normalized;
      if (paths.contains(normalizedUri)) {
        feedback.warn(element, "page has duplicate path of '" + normalizedUri + "'");
      }
      paths.add(normalizedUri);
    }
  }
}
