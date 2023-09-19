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

import org.adamalang.rxhtml.template.config.Feedback;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/** entry point for the type checker */
public class TypeChecker {
  /** Given a bundled forest, produce feedback for the developer */
  public static void typecheck(String forest, Feedback feedback) {
    Document document = Jsoup.parse(forest);
    warnDuplicatePages(document, feedback);
    warnDuplicateTemplates(document, feedback);
    warnTemplateNotFound(document, feedback);
  }

  public static void warnDuplicatePages(Document document, Feedback feedback) {
  }

  public static void warnDuplicateTemplates(Document document, Feedback feedback) {
  }

  public static void warnTemplateNotFound(Document document, Feedback feedback) {
  }
}
